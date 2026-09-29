package com.example.ui.games

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun TicTacToeGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var isXTurn by remember { mutableStateOf(true) }
    var vsAI by remember { mutableStateOf(true) }
    var xWins by remember { mutableIntStateOf(0) }
    var oWins by remember { mutableIntStateOf(0) }
    var draws by remember { mutableIntStateOf(0) }
    var winningIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var gameOver by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }

    fun checkWin(b: List<String>): Pair<String?, List<Int>> {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (line in lines) {
            val (a, c, d) = line
            if (b[a].isNotEmpty() && b[a] == b[c] && b[a] == b[d]) {
                return Pair(b[a], line)
            }
        }
        if (b.all { it.isNotEmpty() }) {
            return Pair("DRAW", emptyList())
        }
        return Pair(null, emptyList())
    }

    fun makeAIMove() {
        if (gameOver || isXTurn) return
        val emptySlots = board.indices.filter { board[it].isEmpty() }
        if (emptySlots.isEmpty()) return

        // 1. Try to win
        for (slot in emptySlots) {
            val testBoard = board.toMutableList()
            testBoard[slot] = "O"
            if (checkWin(testBoard).first == "O") {
                board = testBoard
                isXTurn = true
                return
            }
        }
        // 2. Try to block opponent
        for (slot in emptySlots) {
            val testBoard = board.toMutableList()
            testBoard[slot] = "X"
            if (checkWin(testBoard).first == "X") {
                val newB = board.toMutableList()
                newB[slot] = "O"
                board = newB
                isXTurn = true
                return
            }
        }
        // 3. Take center or random
        val chosen = if (board[4].isEmpty()) 4 else emptySlots.random()
        val newB = board.toMutableList()
        newB[chosen] = "O"
        board = newB
        isXTurn = true
    }

    // Effect on board change
    LaunchedEffect(board) {
        val (resWinner, line) = checkWin(board)
        if (resWinner != null) {
            gameOver = true
            winner = resWinner
            winningIndices = line
            when (resWinner) {
                "X" -> xWins++
                "O" -> oWins++
                "DRAW" -> draws++
            }
        } else if (!isXTurn && vsAI) {
            delay(400)
            makeAIMove()
        }
    }

    fun resetRound() {
        board = List(9) { "" }
        winningIndices = emptyList()
        gameOver = false
        winner = null
        isXTurn = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Mode & Scoreboard
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurface)
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (vsAI) NeonCyan else Color.Transparent)
                        .clickable {
                            vsAI = true
                            resetRound()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "VS SMART AI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (vsAI) CyberBackground else TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!vsAI) NeonPurple else Color.Transparent)
                        .clickable {
                            vsAI = false
                            resetRound()
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "2 PLAYERS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!vsAI) TextPrimary else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberSurfaceVariant)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PLAYER X", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    Text("$xWins", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DRAWS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("$draws", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (vsAI) "CYBER AI" else "PLAYER O", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                    Text("$oWins", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Turn Indicator
            val statusText = when {
                winner == "X" -> "🎉 PLAYER X WINS!"
                winner == "O" -> if (vsAI) "🤖 CYBER AI WINS!" else "🎉 PLAYER O WINS!"
                winner == "DRAW" -> "🤝 IT'S A DRAW!"
                isXTurn -> "Turn: Player X (Cyan)"
                else -> if (vsAI) "AI is calculating..." else "Turn: Player O (Pink)"
            }

            Text(
                text = statusText,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = when {
                    winner == "X" -> NeonCyan
                    winner == "O" -> NeonPink
                    winner == "DRAW" -> GoldColor
                    isXTurn -> NeonCyan
                    else -> NeonPink
                }
            )
        }

        // 3x3 Grid
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurface)
                .border(2.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            val value = board[index]
                            val isWinSquare = winningIndices.contains(index)
                            val bgSquare by animateColorAsState(
                                targetValue = if (isWinSquare) NeonPurple.copy(alpha = 0.35f) else CyberSurfaceVariant,
                                label = "sqBg"
                            )

                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgSquare)
                                    .border(
                                        width = if (isWinSquare) 2.dp else 1.dp,
                                        color = if (isWinSquare) GoldColor else CyberCardBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable(enabled = !gameOver && value.isEmpty() && (isXTurn || !vsAI)) {
                                        val newBoard = board.toMutableList()
                                        newBoard[index] = if (isXTurn) "X" else "O"
                                        board = newBoard
                                        isXTurn = !isXTurn
                                    }
                                    .testTag("tictactoe_cell_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (value == "X") {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "X",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(46.dp)
                                    )
                                } else if (value == "O") {
                                    Icon(
                                        imageVector = Icons.Default.FiberManualRecord,
                                        contentDescription = "O",
                                        tint = NeonPink,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { resetRound() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("tictactoe_next_round_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("CLEAR BOARD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Button(
                    onClick = {
                        val finalScore = (xWins * 100) + (draws * 25)
                        onFinishGame(finalScore)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("tictactoe_finish_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("END & CLAIM", fontSize = 12.sp, fontWeight = FontWeight.Black, color = CyberBackground)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
