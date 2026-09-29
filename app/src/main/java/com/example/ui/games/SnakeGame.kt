package com.example.ui.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }
data class Point(val x: Int, val y: Int)

@Composable
fun SnakeGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridSize = 16
    var snake by remember {
        mutableStateOf(listOf(Point(7, 8), Point(6, 8), Point(5, 8)))
    }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var nextDirection by remember { mutableStateOf(Direction.RIGHT) }
    var food by remember { mutableStateOf(Point(11, 8)) }
    var bonusStar by remember { mutableStateOf<Point?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var speedDelay by remember { mutableStateOf(160L) }

    fun generateFood(): Point {
        val occupied = snake.toSet()
        val free = mutableListOf<Point>()
        for (x in 0 until gridSize) {
            for (y in 0 until gridSize) {
                val pt = Point(x, y)
                if (pt !in occupied) free.add(pt)
            }
        }
        return if (free.isNotEmpty()) free.random() else Point(0, 0)
    }

    fun restart() {
        snake = listOf(Point(7, 8), Point(6, 8), Point(5, 8))
        direction = Direction.RIGHT
        nextDirection = Direction.RIGHT
        food = Point(11, 8)
        bonusStar = null
        score = 0
        speedDelay = 160L
        isGameOver = false
        isPaused = false
    }

    // Game Loop
    LaunchedEffect(isPaused, isGameOver) {
        while (!isPaused && !isGameOver) {
            delay(speedDelay)
            direction = nextDirection
            val head = snake.first()
            val newHead = when (direction) {
                Direction.UP -> Point(head.x, head.y - 1)
                Direction.DOWN -> Point(head.x, head.y + 1)
                Direction.LEFT -> Point(head.x - 1, head.y)
                Direction.RIGHT -> Point(head.x + 1, head.y)
            }

            // Wall Collision
            if (newHead.x < 0 || newHead.x >= gridSize || newHead.y < 0 || newHead.y >= gridSize) {
                isGameOver = true
                onFinishGame(score)
                break
            }

            // Self Collision
            if (snake.contains(newHead)) {
                isGameOver = true
                onFinishGame(score)
                break
            }

            // Check food collision
            val ateFood = (newHead == food)
            val ateBonus = (newHead == bonusStar)

            val newSnake = mutableListOf(newHead)
            if (ateFood || ateBonus) {
                newSnake.addAll(snake)
                if (ateFood) {
                    score += 15
                    food = generateFood()
                    if (Random.nextInt(5) == 0 && bonusStar == null) {
                        bonusStar = generateFood()
                    }
                    if (speedDelay > 70L) speedDelay -= 3L
                }
                if (ateBonus) {
                    score += 50
                    bonusStar = null
                }
            } else {
                newSnake.addAll(snake.dropLast(1))
            }
            snake = newSnake
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
        // Top Stats & Pause
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
                Text("SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("$score", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonGreen)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LENGTH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("${snake.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            }
            IconButton(
                onClick = { isPaused = !isPaused },
                modifier = Modifier
                    .size(42.dp)
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

        // Game Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(2.dp, CyberCardBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellW = size.width / gridSize
                val cellH = size.height / gridSize

                // Draw background cyber grid dots
                for (gx in 0 until gridSize) {
                    for (gy in 0 until gridSize) {
                        drawCircle(
                            color = Color(0xFF1E2644).copy(alpha = 0.4f),
                            radius = 1.5.dp.toPx(),
                            center = Offset((gx + 0.5f) * cellW, (gy + 0.5f) * cellH)
                        )
                    }
                }

                // Food
                drawCircle(
                    color = NeonPink,
                    radius = (cellW / 2.2f),
                    center = Offset((food.x + 0.5f) * cellW, (food.y + 0.5f) * cellH)
                )

                // Bonus Star
                bonusStar?.let {
                    drawCircle(
                        color = GoldColor,
                        radius = (cellW / 2f),
                        center = Offset((it.x + 0.5f) * cellW, (it.y + 0.5f) * cellH)
                    )
                }

                // Snake body
                snake.forEachIndexed { idx, pt ->
                    val color = if (idx == 0) NeonCyan else NeonGreen
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(pt.x * cellW + 2f, pt.y * cellH + 2f),
                        size = Size(cellW - 4f, cellH - 4f),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
            }

            if (isPaused) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CyberBackground.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PAUSED", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                }
            }
        }

        // D-Pad Directional Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            IconButton(
                onClick = { if (direction != Direction.DOWN) nextDirection = Direction.UP },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .testTag("snake_btn_up")
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = NeonCyan, modifier = Modifier.size(32.dp))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (direction != Direction.RIGHT) nextDirection = Direction.LEFT },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .testTag("snake_btn_left")
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = NeonCyan, modifier = Modifier.size(32.dp))
                }
                IconButton(
                    onClick = { if (direction != Direction.UP) nextDirection = Direction.DOWN },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .testTag("snake_btn_down")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = NeonCyan, modifier = Modifier.size(32.dp))
                }
                IconButton(
                    onClick = { if (direction != Direction.LEFT) nextDirection = Direction.RIGHT },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .testTag("snake_btn_right")
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = NeonCyan, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
