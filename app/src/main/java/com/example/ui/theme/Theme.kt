package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    secondary = SecondaryAccent,
    tertiary = RosePremium,
    background = DeepSlateBackground,
    surface = CardSlateBackground,
    surfaceVariant = DarkGreyBackground,
    onPrimary = WhitePrimary,
    onBackground = WhitePrimary,
    onSurface = WhitePrimary,
    onSurfaceVariant = GraySecondary
)

private val LightColorScheme = lightColorScheme(
    primary = SecondaryAccent,
    secondary = PrimaryAccent,
    tertiary = RosePremium,
    background = WhitePrimary,
    surface = Color(0xFFF1F5F9), // slate 100
    surfaceVariant = Color(0xFFE2E8F0), // slate 200
    onPrimary = DeepSlateBackground,
    onBackground = DeepSlateBackground,
    onSurface = DeepSlateBackground,
    onSurfaceVariant = DeepSlateBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force premium dark by default
    dynamicColor: Boolean = false, // Disable default system tinting to preserve premium custom branding
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
