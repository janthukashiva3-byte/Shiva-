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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

// Mini Ludo Board with 16 track cells (0..15) + 3 Home stretch cells for each
// Track loop length: 16
// Player start: 0, Home entrance: 15 -> Home 1,2,3
// AI start: 8, Home entrance: 7 -> Home 1,2,3
data class LudoToken(
    val id: Int,
    val isHome: Boolean = false,
    val positionOnTrack: Int = -1, // -1 in base, 0..15 on track, 16..18 home stretch, 19 finished
    val isFinished: Boolean = false
)

@Composable
fun LudoGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var playerTokens by remember { mutableStateOf(listOf(LudoToken(1), LudoToken(2))) }
    var aiTokens by remember { mutableStateOf(listOf(LudoToken(1), LudoToken(2))) }
    var diceValue by remember { mutableIntStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var gameLog by remember { mutableStateOf("Roll the dice to start!") }
    var playerScore by remember { mutableIntStateOf(0) }
    var diceRotation by remember { mutableFloatStateOf(0f) }

    val rotationAnim by animateFloatAsState(
        targetValue = diceRotation,
        animationSpec = tween(350),
        label = "diceRoll"
    )

    fun checkWinCondition() {
        val playerAllDone = playerTokens.all { it.isFinished }
        val aiAllDone = aiTokens.all { it.isFinished }
        if (playerAllDone) {
            val score = 300 + (playerScore * 50)
            onFinishGame(score)
        } else if (aiAllDone) {
            val score = maxOf(50, playerScore * 40)
            onFinishGame(score)
        }
    }

    fun movePlayerToken(tokenIndex: Int) {
        if (!isPlayerTurn || isRolling) return
        val token = playerTokens[tokenIndex]
        if (token.isFinished) return

        if (token.positionOnTrack == -1) {
            // Need a 6 to enter
            if (diceValue == 6) {
                val updated = playerTokens.toMutableList()
                updated[tokenIndex] = token.copy(positionOnTrack = 0)
                playerTokens = updated
                gameLog = "Token entered the track! 🎉"
                // 6 gives another roll
                return
            } else {
                gameLog = "Need a 6 to deploy token!"
                return
            }
        } else {
            val nextPos = token.positionOnTrack + diceValue
            if (nextPos >= 19) {
                val updated = playerTokens.toMutableList()
                updated[tokenIndex] = token.copy(positionOnTrack = 19, isFinished = true)
                playerTokens = updated
                playerScore += 100
                gameLog = "Token reached Home! 🏆"
            } else {
                val updated = playerTokens.toMutableList()
                updated[tokenIndex] = token.copy(positionOnTrack = nextPos)
                playerTokens = updated
                playerScore += 10

                // Check capture
                val currentTrackCoord = nextPos % 16
                val aiVictimIndex = aiTokens.indexOfFirst {
                    it.positionOnTrack >= 0 && (it.positionOnTrack % 16) == currentTrackCoord && it.positionOnTrack != 0 && it.positionOnTrack != 8
                }
                if (aiVictimIndex != -1) {
                    val aiList = aiTokens.toMutableList()
                    aiList[aiVictimIndex] = aiList[aiVictimIndex].copy(positionOnTrack = -1)
                    aiTokens = aiList
                    playerScore += 50
                    gameLog = "💥 Captured AI Token! +50 PTS!"
                } else {
                    gameLog = "Player moved $diceValue steps"
                }
            }
        }

        checkWinCondition()
        if (diceValue != 6) {
            isPlayerTurn = false
        }
    }

    // AI Turn Loop
    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn) {
            delay(600)
            isRolling = true
            diceRotation += 360f
            delay(350)
            val aiRoll = Random.nextInt(1, 7)
            diceValue = aiRoll
            isRolling = false
            delay(300)

            // AI Decision:
            val deployable = aiTokens.indexOfFirst { it.positionOnTrack == -1 }
            val active = aiTokens.indexOfFirst { it.positionOnTrack in 0..18 }

            if (aiRoll == 6 && deployable != -1 && (active == -1 || Random.nextBoolean())) {
                val updated = aiTokens.toMutableList()
                updated[deployable] = updated[deployable].copy(positionOnTrack = 8) // AI starts at 8
                aiTokens = updated
                gameLog = "AI deployed token onto track!"
            } else if (active != -1) {
                val token = aiTokens[active]
                val nextPos = token.positionOnTrack + aiRoll
                val updated = aiTokens.toMutableList()
                if (nextPos >= 19) {
                    updated[active] = token.copy(positionOnTrack = 19, isFinished = true)
                    gameLog = "AI Token reached Home!"
                } else {
                    updated[active] = token.copy(positionOnTrack = nextPos)
                    // Check if AI captured player
                    val aiTrackCoord = nextPos % 16
                    val playerVictimIndex = playerTokens.indexOfFirst {
                        it.positionOnTrack >= 0 && (it.positionOnTrack % 16) == aiTrackCoord && it.positionOnTrack != 0 && it.positionOnTrack != 8
                    }
                    if (playerVictimIndex != -1) {
                        val pList = playerTokens.toMutableList()
                        pList[playerVictimIndex] = pList[playerVictimIndex].copy(positionOnTrack = -1)
                        playerTokens = pList
                        gameLog = "⚠️ AI knocked out your token!"
                    } else {
                        gameLog = "AI moved $aiRoll steps"
                    }
                }
                aiTokens = updated
            } else {
                gameLog = "AI rolled $aiRoll (no moves)"
            }

            checkWinCondition()
            delay(600)
            if (aiRoll != 6) {
                isPlayerTurn = true
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
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CyberSurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("YOUR TOKENS (CYAN)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Text("Home: ${playerTokens.count { it.isFinished }} / 2", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("AI TOKENS (PINK)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                Text("Home: ${aiTokens.count { it.isFinished }} / 2", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }

        // Ludo Mini Board
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurface)
                .border(2.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row (AI Base & North Track)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AI Base
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonPink.copy(alpha = 0.2f))
                            .border(1.dp, NeonPink, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AI BASE\n🔴🔴", fontSize = 10.sp, color = NeonPink, textAlign = TextAlign.Center)
                    }

                    // Track segment
                    Text("🏁 BATTLE ARENA", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextSecondary)

                    // Player Base
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, NeonCyan, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("YOU\n🔷🔷", fontSize = 10.sp, color = NeonCyan, textAlign = TextAlign.Center)
                    }
                }

                // Middle Track representation with tokens
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "TRACK PROGRESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    playerTokens.forEachIndexed { idx, t ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text("Token ${idx + 1}: ", fontSize = 12.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            val status = when {
                                t.isFinished -> "✅ IN HOME GOAL"
                                t.positionOnTrack == -1 -> "In Base (Needs a 6)"
                                else -> "Step ${t.positionOnTrack}/19"
                            }
                            Text(status, fontSize = 12.sp, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    aiTokens.forEachIndexed { idx, t ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text("AI Token ${idx + 1}: ", fontSize = 12.sp, color = NeonPink, fontWeight = FontWeight.Bold)
                            val status = when {
                                t.isFinished -> "✅ IN HOME GOAL"
                                t.positionOnTrack == -1 -> "In Base"
                                else -> "Step ${t.positionOnTrack}/19"
                            }
                            Text(status, fontSize = 12.sp, color = TextPrimary)
                        }
                    }
                }

                // Status Message Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBackground)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = gameLog,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPlayerTurn) NeonCyan else NeonPink,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Dice Roller & Token Selector
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Dice
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .rotate(rotationAnim)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                if (isPlayerTurn) listOf(NeonCyan, NeonPurple) else listOf(NeonPink, Color(0xFF6B1130))
                            )
                        )
                        .border(2.dp, GoldColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = isPlayerTurn && !isRolling) {
                            isRolling = true
                            diceRotation += 360f
                            diceValue = Random.nextInt(1, 7)
                            isRolling = false
                            gameLog = "You rolled a $diceValue!"
                        }
                        .testTag("ludo_dice_roller"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$diceValue",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                // Move Token 1 or 2 Buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { movePlayerToken(0) },
                        enabled = isPlayerTurn && !playerTokens[0].isFinished,
                        modifier = Modifier
                            .width(170.dp)
                            .height(42.dp)
                            .testTag("ludo_move_token_1"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text("MOVE TOKEN 1", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberBackground)
                    }

                    Button(
                        onClick = { movePlayerToken(1) },
                        enabled = isPlayerTurn && !playerTokens[1].isFinished,
                        modifier = Modifier
                            .width(170.dp)
                            .height(42.dp)
                            .testTag("ludo_move_token_2"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) {
                        Text("MOVE TOKEN 2", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val finalScore = (playerScore) + (playerTokens.count { it.isFinished } * 150)
                    onFinishGame(maxOf(50, finalScore))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("ludo_finish_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Text("FINISH MATCH & CLAIM POINTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            }
        }
    }
}
