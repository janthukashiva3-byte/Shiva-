package com.example.ui.games

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameStatsEntity
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GameScreenHost(
    gameId: String,
    gameStats: GameStatsEntity?,
    onBack: () -> Unit,
    onFinishGame: (gameId: String, gameTitle: String, score: Int, prevHighScore: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val title = gameStats?.title ?: gameId.replaceFirstChar { it.uppercase() }
    val highScore = gameStats?.highScore ?: 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
    ) {
        // Game Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                        .testTag("game_host_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "BEST: $highScore",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldColor
                    )
                }
            }

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .testTag("game_host_close_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Active Game Composable
        Box(modifier = Modifier.fillMaxSize()) {
            when (gameId) {
                "tictactoe" -> TicTacToeGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "snake" -> SnakeGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "ludo" -> LudoGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "racing" -> RacingGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "puzzle2048" -> Puzzle2048Game(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "memory" -> MemoryMatchGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "quiz" -> QuizGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                "flappy" -> FlappyBirdGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
                else -> SnakeGame(
                    onFinishGame = { score -> onFinishGame(gameId, title, score, highScore) }
                )
            }
        }
    }
}
