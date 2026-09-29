package com.example.ui.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Obstacle(val id: Long, val lane: Int, var yRatio: Float, val isCoin: Boolean = false)

@Composable
fun RacingGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var playerLane by remember { mutableIntStateOf(1) } // 0: Left, 1: Center, 2: Right
    var obstacles by remember { mutableStateOf(listOf<Obstacle>()) }
    var score by remember { mutableIntStateOf(0) }
    var coinsCollected by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var roadStripeOffset by remember { mutableFloatStateOf(0f) }
    var nextObstacleId by remember { mutableStateOf(0L) }
    var hasNitro by remember { mutableStateOf(false) }
    var nitroTimeRemaining by remember { mutableIntStateOf(0) }

    val animatedLaneX by animateFloatAsState(
        targetValue = playerLane.toFloat(),
        animationSpec = spring(stiffness = 500f),
        label = "laneX"
    )

    // Game loop
    LaunchedEffect(isPaused, isGameOver) {
        var tickCounter = 0
        while (!isPaused && !isGameOver) {
            delay(35L)
            tickCounter++
            score += 1
            roadStripeOffset = (roadStripeOffset + 0.08f) % 1f

            if (hasNitro) {
                nitroTimeRemaining -= 35
                if (nitroTimeRemaining <= 0) hasNitro = false
            }

            // Spawn obstacles
            if (tickCounter % 35 == 0) {
                val spawnLane = Random.nextInt(0, 3)
                val isCoin = Random.nextInt(3) == 0
                obstacles = obstacles + Obstacle(nextObstacleId++, spawnLane, -0.1f, isCoin)
            }

            // Move obstacles
            val speed = if (hasNitro) 0.035f else 0.022f + (score / 15000f)
            val updated = mutableListOf<Obstacle>()
            var hit = false

            for (obs in obstacles) {
                obs.yRatio += speed
                // Collision check (Player is around yRatio 0.78 to 0.90)
                if (obs.yRatio in 0.74f..0.88f && obs.lane == playerLane) {
                    if (obs.isCoin) {
                        coinsCollected++
                        score += 30
                    } else {
                        if (!hasNitro) {
                            hit = true
                        }
                    }
                } else if (obs.yRatio < 1.1f) {
                    updated.add(obs)
                }
            }
            obstacles = updated

            if (hit) {
                isGameOver = true
                val finalScore = score + (coinsCollected * 25)
                onFinishGame(finalScore)
                break
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("DISTANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("${score}m", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NeonCyan)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("COINS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("🪙 $coinsCollected", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldColor)
            }
            IconButton(
                onClick = { isPaused = !isPaused },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
            ) {
                Icon(
                    imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = NeonCyan
                )
            }
        }

        // Highway Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F121E))
                .border(2.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val laneW = w / 3f

                // Draw lane divider lines
                for (laneDiv in 1..2) {
                    val x = laneDiv * laneW
                    var y = (roadStripeOffset * 60f) % 60f
                    while (y < h) {
                        drawLine(
                            color = NeonPurple.copy(alpha = 0.45f),
                            start = Offset(x, y),
                            end = Offset(x, y + 28f),
                            strokeWidth = 3.dp.toPx()
                        )
                        y += 60f
                    }
                }

                // Draw Obstacles / Traffic & Coins
                for (obs in obstacles) {
                    val cx = (obs.lane + 0.5f) * laneW
                    val cy = obs.yRatio * h
                    if (obs.isCoin) {
                        drawCircle(
                            color = GoldColor,
                            radius = 14.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = NeonYellow,
                            radius = 8.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    } else {
                        // Traffic Car
                        val carW = laneW * 0.55f
                        val carH = h * 0.08f
                        drawRoundRect(
                            color = NeonPink,
                            topLeft = Offset(cx - carW / 2, cy - carH / 2),
                            size = Size(carW, carH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                        // Windshield
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.8f),
                            topLeft = Offset(cx - carW * 0.35f, cy),
                            size = Size(carW * 0.7f, carH * 0.3f),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }

                // Draw Player Cyber Car
                val playerX = (animatedLaneX + 0.5f) * laneW
                val playerY = h * 0.82f
                val pCarW = laneW * 0.58f
                val pCarH = h * 0.085f

                // Glow shadow if nitro
                if (hasNitro) {
                    drawRoundRect(
                        color = NeonCyan.copy(alpha = 0.5f),
                        topLeft = Offset(playerX - pCarW * 0.6f, playerY - pCarH * 0.6f),
                        size = Size(pCarW * 1.2f, pCarH * 1.2f),
                        cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                    )
                }

                drawRoundRect(
                    color = if (hasNitro) NeonYellow else NeonCyan,
                    topLeft = Offset(playerX - pCarW / 2, playerY - pCarH / 2),
                    size = Size(pCarW, pCarH),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
                // Headlights
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(playerX - pCarW * 0.32f, playerY - pCarH * 0.4f))
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(playerX + pCarW * 0.32f, playerY - pCarH * 0.4f))
            }

            if (isPaused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CyberBackground.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GAME PAUSED", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                }
            }
        }

        // Steer Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (playerLane > 0) playerLane-- },
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .border(2.dp, NeonCyan, CircleShape)
                    .testTag("racing_btn_left")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Steer Left", tint = NeonCyan, modifier = Modifier.size(36.dp))
            }

            // Nitro Boost Button
            IconButton(
                onClick = {
                    if (!hasNitro) {
                        hasNitro = true
                        nitroTimeRemaining = 3000
                    }
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (hasNitro) NeonYellow else CyberSurfaceVariant)
                    .border(1.5.dp, GoldColor, CircleShape)
                    .testTag("racing_btn_nitro")
            ) {
                Icon(Icons.Default.Bolt, contentDescription = "Nitro", tint = if (hasNitro) CyberBackground else GoldColor, modifier = Modifier.size(28.dp))
            }

            IconButton(
                onClick = { if (playerLane < 2) playerLane++ },
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .border(2.dp, NeonCyan, CircleShape)
                    .testTag("racing_btn_right")
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Steer Right", tint = NeonCyan, modifier = Modifier.size(36.dp))
            }
        }
    }
}
