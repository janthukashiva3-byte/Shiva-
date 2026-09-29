package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

data class CyberPillar(
    val id: Long,
    var xRatio: Float,
    val gapCenterYRatio: Float, // 0.25 to 0.75
    val gapHeightRatio: Float = 0.30f,
    var scored: Boolean = false
)

@Composable
fun FlappyBirdGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var droneYRatio by remember { mutableFloatStateOf(0.5f) }
    var velocityY by remember { mutableFloatStateOf(0f) }
    val gravity = 0.0016f
    val jumpStrength = -0.024f

    var pillars by remember { mutableStateOf(listOf<CyberPillar>()) }
    var nextPillarId by remember { mutableStateOf(0L) }
    var score by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var gameStarted by remember { mutableStateOf(false) }

    fun flap() {
        if (isGameOver || isPaused) return
        if (!gameStarted) gameStarted = true
        velocityY = jumpStrength
    }

    // Physics & Game Loop
    LaunchedEffect(isPaused, isGameOver, gameStarted) {
        var tickCounter = 0
        while (!isPaused && !isGameOver && gameStarted) {
            delay(28L)
            tickCounter++

            // Apply gravity
            velocityY += gravity
            droneYRatio += velocityY

            // Floor & Ceiling collision
            if (droneYRatio < 0.05f) {
                droneYRatio = 0.05f
                velocityY = 0f
            }
            if (droneYRatio > 0.95f) {
                isGameOver = true
                onFinishGame(score * 10)
                break
            }

            // Spawn pillars every 65 ticks
            if (tickCounter % 65 == 0) {
                val gapCenter = Random.nextFloat() * 0.45f + 0.28f
                pillars = pillars + CyberPillar(nextPillarId++, 1.1f, gapCenter)
            }

            // Move pillars & check collisions
            val pillarSpeed = 0.014f
            val updated = mutableListOf<CyberPillar>()
            val droneX = 0.25f // drone fixed horizontal position

            var hitPillar = false

            for (p in pillars) {
                p.xRatio -= pillarSpeed

                // Score check
                if (!p.scored && p.xRatio < droneX) {
                    p.scored = true
                    score++
                }

                // Collision Check (Pillar width is ~0.14)
                val pWidth = 0.14f
                if (droneX in (p.xRatio - 0.04f)..(p.xRatio + pWidth + 0.04f)) {
                    val halfGap = p.gapHeightRatio / 2f
                    val topPillarBottom = p.gapCenterYRatio - halfGap
                    val bottomPillarTop = p.gapCenterYRatio + halfGap

                    // Drone touches upper pillar or lower pillar
                    if (droneYRatio - 0.035f < topPillarBottom || droneYRatio + 0.035f > bottomPillarTop) {
                        hitPillar = true
                    }
                }

                if (p.xRatio > -0.2f) {
                    updated.add(p)
                }
            }
            pillars = updated

            if (hitPillar) {
                isGameOver = true
                onFinishGame(score * 10)
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
                Text("PILLARS CLEARED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("$score", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonCyan)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("POINTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("+${score * 10} PTS", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonYellow)
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

        // Tap-to-Flap Game Field Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF090D1A))
                .border(2.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { flap() }
                .testTag("flappy_tap_area"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw background grid stars
                for (i in 0 until 18) {
                    val sx = (w * (i * 0.055f + (score * 0.02f) % 0.1f)) % w
                    val sy = (h * ((i * 37) % 100) / 100f)
                    drawCircle(color = Color.White.copy(alpha = 0.25f), radius = 2f, center = Offset(sx, sy))
                }

                // Draw Pillars
                val pWidth = w * 0.14f
                for (p in pillars) {
                    val px = p.xRatio * w
                    val halfGap = (p.gapHeightRatio / 2f) * h
                    val gapCenterY = p.gapCenterYRatio * h

                    // Top Pillar
                    val topH = gapCenterY - halfGap
                    drawRoundRect(
                        color = NeonPurple,
                        topLeft = Offset(px, 0f),
                        size = Size(pWidth, topH),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    // Pillar Border Glow
                    drawRoundRect(
                        color = NeonCyan,
                        topLeft = Offset(px, topH - 12f),
                        size = Size(pWidth, 12f),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )

                    // Bottom Pillar
                    val bottomY = gapCenterY + halfGap
                    drawRoundRect(
                        color = NeonPurple,
                        topLeft = Offset(px, bottomY),
                        size = Size(pWidth, h - bottomY),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                    drawRoundRect(
                        color = NeonCyan,
                        topLeft = Offset(px, bottomY),
                        size = Size(pWidth, 12f),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }

                // Draw Cyber Drone Player
                val droneX = 0.25f * w
                val droneY = droneYRatio * h
                val droneRadius = 16.dp.toPx()

                // Glow ring
                drawCircle(
                    color = NeonPink.copy(alpha = 0.35f),
                    radius = droneRadius * 1.5f,
                    center = Offset(droneX, droneY)
                )
                // Core
                drawCircle(
                    color = NeonCyan,
                    radius = droneRadius,
                    center = Offset(droneX, droneY)
                )
                // Drone eye
                drawCircle(
                    color = Color.White,
                    radius = droneRadius * 0.4f,
                    center = Offset(droneX + 4f, droneY)
                )
            }

            if (!gameStarted && !isGameOver) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurface.copy(alpha = 0.9f))
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    Text("TAP TO FLY!", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Avoid the electric pillars", fontSize = 12.sp, color = TextSecondary)
                }
            }

            if (isPaused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CyberBackground.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PAUSED", fontSize = 22.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                }
            }
        }

        // Tap Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberSurfaceVariant)
                    .border(1.5.dp, NeonCyan, RoundedCornerShape(14.dp))
                    .clickable { flap() }
                    .testTag("flappy_flap_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TAP SCREEN OR HERE TO FLAP 🚀",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = NeonCyan
                )
            }
        }
    }
}
