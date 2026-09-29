package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameStatsEntity
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AdminScreen(
    user: UserProfileEntity?,
    games: List<GameStatsEntity>,
    onBack: () -> Unit,
    onToggleGameActive: (gameId: String, currentActive: Boolean) -> Unit,
    onToggleGameFeatured: (gameId: String, currentFeatured: Boolean) -> Unit,
    onGrantPoints: (Int) -> Unit,
    onSendBroadcast: (title: String, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMsg by remember { mutableStateOf("") }
    var pointsToAdd by remember { mutableStateOf("500") }

    val totalPlays = games.sumOf { it.timesPlayed }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .testTag("admin_back_btn")
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
                    text = "ADMIN CONSOLE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "GameZone Platform Management",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Stats Overview Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberSurface)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("TOTAL PLAYS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                            Text("$totalPlays", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberSurface)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.People, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACTIVE USERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                            Text("1,428", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeonPurple)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberSurface)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldColor, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("POINTS POOL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                            Text("284K", fontSize = 20.sp, fontWeight = FontWeight.Black, color = GoldColor)
                        }
                    }
                }
            }

            // Quick Points Award to User
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "⚡ USER BALANCE ADJUSTMENT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = pointsToAdd,
                                onValueChange = { pointsToAdd = it },
                                label = { Text("Points amount") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val pts = pointsToAdd.toIntOrNull() ?: 500
                                    onGrantPoints(pts)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                modifier = Modifier.testTag("admin_grant_pts_btn")
                            ) {
                                Text("GRANT", fontWeight = FontWeight.Black, color = CyberBackground)
                            }
                        }
                    }
                }
            }

            // System Notification Broadcast
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SEND SYSTEM BROADCAST", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                        }

                        OutlinedTextField(
                            value = broadcastTitle,
                            onValueChange = { broadcastTitle = it },
                            label = { Text("Notification Title") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = broadcastMsg,
                            onValueChange = { broadcastMsg = it },
                            label = { Text("Message Body") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (broadcastTitle.isNotEmpty() && broadcastMsg.isNotEmpty()) {
                                    onSendBroadcast(broadcastTitle, broadcastMsg)
                                    broadcastTitle = ""
                                    broadcastMsg = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_send_broadcast_btn")
                        ) {
                            Text("DISPATCH BROADCAST NOTIFICATION", fontWeight = FontWeight.Black, color = TextPrimary)
                        }
                    }
                }
            }

            // Manage Games
            item {
                Text(
                    text = "MANAGE GAMES (${games.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
            }

            items(games) { game ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(game.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            if (game.isFeatured) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Star, contentDescription = "Featured", tint = GoldColor, modifier = Modifier.size(16.dp))
                            }
                        }
                        Text(
                            text = "${game.category} • ${game.timesPlayed} plays • High: ${game.highScore}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Toggle Featured
                        Button(
                            onClick = { onToggleGameFeatured(game.gameId, game.isFeatured) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (game.isFeatured) GoldColor.copy(alpha = 0.2f) else CyberSurfaceVariant
                            )
                        ) {
                            Text(
                                text = if (game.isFeatured) "★ FEATURED" else "☆ FEATURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (game.isFeatured) GoldColor else TextSecondary
                            )
                        }

                        // Toggle Active
                        Switch(
                            checked = game.isEnabled,
                            onCheckedChange = { onToggleGameActive(game.gameId, game.isEnabled) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberBackground,
                                checkedTrackColor = NeonCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberSurfaceVariant
                            )
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
