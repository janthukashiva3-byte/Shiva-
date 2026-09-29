package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey val gameId: String,
    val title: String,
    val category: String,
    val description: String,
    val timesPlayed: Int = 0,
    val highScore: Int = 0,
    val lastPlayedTime: Long = 0L,
    val isEnabled: Boolean = true,
    val isFeatured: Boolean = false,
    val iconName: String = "gamepad"
)

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val gameId: String,
    val gameTitle: String,
    val score: Int,
    val pointsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
