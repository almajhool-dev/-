package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.SecurityGreen
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.SecurityRed
import com.example.ui.theme.SecurityYellow
import kotlin.math.roundToInt

@Composable
fun CyberSecurityGauge(
    score: Int,
    ratingText: String,
    modifier: Modifier = Modifier
) {
    val targetPercent = (score.coerceIn(0, 100) / 100f)
    val animatedPercent by animateFloatAsState(
        targetValue = targetPercent,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "gauge_sweep"
    )

    // Dynamic color calculation based on score specifications:
    // 0–39%: أحمر.
    // 40–69%: برتقالي.
    // 70–89%: أصفر.
    // 90–100%: أخضر.
    val scoreColor = when (score) {
        in 90..100 -> SecurityGreen
        in 70..89 -> SecurityYellow
        in 40..69 -> SecurityOrange
        else -> SecurityRed
    }

    val displayScore = (animatedPercent * 100).roundToInt()

    Column(
        modifier = modifier
            .testTag("security_gauge_container")
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "نسبة أمان وصحة الحساب",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE2E8F0),
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.size(190.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas Arc
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = center

                // Inactive Track Arc (240 degrees from 150 deg to 390 deg)
                drawArc(
                    color = Color(0xFF162032),
                    startAngle = 150f,
                    sweepAngle = 240f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active Glowing Cyber Arc
                val sweepAngle = animatedPercent * 240f
                if (sweepAngle > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to scoreColor.copy(alpha = 0.6f),
                            0.5f to scoreColor,
                            1.0f to scoreColor
                        ),
                        startAngle = 150f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Inner Score Disc & Numbers
            Column(
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
                    .background(CyberSurface.copy(alpha = 0.85f))
                    .border(1.dp, scoreColor.copy(alpha = 0.35f), CircleShape),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$displayScore%",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = scoreColor
                    ),
                    modifier = Modifier.testTag("security_score_text")
                )

                Text(
                    text = "مؤشر التقييم",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Badge Rating Pill
        Box(
            modifier = Modifier
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), spotColor = scoreColor)
                .background(scoreColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .border(1.dp, scoreColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = ratingText,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = scoreColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
