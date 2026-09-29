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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.LeaderboardUser
import com.example.ui.components.GamerAvatarView
import com.example.ui.theme.BronzeColor
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.SilverColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LeaderboardScreen(
    selectedTab: String,
    onTabSelect: (String) -> Unit,
    users: List<LeaderboardUser>,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("global" to "GLOBAL", "weekly" to "WEEKLY", "friends" to "FRIENDS")
    val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)

    val currentUser = users.find { it.isCurrentUser }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = CyberSurface,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    color = NeonCyan,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
        ) {
            tabs.forEachIndexed { idx, (key, title) ->
                Tab(
                    selected = selectedIndex == idx,
                    onClick = { onTabSelect(key) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedIndex == idx) NeonCyan else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("leaderboard_tab_$key")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Podium for Top 3
        if (users.size >= 3) {
            val rank1 = users[0]
            val rank2 = users[1]
            val rank3 = users[2]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B1537), CyberSurfaceVariant)
                        )
                    )
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place (Silver)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🥈 2ND", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SilverColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        GamerAvatarView(avatarId = rank2.avatarId, size = 52.dp, borderWidth = 2.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(rank2.username, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, textAlign = TextAlign.Center)
                        Text("${rank2.points} PTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SilverColor)
                    }

                    // 1st Place (Gold)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Text("👑 CHAMPION", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        GamerAvatarView(avatarId = rank1.avatarId, size = 68.dp, borderWidth = 3.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(rank1.username, fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldColor, maxLines = 1, textAlign = TextAlign.Center)
                        Text("${rank1.points} PTS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldColor)
                    }

                    // 3rd Place (Bronze)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🥉 3RD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BronzeColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        GamerAvatarView(avatarId = rank3.avatarId, size = 52.dp, borderWidth = 2.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(rank3.username, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, textAlign = TextAlign.Center)
                        Text("${rank3.points} PTS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BronzeColor)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Rank 4..N list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            val tailUsers = if (users.size > 3) users.drop(3) else emptyList()
            items(tailUsers) { userItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (userItem.isCurrentUser) NeonCyan.copy(alpha = 0.15f) else CyberSurface
                        )
                        .border(
                            1.dp,
                            if (userItem.isCurrentUser) NeonCyan else CyberCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${userItem.rank}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (userItem.isCurrentUser) NeonCyan else TextSecondary,
                            modifier = Modifier.width(36.dp)
                        )
                        GamerAvatarView(avatarId = userItem.avatarId, size = 36.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = userItem.username,
                                fontSize = 13.sp,
                                fontWeight = if (userItem.isCurrentUser) FontWeight.Black else FontWeight.Bold,
                                color = if (userItem.isCurrentUser) NeonCyan else TextPrimary
                            )
                            Text(
                                text = userItem.badge,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }

                    Text(
                        text = "${userItem.points} PTS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldColor
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

        // Pinned Current User Card at bottom
        currentUser?.let { me ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(CyberSurfaceVariant)
                    .border(
                        1.5.dp,
                        NeonCyan,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("pinned_current_user_rank")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${me.rank}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCyan,
                            modifier = Modifier.width(42.dp)
                        )
                        GamerAvatarView(avatarId = me.avatarId, size = 38.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Your Current Rank",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = me.username,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        }
                    }

                    Text(
                        text = "${me.points} PTS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonYellow
                    )
                }
            }
        }
    }
}
