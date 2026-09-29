package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GameOverDialogState

@Composable
fun GameOverDialog(
    state: GameOverDialogState,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit
) {
    if (!state.isVisible) return

    Dialog(onDismissRequest = onExit) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(NeonCyan, NeonPurple, NeonPink)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy or Victory Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (state.isNewHighScore) GoldColor.copy(alpha = 0.35f) else NeonCyan.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            2.dp,
                            if (state.isNewHighScore) GoldColor else NeonCyan,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = if (state.isNewHighScore) GoldColor else NeonCyan,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (state.isNewHighScore) "🔥 NEW HIGH SCORE! 🔥" else "MATCH FINISHED!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (state.isNewHighScore) GoldColor else TextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = state.gameTitle,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurfaceVariant)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "SCORE", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${state.finalScore}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(44.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "EARNED", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "+${state.pointsEarned} PTS",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonYellow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_play_again_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Play Again",
                        tint = CyberBackground,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PLAY AGAIN",
                        color = CyberBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("game_over_exit_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Exit to Games",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXIT TO GAMES",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
