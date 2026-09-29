package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow

data class GamerAvatar(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color
)

object AvatarRegistry {
    val avatars = listOf(
        GamerAvatar(0, "Cyber Striker", Icons.Default.SportsEsports, NeonCyan, NeonPurple),
        GamerAvatar(1, "Neon Phoenix", Icons.Default.LocalFireDepartment, NeonPink, NeonOrange),
        GamerAvatar(2, "Quantum Void", Icons.Default.RocketLaunch, NeonPurple, NeonCyan),
        GamerAvatar(3, "Iron Sentinel", Icons.Default.Shield, NeonGreen, NeonCyan),
        GamerAvatar(4, "Synapse Mage", Icons.Default.Psychology, NeonYellow, NeonPink),
        GamerAvatar(5, "Volt Champion", Icons.Default.ElectricBolt, NeonYellow, NeonOrange)
    )

    fun getAvatar(id: Int): GamerAvatar {
        return avatars.getOrNull(id) ?: avatars[0]
    }
}

@Composable
fun GamerAvatarView(
    avatarId: Int,
    size: Dp = 44.dp,
    borderWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val avatar = AvatarRegistry.getAvatar(avatarId)
    Box(
        modifier = modifier
            .size(size)
            .border(
                width = borderWidth,
                brush = Brush.linearGradient(listOf(avatar.primaryColor, avatar.secondaryColor)),
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        avatar.primaryColor.copy(alpha = 0.45f),
                        CyberBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = avatar.icon,
            contentDescription = avatar.name,
            tint = avatar.primaryColor,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}
