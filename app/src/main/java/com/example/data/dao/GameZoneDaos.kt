package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyChallengeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.GameStatsEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)
}

@Dao
interface GameDao {
    @Query("SELECT * FROM game_stats ORDER BY isFeatured DESC, title ASC")
    fun getAllGames(): Flow<List<GameStatsEntity>>

    @Query("SELECT * FROM game_stats WHERE gameId = :gameId")
    fun getGameStats(gameId: String): Flow<GameStatsEntity?>

    @Query("SELECT * FROM game_stats WHERE gameId = :gameId")
    suspend fun getGameStatsOnce(gameId: String): GameStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGames(games: List<GameStatsEntity>)

    @Update
    suspend fun updateGame(game: GameStatsEntity)

    @Query("UPDATE game_stats SET isEnabled = :enabled WHERE gameId = :gameId")
    suspend fun setGameEnabled(gameId: String, enabled: Boolean)

    @Query("UPDATE game_stats SET isFeatured = :featured WHERE gameId = :gameId")
    suspend fun setGameFeatured(gameId: String, featured: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameRecord(record: GameRecordEntity)

    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT 20")
    fun getRecentRecords(): Flow<List<GameRecordEntity>>

    @Query("SELECT SUM(timesPlayed) FROM game_stats")
    fun getTotalGamesPlayed(): Flow<Int?>

    @Query("SELECT MAX(highScore) FROM game_stats")
    fun getMaxHighScore(): Flow<Int?>
}

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM daily_challenges")
    fun getAllChallenges(): Flow<List<DailyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<DailyChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: DailyChallengeEntity)

    @Query("UPDATE daily_challenges SET currentProgress = :progress, isCompleted = :completed WHERE id = :id")
    suspend fun updateProgress(id: Int, progress: Int, completed: Boolean)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}
