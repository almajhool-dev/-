package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.TikTokAccount
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityOrange

@Composable
fun AccountHeaderCard(
    account: TikTokAccount,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    GlassmorphicCard(
        modifier = modifier.testTag("account_header_card"),
        borderColor = if (account.isVerified) NeonBlue else NeonCyan.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar with Cyber Border
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    NeonCyan,
                                    NeonBlue,
                                    Color(0xFF8B5CF6),
                                    NeonCyan
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                ) {
                    if (account.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(account.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "صورة حساب ${account.username}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                                .testTag("account_avatar_image")
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = account.username.take(2).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Name, Username & Verification Badge
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = account.nickname.ifBlank { account.username },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (account.isVerified) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "موثق بالعلامة الزرقاء",
                                tint = Color(0xFF20D5EC),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "@${account.username}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = NeonCyan,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("TikTok Username", "@${account.username}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ اليوزر @${account.username}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp).padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ اسم المستخدم",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Privacy Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isPriv = account.isPrivate
                        val badgeColor = if (isPriv) SecurityOrange else SecurityGreen
                        Icon(
                            imageVector = if (isPriv) Icons.Default.Lock else Icons.Default.Public,
                            contentDescription = if (isPriv) "خاص" else "عام",
                            tint = badgeColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isPriv) "حساب خاص (مغلق)" else "حساب عام متاح",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = badgeColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Bio Section if present
            if (account.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B1322), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = account.bio,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // User ID & Security details if available
            if (!account.userId.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "معرف الحساب (UID): ${account.userId}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Open in TikTok Button
            OutlinedButton(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(account.profileUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "تعذر فتح المتصفح", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_tiktok"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NeonCyan
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "فتح في تيك توك",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "فتح الحساب الأصلي على TikTok",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}
