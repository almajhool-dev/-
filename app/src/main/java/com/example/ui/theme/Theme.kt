package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002029),
    primaryContainer = Color(0xFF003844),
    onPrimaryContainer = Color(0xFF70F5FF),
    secondary = NeonBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF00357A),
    onSecondaryContainer = Color(0xFFCCE0FF),
    tertiary = CyberPurple,
    onTertiary = Color.White,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    error = SecurityRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // TK_SABR uses a dedicated Cyber Security dark theme for maximum contrast and high-tech styling
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
