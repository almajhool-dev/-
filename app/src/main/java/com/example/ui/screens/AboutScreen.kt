package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityOrange

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("about_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo and Identity
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F172A))
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(listOf(NeonCyan, NeonBlue, Color(0xFF8B5CF6), NeonCyan)),
                    shape = CircleShape
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.tk_sabr_logo_1792462968602),
                contentDescription = "شعار TK_SABR",
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }

        Text(
            text = "منظومة TK_SABR",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 24.sp
            )
        )

        Text(
            text = "أداة الأمن السيبراني المتخصصة في تدقيق وفحص حسابات TikTok بدون تسجيل دخول",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                lineHeight = 19.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Developer Credit Card (المبرمج المجهول)
        GlassmorphicCard(
            borderColor = NeonCyan.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(NeonCyan.copy(alpha = 0.12f), CircleShape)
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "المبرمج المجهول",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "تطوير وبرمجة:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "المبرمج المجهول",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }

        // Security Principles
        GlassmorphicCard(borderColor = NeonBlue.copy(alpha = 0.3f)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "المبادئ الأمنية الصارمة للتطبيق",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                AboutFeatureRow(
                    icon = Icons.Default.LockOpen,
                    iconColor = SecurityGreen,
                    title = "بدون تسجيل دخول أو كلمات مرور",
                    description = "التطبيق لا يطلب ولا يخزن أي بيانات اعتماد أو كلمات سر إطلاقاً، ويعتمد كلياً على اسم المستخدم العلني."
                )

                AboutFeatureRow(
                    icon = Icons.Default.Visibility,
                    iconColor = NeonCyan,
                    title = "فحص المؤشرات العامة الحقيقية",
                    description = "تعتمد خوارزمية الفحص على الأرقام الحقيقية المتاحة علناً دون اختلاق بيانات وهمية أو افتراضية."
                )

                AboutFeatureRow(
                    icon = Icons.Default.Shield,
                    iconColor = SecurityOrange,
                    title = "تشفير وحفظ محلي آمن",
                    description = "جميع سجلات الفحص والتقارير تحفظ محلياً على جهازك بواسطة قاعدة بيانات مشفرة ويمكنك مسحها بأي وقت."
                )
            }
        }

        // Disclaimer
        GlassmorphicCard(borderColor = Color(0xFF475569)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "إخلاء مسؤولية",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "إخلاء مسؤولية وقواعد الاستخدام",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCBD5E1)
                        )
                    )
                }

                Text(
                    text = "تطبيق TK_SABR هو أداة تحليل استرشادية مستقلة ولا تتبع رسمياً لشركة TikTok أو ByteDance. تقييم أمان الحساب هو مقياس حسابي لجودة وتكامل مؤشرات الملف الرقمي الظاهرة للعامة، ولا يمثل فحصاً أمنياً لاختبار الاختراق الداخلي، ولا يقدم أي ادعاءات قطعية بشأن مخالفات خفية داخلية بدون إثبات موثوق.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "الإصدار 1.0.0 (Cyber Stable Build)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun AboutFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}
