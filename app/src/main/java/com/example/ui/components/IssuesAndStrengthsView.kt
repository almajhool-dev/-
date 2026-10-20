package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditIssue
import com.example.data.model.AuditStrength
import com.example.data.model.IssueSeverity
import com.example.data.model.ScoreFactor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.SecurityYellow

@Composable
fun DiscoveredIssuesSection(
    issues: List<AuditIssue>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("discovered_issues_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "المشاكل المكتشفة",
                tint = if (issues.isEmpty()) SecurityGreen else SecurityOrange,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "المشاكل المكتشفة والتحسينات (${issues.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
        }

        if (issues.isEmpty()) {
            GlassmorphicCard(borderColor = SecurityGreen.copy(alpha = 0.3f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "لا توجد مشاكل",
                        tint = SecurityGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "لم يتم رصد مشاكل بارزة في المؤشرات العامة للحساب. الملف يبدو مكتمل الأركان.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        )
                    )
                }
            }
        } else {
            issues.forEach { issue ->
                AuditIssueCard(issue = issue)
            }
        }
    }
}

@Composable
fun AuditIssueCard(
    issue: AuditIssue,
    modifier: Modifier = Modifier
) {
    val (severityColor, severityLabel) = when (issue.severity) {
        IssueSeverity.CRITICAL -> SecurityRed to "حرج"
        IssueSeverity.WARNING -> SecurityOrange to "تنبيه"
        IssueSeverity.INFO -> SecurityYellow to "ملاحظة"
    }

    GlassmorphicCard(
        modifier = modifier,
        borderColor = severityColor.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with severity badge and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(severityColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .border(1.dp, severityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = severityLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = severityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reason / Cause
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B1220), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "سبب الظهور والتشخيص:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = issue.reason,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Solution
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecurityGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .border(1.dp, SecurityGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "الحل المقترح والإجراء الموصى به:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecurityGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = issue.solution,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFF1F5F9),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }
        }
    }
}

@Composable
fun StrengthsSection(
    strengths: List<AuditStrength>,
    modifier: Modifier = Modifier
) {
    if (strengths.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("strengths_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "نقاط القوة",
                tint = SecurityGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "نقاط القوة والمؤشرات الإيجابية (${strengths.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            )
        }

        strengths.forEach { str ->
            GlassmorphicCard(borderColor = SecurityGreen.copy(alpha = 0.25f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SecurityGreen,
                        modifier = Modifier.size(18.dp)
                    )

                    Column {
                        Text(
                            text = str.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = str.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FactorsBreakdownSection(
    factors: List<ScoreFactor>,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = NeonCyan.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "تفاصيل توزيع نقاط نسبة الأمان",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    fontSize = 14.sp
                )
            )

            factors.forEach { factor ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = factor.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = factor.description,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${factor.earnedPoints} / ${factor.maxPoints}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityDisclaimerCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "تنويه أمني",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "تنويه هام: هذه النسبة تقديرية وتعبر عن صحة الحساب ومؤشراته العامة الظاهرة علناً، وليست نتيجة أمنية رسمية من شركة TikTok، ولا تثبت أن الحساب محمي من الاختراق أو محصن ضد الهجمات الإلكترونية.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}
