package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityRed
import java.util.Locale

@Composable
fun StatsGrid(
    followers: Long,
    following: Long,
    likes: Long,
    videos: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricItem(
                title = "المتابعون",
                value = followers,
                icon = Icons.Default.People,
                accentColor = NeonCyan,
                modifier = Modifier.weight(1f).testTag("stat_followers")
            )
            StatMetricItem(
                title = "يتابع",
                value = following,
                icon = Icons.Default.PersonAdd,
                accentColor = NeonBlue,
                modifier = Modifier.weight(1f).testTag("stat_following")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatMetricItem(
                title = "الإعجابات",
                value = likes,
                icon = Icons.Default.Favorite,
                accentColor = SecurityRed,
                modifier = Modifier.weight(1f).testTag("stat_likes")
            )
            StatMetricItem(
                title = "الفيديوهات",
                value = videos,
                icon = Icons.Default.PlayCircle,
                accentColor = SecurityGreen,
                modifier = Modifier.weight(1f).testTag("stat_videos")
            )
        }
    }
}

@Composable
fun StatMetricItem(
    title: String,
    value: Long,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0F172A), RoundedCornerShape(14.dp))
            .border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatCompactNumber(value),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 20.sp
                )
            )
        }
    }
}

fun formatCompactNumber(number: Long): String {
    if (number < 0) return "0"
    if (number < 1_000) return number.toString()
    if (number < 1_000_000) {
        val k = number / 1_000.0
        return String.format(Locale.US, "%.1fK", k).replace(".0K", "K")
    }
    if (number < 1_000_000_000) {
        val m = number / 1_000_000.0
        return String.format(Locale.US, "%.1fM", m).replace(".0M", "M")
    }
    val b = number / 1_000_000_000.0
    return String.format(Locale.US, "%.1fB", b).replace(".0B", "B")
}
