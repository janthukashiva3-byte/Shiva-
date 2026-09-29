package com.example.ui.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.delay

data class MemoryCard(
    val id: Int,
    val iconKey: Int,
    val icon: ImageVector,
    val color: Color,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryMatchGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val icons = listOf(
        Pair(Icons.Default.SportsEsports, NeonCyan),
        Pair(Icons.Default.EmojiEvents, GoldColor),
        Pair(Icons.Default.RocketLaunch, NeonPurple),
        Pair(Icons.Default.Shield, NeonGreen),
        Pair(Icons.Default.Diamond, NeonCyan),
        Pair(Icons.Default.ElectricBolt, NeonYellow),
        Pair(Icons.Default.LocalFireDepartment, NeonOrange),
        Pair(Icons.Default.Security, NeonPink)
    )

    fun setupCards(): List<MemoryCard> {
        val list = mutableListOf<MemoryCard>()
        var id = 0
        icons.forEachIndexed { iconKey, (icon, color) ->
            list.add(MemoryCard(id++, iconKey, icon, color))
            list.add(MemoryCard(id++, iconKey, icon, color))
        }
        return list.shuffled()
    }

    var cards by remember { mutableStateOf(setupCards()) }
    var flippedIndices by remember { mutableStateOf(listOf<Int>()) }
    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(60) }
    var isGameOver by remember { mutableStateOf(false) }

    // Countdown Timer
    LaunchedEffect(isGameOver) {
        while (timeLeft > 0 && !isGameOver) {
            delay(1000)
            timeLeft--
            if (timeLeft <= 0) {
                isGameOver = true
                val finalScore = (matchedPairs * 80) + (timeLeft * 5)
                onFinishGame(maxOf(30, finalScore))
            }
        }
    }

    // Check Match
    LaunchedEffect(flippedIndices) {
        if (flippedIndices.size == 2) {
            val first = flippedIndices[0]
            val second = flippedIndices[1]
            moves++
            delay(500)
            val updated = cards.toMutableList()
            if (cards[first].iconKey == cards[second].iconKey) {
                updated[first] = updated[first].copy(isMatched = true)
                updated[second] = updated[second].copy(isMatched = true)
                matchedPairs++
                if (matchedPairs == 8) {
                    isGameOver = true
                    val score = (matchedPairs * 100) + (timeLeft * 10) - (moves * 2)
                    onFinishGame(maxOf(100, score))
                }
            } else {
                updated[first] = updated[first].copy(isFlipped = false)
                updated[second] = updated[second].copy(isFlipped = false)
            }
            cards = updated
            flippedIndices = emptyList()
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
                Text("TIME LEFT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("${timeLeft}s", fontSize = 22.sp, fontWeight = FontWeight.Black, color = if (timeLeft < 15) NeonPink else NeonCyan)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PAIRS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("$matchedPairs / 8", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("MOVES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Text("$moves", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        // 4x4 Grid of Cards
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurface)
                .border(2.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..3) {
                            val index = row * 4 + col
                            val card = cards[index]
                            val isShown = card.isFlipped || card.isMatched

                            val rotation by animateFloatAsState(
                                targetValue = if (isShown) 180f else 0f,
                                animationSpec = tween(280),
                                label = "cardFlip"
                            )

                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .graphicsLayer {
                                        rotationY = rotation
                                        cameraDistance = 12f * density
                                    }
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (card.isMatched) card.color.copy(alpha = 0.2f)
                                        else if (isShown) CyberSurfaceVariant
                                        else Color(0xFF181E34)
                                    )
                                    .border(
                                        width = if (card.isMatched) 2.dp else 1.dp,
                                        color = if (card.isMatched) card.color else CyberCardBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable(enabled = !isShown && flippedIndices.size < 2 && !isGameOver) {
                                        val updated = cards.toMutableList()
                                        updated[index] = card.copy(isFlipped = true)
                                        cards = updated
                                        flippedIndices = flippedIndices + index
                                    }
                                    .testTag("memory_card_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rotation >= 90f) {
                                    Icon(
                                        imageVector = card.icon,
                                        contentDescription = "Card Symbol",
                                        tint = card.color,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .graphicsLayer { rotationY = 180f }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.QuestionMark,
                                        contentDescription = "Hidden Card",
                                        tint = TextSecondary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    cards = setupCards()
                    flippedIndices = emptyList()
                    moves = 0
                    matchedPairs = 0
                    timeLeft = 60
                    isGameOver = false
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("memory_restart_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Icon(Icons.Default.Replay, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(6.dp))
                Text("RESTART", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            Button(
                onClick = {
                    val finalScore = (matchedPairs * 80) + (timeLeft * 5)
                    onFinishGame(maxOf(30, finalScore))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("memory_finish_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
            ) {
                Text("FINISH & CLAIM", fontSize = 12.sp, fontWeight = FontWeight.Black, color = CyberBackground)
            }
        }
    }
}
