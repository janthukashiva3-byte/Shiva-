package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GamingTopBar(
    user: UserProfileEntity?,
    unreadCount: Int,
    onAvatarClick: () -> Unit,
    onPointsClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val username = user?.username ?: "Player"
    val avatarId = user?.avatarId ?: 0
    val points = user?.points ?: 0
    val level = user?.level ?: 1

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(CyberBackground.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Avatar + Username + Level
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onAvatarClick() }
                .testTag("top_bar_user_profile")
        ) {
            GamerAvatarView(avatarId = avatarId, size = 42.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = username,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "LVL $level",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberBackground
                        )
                    }
                }
            }
        }

        // Right: Points Pill + Admin Button + Notifications Bell
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Points Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(listOf(GoldColor, NeonYellow)),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(CyberSurface)
                    .clickable { onPointsClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("points_badge")
            ) {
                Text(text = "🪙", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$points PTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldColor
                )
            }

            if (user?.isAdmin == true) {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                        .testTag("admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin Panel",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Notification Bell with badge
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .testTag("notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge(
                                containerColor = NeonPink,
                                contentColor = TextPrimary
                            ) {
                                Text(
                                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = if (unreadCount > 0) NeonCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private val NeonYellow = Color(0xFFFFD600)
