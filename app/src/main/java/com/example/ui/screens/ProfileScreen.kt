package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.GameStatsEntity
import com.example.data.model.UserProfileEntity
import com.example.ui.components.AvatarRegistry
import com.example.ui.components.GamerAvatarView
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    user: UserProfileEntity?,
    games: List<GameStatsEntity>,
    records: List<GameRecordEntity>,
    achievements: List<AchievementEntity>,
    onUpdateProfile: (username: String, avatarId: Int, bio: String, favGame: String) -> Unit,
    onOpenAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    var editUsername by remember { mutableStateOf(user?.username ?: "Player") }
    var editBio by remember { mutableStateOf(user?.bio ?: "") }
    var editFavGame by remember { mutableStateOf(user?.favoriteGame ?: "Neon Highway") }
    var editAvatarId by remember { mutableIntStateOf(user?.avatarId ?: 0) }

    val totalPlayed = games.sumOf { it.timesPlayed }
    val maxScore = games.maxOfOrNull { it.highScore } ?: 0
    val unlockedCount = achievements.count { it.isUnlocked }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // User Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberSurface)
                    .border(1.5.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clickable Avatar with edit badge
                        Box(
                            modifier = Modifier.clickable { showAvatarPicker = true }
                        ) {
                            GamerAvatarView(avatarId = user?.avatarId ?: 0, size = 68.dp, borderWidth = 2.5.dp)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Avatar", tint = CyberBackground, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = user?.username ?: "CyberStriker",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "#GZ-${(user?.points ?: 1000) % 9999}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editUsername = user?.username ?: ""
                                        editBio = user?.bio ?: ""
                                        editFavGame = user?.favoriteGame ?: ""
                                        editAvatarId = user?.avatarId ?: 0
                                        showEditDialog = true
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = NeonCyan)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Level Badge & XP
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "LEVEL ${user?.level ?: 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPurple
                                )
                                Text(
                                    text = "${user?.xp ?: 0} / ${((user?.level ?: 1)) * 500} XP",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            val xpFraction = ((user?.xp ?: 0) % 500) / 500f
                            LinearProgressIndicator(
                                progress = { xpFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NeonPurple,
                                trackColor = CyberSurfaceVariant
                            )
                        }
                    }

                    if (!user?.bio.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = user?.bio ?: "",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🎮 Favorite Game: ${user?.favoriteGame ?: "Neon Highway"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }

        // Stats Overview Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Points
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("POINTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text("${user?.points ?: 0}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GoldColor)
                    }
                }

                // Total Matches
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("MATCHES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text("$totalPlayed", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                    }
                }

                // Best Score
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text("BEST SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text("$maxScore", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonPink)
                    }
                }
            }
        }

        // Achievements Showcase
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACHIEVEMENTS ($unlockedCount / ${achievements.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(achievements) { ach ->
                        Box(
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (ach.isUnlocked) CyberSurface else CyberSurfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, if (ach.isUnlocked) GoldColor else CyberCardBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (ach.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (ach.isUnlocked) GoldColor else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "+${ach.rewardPoints} PTS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ach.isUnlocked) GoldColor else TextMuted
                                    )
                                }

                                Text(
                                    text = ach.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ach.isUnlocked) TextPrimary else TextSecondary
                                )

                                Text(
                                    text = ach.description,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    minLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Match History
        item {
            Column {
                Text(
                    text = "RECENT MATCHES",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (records.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberSurface)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No match records yet. Play a game to record your score!", fontSize = 12.sp, color = TextSecondary)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        records.take(5).forEach { rec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberSurface)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(rec.gameTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Score: ${rec.score}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                Text("+${rec.pointsEarned} PTS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = NeonYellow)
                            }
                        }
                    }
                }
            }
        }

        // Admin & Logout Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (user?.isAdmin == true) {
                    Button(
                        onClick = onOpenAdmin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("profile_admin_dashboard_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OPEN ADMIN DASHBOARD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    }
                }

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("profile_logout_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG OUT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Avatar Picker Dialog
    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            title = { Text("Choose Gamer Avatar", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AvatarRegistry.avatars.forEach { av ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    editAvatarId = av.id
                                    onUpdateProfile(user?.username ?: "", av.id, user?.bio ?: "", user?.favoriteGame ?: "")
                                    showAvatarPicker = false
                                }
                                .padding(4.dp)
                        ) {
                            GamerAvatarView(avatarId = av.id, size = 48.dp, borderWidth = if (user?.avatarId == av.id) 3.dp else 1.dp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPicker = false }) {
                    Text("Close", color = NeonCyan)
                }
            },
            containerColor = CyberSurface
        )
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Gamer Profile", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Gamer Username") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editFavGame,
                        onValueChange = { editFavGame = it },
                        label = { Text("Favorite Game") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editUsername, editAvatarId, editBio, editFavGame)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Save Changes", color = CyberBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CyberSurface
        )
    }
}
