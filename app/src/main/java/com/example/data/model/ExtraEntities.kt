package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val gameId: String,
    val targetScore: Int,
    val currentProgress: Int = 0,
    val rewardPoints: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val iconKey: String = "trophy"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val message: String,
    val type: String, // "challenge", "reward", "game", "system"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class LeaderboardUser(
    val rank: Int,
    val username: String,
    val avatarId: Int,
    val points: Int,
    val badge: String,
    val isCurrentUser: Boolean = false
)
