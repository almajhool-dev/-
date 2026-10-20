package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan

@Composable
fun CyberBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_anim")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Deep Space Cyber Radial Gradient
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0F1A2C),
                    Color(0xFF090E17),
                    CyberBackground
                ),
                center = Offset(width * 0.5f, height * 0.3f),
                radius = width * 1.2f
            )
        )

        // 2. Subtle High-Tech Cyber Grid
        val gridSize = 64f
        val gridColor = NeonCyan.copy(alpha = 0.04f)
        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridSize
        }

        var y = 0f
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSize
        }

        // 3. Cyber Scanning Laser Beam (Subtle)
        val beamY = scanLineY * height
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    NeonCyan.copy(alpha = pulseGlow * 0.7f),
                    NeonBlue.copy(alpha = pulseGlow),
                    NeonCyan.copy(alpha = pulseGlow * 0.7f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, beamY),
            end = Offset(width, beamY),
            strokeWidth = 2.5f
        )
    }
}
