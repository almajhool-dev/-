package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.NeonCyan

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    borderColor: Color = NeonCyan.copy(alpha = 0.22f),
    cornerRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color(0xFF000000),
                spotColor = borderColor.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberCardBg,
                        Color(0xF00A1220)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.55f),
                        borderColor.copy(alpha = 0.15f),
                        borderColor.copy(alpha = 0.45f)
                    )
                ),
                shape = shape
            )
            .padding(16.dp),
        content = content
    )
}
