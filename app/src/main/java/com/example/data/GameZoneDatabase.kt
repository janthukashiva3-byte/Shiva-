package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AchievementDao
import com.example.data.dao.ChallengeDao
import com.example.data.dao.GameDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.UserProfileDao
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyChallengeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.GameStatsEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        GameStatsEntity::class,
        GameRecordEntity::class,
        DailyChallengeEntity::class,
        AchievementEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameZoneDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun gameDao(): GameDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun achievementDao(): AchievementDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: GameZoneDatabase? = null

        fun getInstance(context: Context): GameZoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameZoneDatabase::class.java,
                    "gamezone_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateDatabase(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(database: GameZoneDatabase) {
            // Seed User
            database.userProfileDao().insertOrUpdate(
                UserProfileEntity(
                    id = 1,
                    username = "NeonStriker",
                    email = "striker@gamezone.io",
                    phone = "+1 (555) 789-2048",
                    avatarId = 0,
                    points = 2450,
                    xp = 1800,
                    level = 4,
                    streakDays = 5,
                    bio = "Climbing the global ranks. Speedrunner & trivia enthusiast!",
                    favoriteGame = "Neon Highway",
                    isAdmin = true
                )
            )

            // Seed Games
            val games = listOf(
                GameStatsEntity(
                    gameId = "tictactoe",
                    title = "Tic-Tac-Toe",
                    category = "Strategy",
                    description = "Classic neon grid battle against smart AI or a friend.",
                    timesPlayed = 14,
                    highScore = 6,
                    isFeatured = false,
                    iconName = "grid"
                ),
                GameStatsEntity(
                    gameId = "snake",
                    title = "Snake Neon",
                    category = "Arcade",
                    description = "Maneuver the neon serpent, gather glow orbs, and grow.",
                    timesPlayed = 22,
                    highScore = 180,
                    isFeatured = true,
                    iconName = "snake"
                ),
                GameStatsEntity(
                    gameId = "ludo",
                    title = "Quick Ludo",
                    category = "Board",
                    description = "Roll the dice, race tokens home, and knock out AI rivals.",
                    timesPlayed = 9,
                    highScore = 4,
                    isFeatured = false,
                    iconName = "dice"
                ),
                GameStatsEntity(
                    gameId = "racing",
                    title = "Neon Highway",
                    category = "Racing",
                    description = "High-speed cyber racer. Dodge oncoming vehicles & grab nitro.",
                    timesPlayed = 31,
                    highScore = 420,
                    isFeatured = true,
                    iconName = "car"
                ),
                GameStatsEntity(
                    gameId = "puzzle2048",
                    title = "2048 Cyber",
                    category = "Puzzle",
                    description = "Slide and merge numbers to reach the legendary 2048 tile.",
                    timesPlayed = 18,
                    highScore = 2048,
                    isFeatured = true,
                    iconName = "puzzle"
                ),
                GameStatsEntity(
                    gameId = "memory",
                    title = "Memory Match",
                    category = "Puzzle",
                    description = "Test your visual recall! Match pairs before the clock expires.",
                    timesPlayed = 11,
                    highScore = 800,
                    isFeatured = false,
                    iconName = "memory"
                ),
                GameStatsEntity(
                    gameId = "quiz",
                    title = "Gamer Trivia",
                    category = "Trivia",
                    description = "Put your gaming lore to the test with rapid-fire questions.",
                    timesPlayed = 16,
                    highScore = 950,
                    isFeatured = false,
                    iconName = "quiz"
                ),
                GameStatsEntity(
                    gameId = "flappy",
                    title = "Cyber Flap",
                    category = "Action",
                    description = "Guide your drone between electric cyber pillars.",
                    timesPlayed = 28,
                    highScore = 34,
                    isFeatured = true,
                    iconName = "flight"
                )
            )
            database.gameDao().insertGames(games)

            // Seed Daily Challenges
            val challenges = listOf(
                DailyChallengeEntity(
                    id = 1,
                    title = "Speed Racer",
                    description = "Score 250+ meters in Neon Highway",
                    gameId = "racing",
                    targetScore = 250,
                    currentProgress = 180,
                    rewardPoints = 200,
                    isCompleted = false,
                    isClaimed = false
                ),
                DailyChallengeEntity(
                    id = 2,
                    title = "Cyber Aviator",
                    description = "Pass 15 pillars in Cyber Flap",
                    gameId = "flappy",
                    targetScore = 15,
                    currentProgress = 15,
                    rewardPoints = 150,
                    isCompleted = true,
                    isClaimed = false
                ),
                DailyChallengeEntity(
                    id = 3,
                    title = "Serpent Feast",
                    description = "Collect 10 glow orbs in Snake Neon",
                    gameId = "snake",
                    targetScore = 10,
                    currentProgress = 6,
                    rewardPoints = 120,
                    isCompleted = false,
                    isClaimed = false
                ),
                DailyChallengeEntity(
                    id = 4,
                    title = "Mind Master",
                    description = "Clear Memory Match within 60s",
                    gameId = "memory",
                    targetScore = 1,
                    currentProgress = 0,
                    rewardPoints = 180,
                    isCompleted = false,
                    isClaimed = false
                )
            )
            database.challengeDao().insertChallenges(challenges)

            // Seed Achievements
            val achievements = listOf(
                AchievementEntity(
                    id = "first_win",
                    title = "First Blood",
                    description = "Play and complete your first match on GameZone",
                    rewardPoints = 100,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 86400000L,
                    iconKey = "trophy"
                ),
                AchievementEntity(
                    id = "highway_champion",
                    title = "Apex Racer",
                    description = "Exceed 300 score in Neon Highway",
                    rewardPoints = 250,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 43200000L,
                    iconKey = "speed"
                ),
                AchievementEntity(
                    id = "2048_master",
                    title = "Matrix Fusion",
                    description = "Reach the 2048 tile in 2048 Cyber",
                    rewardPoints = 500,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 21600000L,
                    iconKey = "grid"
                ),
                AchievementEntity(
                    id = "trivia_whiz",
                    title = "Lore Keeper",
                    description = "Score 800+ in Gamer Trivia without lifelines",
                    rewardPoints = 300,
                    isUnlocked = false,
                    iconKey = "star"
                ),
                AchievementEntity(
                    id = "flap_legend",
                    title = "Gravity Defier",
                    description = "Pass 30 pillars in Cyber Flap",
                    rewardPoints = 350,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - 10800000L,
                    iconKey = "shield"
                ),
                AchievementEntity(
                    id = "streak_week",
                    title = "Unstoppable",
                    description = "Maintain a 7-day daily login streak",
                    rewardPoints = 400,
                    isUnlocked = false,
                    iconKey = "fire"
                )
            )
            database.achievementDao().insertAchievements(achievements)

            // Seed Notifications
            val notifications = listOf(
                NotificationEntity(
                    title = "⚡ Daily Streak Bonus Ready!",
                    message = "You're on day 5 of your streak. Claim your 250 PTS bonus now!",
                    type = "reward",
                    isRead = false
                ),
                NotificationEntity(
                    title = "🏆 Weekly Tournament Live",
                    message = "Compete in Cyber Flap & Neon Highway to rank on the Global Podium.",
                    type = "game",
                    isRead = false
                ),
                NotificationEntity(
                    title = "🎯 New Challenge Available",
                    message = "Earn 200 PTS in today's Speed Racer challenge.",
                    type = "challenge",
                    isRead = true
                )
            )
            for (notification in notifications) {
                database.notificationDao().insertNotification(notification)
            }
        }
    }
}
