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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyChallengeEntity
import com.example.data.model.GameStatsEntity
import com.example.data.model.LeaderboardUser
import com.example.data.model.UserProfileEntity
import com.example.ui.components.AvatarRegistry
import com.example.ui.components.GamerAvatarView
import com.example.ui.theme.BronzeColor
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
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    user: UserProfileEntity?,
    games: List<GameStatsEntity>,
    challenges: List<DailyChallengeEntity>,
    leaderboard: List<LeaderboardUser>,
    onLaunchGame: (String) -> Unit,
    onClaimStreak: () -> Unit,
    onClaimChallenge: (Int) -> Unit,
    onViewAllGames: () -> Unit,
    onViewLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streakDays = user?.streakDays ?: 1
    val featuredGame = games.find { it.isFeatured } ?: games.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Daily Streak & Rewards Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1E143E),
                                Color(0xFF162544)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔥", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$streakDays DAY STREAK",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = NeonYellow
                                )
                            }
                            Text(
                                text = "Daily login bonus: +${streakDays * 50} PTS",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onClaimStreak,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            modifier = Modifier.testTag("claim_daily_streak_btn")
                        ) {
                            Text(
                                text = "CLAIM TODAY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = CyberBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7-day mini track
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (day in 1..7) {
                            val isClaimed = day <= streakDays
                            val isCurrent = day == streakDays
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isClaimed) NeonCyan else CyberSurface
                                        )
                                        .border(
                                            1.dp,
                                            if (isCurrent) NeonYellow else CyberCardBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isClaimed) "✓" else "${day * 50}",
                                        fontSize = if (isClaimed) 14.sp else 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isClaimed) CyberBackground else TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "D$day",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) NeonCyan else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Featured Game Hero Card
        featuredGame?.let { feat ->
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FEATURED TITLE",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonPink.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("HOT 🔥", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonPink)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF26123D), CyberSurfaceVariant, Color(0xFF0F1B38))
                                )
                            )
                            .border(1.5.dp, NeonPurple, RoundedCornerShape(20.dp))
                            .clickable { onLaunchGame(feat.gameId) }
                            .padding(18.dp)
                            .testTag("featured_game_card")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = feat.title,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = feat.description,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 2
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(NeonPurple.copy(alpha = 0.3f))
                                        .border(1.dp, NeonPurple, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🏆 BEST: ${feat.highScore}  •  ${feat.timesPlayed} PLAYS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldColor
                                )

                                Button(
                                    onClick = { onLaunchGame(feat.gameId) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberBackground, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PLAY NOW", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CyberBackground)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Challenges
        if (challenges.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAILY CHALLENGES",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Resets in 12h",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(challenges) { ch ->
                            Box(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CyberSurface)
                                    .border(1.dp, if (ch.isCompleted && !ch.isClaimed) NeonGreen else CyberCardBorder, RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = ch.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "+${ch.rewardPoints} PTS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldColor
                                        )
                                    }

                                    Text(
                                        text = ch.description,
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        minLines = 2
                                    )

                                    val progressFraction = (ch.currentProgress.toFloat() / maxOf(1, ch.targetScore)).coerceIn(0f, 1f)
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = if (ch.isCompleted) NeonGreen else NeonCyan,
                                        trackColor = CyberSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${ch.currentProgress} / ${ch.targetScore}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )

                                        if (ch.isClaimed) {
                                            Text("CLAIMED ✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        } else if (ch.isCompleted) {
                                            Button(
                                                onClick = { onClaimChallenge(ch.id) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                                contentPadding = ButtonDefaults.ButtonWithIconContentPadding
                                            ) {
                                                Text("CLAIM", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CyberBackground)
                                            }
                                        } else {
                                            Button(
                                                onClick = { onLaunchGame(ch.gameId) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                                            ) {
                                                Text("GO ❯", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Leaderboard Preview Podium (Top 3)
        if (leaderboard.size >= 3) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LEADERBOARD TOP 3",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "VIEW ALL ❯",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            modifier = Modifier.clickable { onViewLeaderboard() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(CyberSurface)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Rank 2 (Silver)
                        val rank2 = leaderboard[1]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🥈 #2", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SilverColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            GamerAvatarView(avatarId = rank2.avatarId, size = 44.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rank2.username, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            Text("${rank2.points} PTS", fontSize = 11.sp, color = SilverColor)
                        }

                        // Rank 1 (Gold) - Elevated
                        val rank1 = leaderboard[0]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("👑 #1", fontSize = 15.sp, fontWeight = FontWeight.Black, color = GoldColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            GamerAvatarView(avatarId = rank1.avatarId, size = 56.dp, borderWidth = 3.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rank1.username, fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldColor, maxLines = 1)
                            Text("${rank1.points} PTS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldColor)
                        }

                        // Rank 3 (Bronze)
                        val rank3 = leaderboard[2]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🥉 #3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BronzeColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            GamerAvatarView(avatarId = rank3.avatarId, size = 44.dp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(rank3.username, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            Text("${rank3.points} PTS", fontSize = 11.sp, color = BronzeColor)
                        }
                    }
                }
            }
        }

        // Quick Play All Games
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUICK PLAY GAMES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "EXPLORE ALL (${games.size}) ❯",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier.clickable { onViewAllGames() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    games.take(4).forEach { game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberSurface)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                                .clickable { onLaunchGame(game.gameId) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyberSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = game.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${game.category} • Best: ${game.highScore}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Button(
                                onClick = { onLaunchGame(game.gameId) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
                            ) {
                                Text("PLAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
