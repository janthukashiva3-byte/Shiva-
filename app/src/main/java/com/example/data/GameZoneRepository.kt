package com.example.data

import com.example.data.model.AchievementEntity
import com.example.data.model.DailyChallengeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.GameStatsEntity
import com.example.data.model.LeaderboardUser
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class GameZoneRepository(private val db: GameZoneDatabase) {

    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()
    val allGames: Flow<List<GameStatsEntity>> = db.gameDao().getAllGames()
    val recentRecords: Flow<List<GameRecordEntity>> = db.gameDao().getRecentRecords()
    val dailyChallenges: Flow<List<DailyChallengeEntity>> = db.challengeDao().getAllChallenges()
    val achievements: Flow<List<AchievementEntity>> = db.achievementDao().getAllAchievements()
    val notifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = db.notificationDao().getUnreadCount()

    suspend fun saveGameResult(gameId: String, score: Int): Int {
        val user = db.userProfileDao().getUserProfileOnce() ?: UserProfileEntity()
        val stats = db.gameDao().getGameStatsOnce(gameId)

        // Points earned formula: base points + performance bonus
        val pointsEarned = maxOf(10, score / 2)
        val xpEarned = maxOf(15, score / 3)

        // Update Game stats
        val newHighScore = if (stats != null) maxOf(stats.highScore, score) else score
        val newTimesPlayed = (stats?.timesPlayed ?: 0) + 1

        val updatedStats = stats?.copy(
            highScore = newHighScore,
            timesPlayed = newTimesPlayed,
            lastPlayedTime = System.currentTimeMillis()
        ) ?: GameStatsEntity(
            gameId = gameId,
            title = gameId.replaceFirstChar { it.uppercase() },
            category = "Arcade",
            description = "Casual mini game",
            highScore = newHighScore,
            timesPlayed = newTimesPlayed,
            lastPlayedTime = System.currentTimeMillis()
        )
        db.gameDao().insertGames(listOf(updatedStats))

        // Record history
        db.gameDao().insertGameRecord(
            GameRecordEntity(
                gameId = gameId,
                gameTitle = updatedStats.title,
                score = score,
                pointsEarned = pointsEarned
            )
        )

        // Update user points and XP / Level
        val newPoints = user.points + pointsEarned
        val newXp = user.xp + xpEarned
        val newLevel = 1 + (newXp / 500) // Level up every 500 XP

        db.userProfileDao().insertOrUpdate(
            user.copy(
                points = newPoints,
                xp = newXp,
                level = newLevel
            )
        )

        // Check if daily challenges progress
        val challenges = db.challengeDao().getAllChallenges().firstOrNull() ?: emptyList()
        for (challenge in challenges) {
            if (challenge.gameId == gameId && !challenge.isCompleted) {
                val newProgress = maxOf(challenge.currentProgress, score)
                val completed = newProgress >= challenge.targetScore
                db.challengeDao().updateProgress(challenge.id, newProgress, completed)
                if (completed) {
                    db.notificationDao().insertNotification(
                        NotificationEntity(
                            title = "🎯 Challenge Completed!",
                            message = "You completed '${challenge.title}'. Claim ${challenge.rewardPoints} PTS now!",
                            type = "challenge"
                        )
                    )
                }
            }
        }

        return pointsEarned
    }

    suspend fun claimChallenge(challengeId: Int): Boolean {
        val challenges = db.challengeDao().getAllChallenges().firstOrNull() ?: emptyList()
        val challenge = challenges.find { it.id == challengeId } ?: return false
        if (!challenge.isCompleted || challenge.isClaimed) return false

        db.challengeDao().updateChallenge(challenge.copy(isClaimed = true))
        val user = db.userProfileDao().getUserProfileOnce() ?: return false
        db.userProfileDao().insertOrUpdate(
            user.copy(
                points = user.points + challenge.rewardPoints,
                xp = user.xp + (challenge.rewardPoints / 2)
            )
        )
        return true
    }

    suspend fun claimDailyStreakReward(): Int {
        val user = db.userProfileDao().getUserProfileOnce() ?: return 0
        val currentStreak = if (user.streakDays >= 7) 1 else user.streakDays + 1
        val rewardMultiplier = currentStreak
        val rewardPoints = 50 * rewardMultiplier

        db.userProfileDao().insertOrUpdate(
            user.copy(
                streakDays = currentStreak,
                lastRewardClaimTime = System.currentTimeMillis(),
                points = user.points + rewardPoints,
                xp = user.xp + 100
            )
        )

        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "🎁 Streak Day $currentStreak Claimed!",
                message = "You earned +$rewardPoints PTS and +100 XP!",
                type = "reward"
            )
        )
        return rewardPoints
    }

    suspend fun updateUserProfile(username: String, avatarId: Int, bio: String, favoriteGame: String) {
        val user = db.userProfileDao().getUserProfileOnce() ?: UserProfileEntity()
        db.userProfileDao().insertOrUpdate(
            user.copy(
                username = username,
                avatarId = avatarId,
                bio = bio,
                favoriteGame = favoriteGame
            )
        )
    }

    suspend fun markNotificationAsRead(id: Long) {
        db.notificationDao().markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        db.notificationDao().markAllAsRead()
    }

    // Admin functions
    suspend fun toggleGameEnabled(gameId: String, enabled: Boolean) {
        db.gameDao().setGameEnabled(gameId, enabled)
    }

    suspend fun toggleGameFeatured(gameId: String, featured: Boolean) {
        db.gameDao().setGameFeatured(gameId, featured)
    }

    suspend fun adminAwardPoints(points: Int) {
        val user = db.userProfileDao().getUserProfileOnce() ?: return
        db.userProfileDao().insertOrUpdate(
            user.copy(points = maxOf(0, user.points + points))
        )
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "⚡ Admin Points Adjustment",
                message = "System added $points PTS to your balance.",
                type = "system"
            )
        )
    }

    suspend fun sendSystemBroadcast(title: String, message: String) {
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = title,
                message = message,
                type = "system"
            )
        )
    }

    // Leaderboard generator that integrates current user profile
    fun getLeaderboard(type: String): Flow<List<LeaderboardUser>> {
        return userProfile.map { profile ->
            val userPoints = profile?.points ?: 1250
            val username = profile?.username ?: "You"
            val userAvatar = profile?.avatarId ?: 0

            val baseRoster = when (type) {
                "weekly" -> listOf(
                    Triple("ValkyrieX", 3120, 2),
                    Triple("PixelGhost", 2850, 1),
                    Triple("NeonStriker", userPoints, userAvatar),
                    Triple("ShadowNinja", 2390, 3),
                    Triple("BlazeFury", 2150, 4),
                    Triple("QuantumByte", 1920, 5),
                    Triple("CyberDragon", 1750, 0),
                    Triple("GlitchKing", 1600, 2),
                    Triple("StarLord", 1420, 1),
                    Triple("EchoKnight", 1290, 3)
                )
                "friends" -> listOf(
                    Triple("NeonStriker", userPoints, userAvatar),
                    Triple("Alex_Gamer", 2100, 3),
                    Triple("Sarah_Retro", 1880, 2),
                    Triple("DevDave", 1490, 4),
                    Triple("Maya_Pro", 1250, 1),
                    Triple("SamuraiJack", 920, 5)
                )
                else -> listOf( // global
                    Triple("ApexApex", 5420, 4),
                    Triple("NovaChampion", 4980, 3),
                    Triple("CyberSamurai", 4510, 2),
                    Triple("ValkyrieX", 3890, 1),
                    Triple("PixelGhost", 3420, 5),
                    Triple("NeonStriker", userPoints, userAvatar),
                    Triple("ShadowNinja", 2980, 0),
                    Triple("BlazeFury", 2740, 2),
                    Triple("QuantumByte", 2430, 4),
                    Triple("AeroPilot", 2210, 3),
                    Triple("GlitchKing", 1980, 1),
                    Triple("HyperDrive", 1850, 5)
                )
            }

            val sorted = baseRoster.sortedByDescending { it.second }
            sorted.mapIndexed { index, triple ->
                val isMe = triple.first == "NeonStriker" || triple.first == username
                LeaderboardUser(
                    rank = index + 1,
                    username = if (isMe) "$username (You)" else triple.first,
                    avatarId = triple.third,
                    points = if (isMe) userPoints else triple.second,
                    badge = when (index) {
                        0 -> "👑 Champion"
                        1 -> "⚡ Grandmaster"
                        2 -> "🔥 Master"
                        in 3..5 -> "💎 Diamond"
                        else -> "⭐ Gold"
                    },
                    isCurrentUser = isMe
                )
            }
        }
    }
}
