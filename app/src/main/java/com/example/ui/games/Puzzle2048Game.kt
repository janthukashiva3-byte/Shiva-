package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.abs
import kotlin.random.Random

fun getTileColor(value: Int): Pair<Color, Color> {
    return when (value) {
        2 -> Pair(Color(0xFF1E2846), NeonCyan)
        4 -> Pair(Color(0xFF1A334B), NeonCyan)
        8 -> Pair(Color(0xFF33264D), NeonPurple)
        16 -> Pair(Color(0xFF4B234F), NeonPurple)
        32 -> Pair(Color(0xFF581B3E), NeonPink)
        64 -> Pair(Color(0xFF6B1530), NeonPink)
        128 -> Pair(Color(0xFF523B15), NeonOrange)
        256 -> Pair(Color(0xFF5A4410), NeonYellow)
        512 -> Pair(Color(0xFF1B4E38), NeonGreen)
        1024 -> Pair(Color(0xFF165C58), NeonCyan)
        2048 -> Pair(Color(0xFF5A4D00), GoldColor)
        else -> Pair(CyberSurfaceVariant, TextSecondary)
    }
}

@Composable
fun Puzzle2048Game(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    fun spawnRandomTile(grid: Array<IntArray>): Boolean {
        val emptySlots = mutableListOf<Pair<Int, Int>>()
        for (r in 0..3) {
            for (c in 0..3) {
                if (grid[r][c] == 0) emptySlots.add(Pair(r, c))
            }
        }
        if (emptySlots.isEmpty()) return false
        val (r, c) = emptySlots.random()
        grid[r][c] = if (Random.nextInt(10) == 0) 4 else 2
        return true
    }

    fun initGrid(): Array<IntArray> {
        val g = Array(4) { IntArray(4) { 0 } }
        spawnRandomTile(g)
        spawnRandomTile(g)
        return g
    }

    var grid by remember { mutableStateOf(initGrid()) }
    var previousGrid by remember { mutableStateOf<Array<IntArray>?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var previousScore by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }

    fun canMove(g: Array<IntArray>): Boolean {
        for (r in 0..3) {
            for (c in 0..3) {
                if (g[r][c] == 0) return true
                if (r < 3 && g[r][c] == g[r + 1][c]) return true
                if (c < 3 && g[r][c] == g[r][c + 1]) return true
            }
        }
        return false
    }

    fun move(direction: Direction) {
        val backup = Array(4) { r -> grid[r].clone() }
        var moved = false
        var scoreGained = 0

        fun slideAndMerge(line: IntArray): IntArray {
            val nonZeros = line.filter { it != 0 }.toMutableList()
            val result = IntArray(4)
            var writeIdx = 0
            var i = 0
            while (i < nonZeros.size) {
                if (i + 1 < nonZeros.size && nonZeros[i] == nonZeros[i + 1]) {
                    val merged = nonZeros[i] * 2
                    result[writeIdx++] = merged
                    scoreGained += merged
                    i += 2
                } else {
                    result[writeIdx++] = nonZeros[i]
                    i++
                }
            }
            return result
        }

        val newGrid = Array(4) { IntArray(4) }

        when (direction) {
            Direction.LEFT -> {
                for (r in 0..3) {
                    val merged = slideAndMerge(grid[r])
                    newGrid[r] = merged
                    if (!grid[r].contentEquals(merged)) moved = true
                }
            }
            Direction.RIGHT -> {
                for (r in 0..3) {
                    val reversed = grid[r].reversedArray()
                    val merged = slideAndMerge(reversed).reversedArray()
                    newGrid[r] = merged
                    if (!grid[r].contentEquals(merged)) moved = true
                }
            }
            Direction.UP -> {
                for (c in 0..3) {
                    val colArr = IntArray(4) { grid[it][c] }
                    val merged = slideAndMerge(colArr)
                    for (r in 0..3) newGrid[r][c] = merged[r]
                    if (!colArr.contentEquals(merged)) moved = true
                }
            }
            Direction.DOWN -> {
                for (c in 0..3) {
                    val colArr = IntArray(4) { grid[it][c] }.reversedArray()
                    val merged = slideAndMerge(colArr).reversedArray()
                    for (r in 0..3) newGrid[r][c] = merged[r]
                    if (!colArr.contentEquals(merged.reversedArray())) moved = true
                }
            }
        }

        if (moved) {
            previousGrid = backup
            previousScore = score
            score += scoreGained
            spawnRandomTile(newGrid)
            grid = newGrid

            if (!canMove(newGrid)) {
                isGameOver = true
                onFinishGame(score)
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
        // Top Stats & Actions
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
                Text("$score", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonCyan)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        previousGrid?.let {
                            grid = it
                            score = previousScore
                            previousGrid = null
                        }
                    },
                    enabled = previousGrid != null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                ) {
                    Icon(Icons.Default.Undo, contentDescription = "Undo", tint = if (previousGrid != null) NeonCyan else TextSecondary)
                }

                IconButton(
                    onClick = {
                        grid = initGrid()
                        previousGrid = null
                        score = 0
                        isGameOver = false
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = "Restart", tint = NeonPink)
                }
            }
        }

        // 4x4 Grid with Swipe Detection
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurface)
                .border(2.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                .padding(10.dp)
                .pointerInput(Unit) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                        },
                        onDragEnd = {
                            val threshold = 40f
                            if (abs(totalDragX) > abs(totalDragY)) {
                                if (totalDragX > threshold) move(Direction.RIGHT)
                                else if (totalDragX < -threshold) move(Direction.LEFT)
                            } else {
                                if (totalDragY > threshold) move(Direction.DOWN)
                                else if (totalDragY < -threshold) move(Direction.UP)
                            }
                            totalDragX = 0f
                            totalDragY = 0f
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (r in 0..3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (c in 0..3) {
                            val value = grid[r][c]
                            val (bg, textColor) = getTileColor(value)

                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bg)
                                    .border(
                                        width = if (value >= 128) 1.5.dp else 1.dp,
                                        color = if (value >= 128) textColor else CyberCardBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (value > 0) {
                                    Text(
                                        text = "$value",
                                        fontSize = if (value >= 1024) 18.sp else 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // On-Screen D-Pad / Navigation Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            IconButton(
                onClick = { move(Direction.UP) },
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .testTag("2048_btn_up")
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = NeonCyan)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(36.dp)) {
                IconButton(
                    onClick = { move(Direction.LEFT) },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .testTag("2048_btn_left")
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = NeonCyan)
                }
                IconButton(
                    onClick = { move(Direction.DOWN) },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .testTag("2048_btn_down")
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = NeonCyan)
                }
                IconButton(
                    onClick = { move(Direction.RIGHT) },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(CyberSurfaceVariant)
                        .testTag("2048_btn_right")
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onFinishGame(score) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("2048_finish_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Text("FINISH & SAVE SCORE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            }
        }
    }
}
