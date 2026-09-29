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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
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
import com.example.data.model.UserProfileEntity
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

data class LevelTier(
    val level: Int,
    val title: String,
    val requiredXp: Int,
    val perk: String
)

val tiers = listOf(
    LevelTier(1, "Bronze Rookie", 0, "Access to all casual games"),
    LevelTier(2, "Silver Challenger", 500, "+5% Score Points Multiplier"),
    LevelTier(3, "Gold Striker", 1000, "Exclusive 'Neon Phoenix' Avatar"),
    LevelTier(4, "Platinum Prodigy", 1500, "+10% Daily Streak Points"),
    LevelTier(5, "Diamond Legend", 2000, "Golden Gamer Tag Badge & Frame"),
    LevelTier(6, "Apex Grandmaster", 3000, "+20% Points Boost across all titles")
)

@Composable
fun RewardsScreen(
    user: UserProfileEntity?,
    challenges: List<DailyChallengeEntity>,
    onClaimDailyStreak: () -> Unit,
    onClaimChallenge: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val streakDays = user?.streakDays ?: 1
    val currentXp = user?.xp ?: 0
    val currentLevel = user?.level ?: 1

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 7-Day Login Streak Box
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberSurface)
                    .border(1.5.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY LOGIN REWARDS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonYellow
                            )
                            Text(
                                text = "Log in every day to multiply your reward points",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onClaimDailyStreak,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("rewards_claim_streak_btn")
                        ) {
                            Text("CLAIM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CyberBackground)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (d in 1..7) {
                            val isClaimed = d <= streakDays
                            val isCurrent = d == streakDays
                            val pts = d * 50

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isClaimed) NeonCyan else CyberSurfaceVariant
                                        )
                                        .border(
                                            1.dp,
                                            if (isCurrent) NeonYellow else CyberCardBorder,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isClaimed) "✓" else "+$pts",
                                        fontSize = if (isClaimed) 14.sp else 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isClaimed) CyberBackground else TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "DAY $d",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) NeonCyan else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Challenge Tasks
        item {
            Column {
                Text(
                    text = "CHALLENGE REWARDS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    challenges.forEach { ch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberSurface)
                                .border(1.dp, if (ch.isCompleted && !ch.isClaimed) NeonGreen else CyberCardBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ch.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(ch.description, fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                val progress = (ch.currentProgress.toFloat() / maxOf(1, ch.targetScore)).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (ch.isCompleted) NeonGreen else NeonCyan,
                                    trackColor = CyberSurfaceVariant
                                )
                            }

                            if (ch.isClaimed) {
                                Text("CLAIMED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            } else if (ch.isCompleted) {
                                Button(
                                    onClick = { onClaimChallenge(ch.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                                ) {
                                    Text("CLAIM +${ch.rewardPoints}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CyberBackground)
                                }
                            } else {
                                Text("+${ch.rewardPoints} PTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldColor)
                            }
                        }
                    }
                }
            }
        }

        // Level-Up Tier Roadmap
        item {
            Column {
                Text(
                    text = "LEVEL PROGRESSION ROADMAP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    tiers.forEach { tier ->
                        val isUnlocked = currentLevel >= tier.level
                        val isCurrent = currentLevel == tier.level

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isCurrent) NeonPurple.copy(alpha = 0.15f) else CyberSurface)
                                .border(
                                    1.dp,
                                    if (isCurrent) NeonPurple else CyberCardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isUnlocked) NeonCyan else CyberSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isUnlocked) CyberBackground else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "LVL ${tier.level}: ${tier.title}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUnlocked) TextPrimary else TextSecondary
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonPurple)
                                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text("CURRENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                }
                                Text(
                                    text = tier.perk,
                                    fontSize = 11.sp,
                                    color = if (isUnlocked) NeonCyan else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
