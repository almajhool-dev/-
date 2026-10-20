package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.AuditHistoryEntity
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.formatCompactNumber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.SecurityYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    historyList: List<AuditHistoryEntity>,
    onSelectAccount: (String) -> Unit,
    onDeleteAccount: (Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("history_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "السجل",
                    tint = NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "سجل الفحوصات السابقة (${historyList.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            if (historyList.isNotEmpty()) {
                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier.testTag("btn_clear_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "مسح الكل",
                        tint = Color(0xFFEF4444)
                    )
                }
            }
        }

        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Color(0xFF334155),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "لا توجد أي عمليات فحص سابقة",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "قم بفحص أي حساب تيك توك لحفظ تقريره التقديري هنا للرجوع إليه لاحقاً.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onClick = { onSelectAccount(item.username) },
                        onDelete = { onDeleteAccount(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: AuditHistoryEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val scoreColor = when (item.safetyScore) {
        in 90..100 -> SecurityGreen
        in 70..89 -> SecurityYellow
        in 40..69 -> SecurityOrange
        else -> SecurityRed
    }

    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.timestamp))

    GlassmorphicCard(
        borderColor = scoreColor.copy(alpha = 0.35f),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.5.dp, scoreColor.copy(alpha = 0.6f), CircleShape)
            ) {
                if (item.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.avatarUrl,
                        contentDescription = item.username,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = item.username.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(color = NeonCyan)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.nickname.ifBlank { item.username },
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    if (item.isVerified) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "موثق",
                            tint = Color(0xFF20D5EC),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "@${item.username}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeonCyan,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${formatCompactNumber(item.followerCount)} متابع • $formattedDate",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                )
            }

            // Score Pill
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .background(scoreColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, scoreColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${item.safetyScore}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = scoreColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
