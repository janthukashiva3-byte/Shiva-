package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "CyberStriker",
    val email: String = "player@gamezone.com",
    val phone: String = "+1 555-0199",
    val avatarId: Int = 0,
    val points: Int = 1250,
    val xp: Int = 850,
    val level: Int = 3,
    val streakDays: Int = 4,
    val lastRewardClaimTime: Long = 0L,
    val bio: String = "Casual gamer aiming for diamond tier! 🎮⚡",
    val favoriteGame: String = "Cyber Flap",
    val isAdmin: Boolean = true
)
