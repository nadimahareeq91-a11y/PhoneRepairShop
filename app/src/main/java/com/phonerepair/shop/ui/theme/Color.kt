package com.phonerepair.shop.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand Colors - Modern Teal/Green theme for repair shop
private val Primary = Color(0xFF009688)
private val PrimaryContainer = Color(0xFFB2DFDB)
private val Secondary = Color(0xFF00695C)
private val SecondaryContainer = Color(0xFF80CBC4)
private val Tertiary = Color(0xFF2E7D32)
private val TertiaryContainer = Color(0xFFA5D6A7)
private val Error = Color(0xFFC62828)
private val ErrorContainer = Color(0xFFEF9A9A)
private val Surface = Color(0xFFFAFAFA)
private val SurfaceVariant = Color(0xFFF5F5F5)
private val Background = Color(0xFFFFFFFF)
private val OnPrimary = Color(0xFFFFFFFF)
private val OnSecondary = Color(0xFFFFFFFF)
private val OnSurface = Color(0xFF1A1A2E)
private val OnBackground = Color(0xFF1A1A2E)
private val Outline = Color(0xFF757575)
private val OutlineVariant = Color(0xFFBDBDBD)

private val DarkPrimary = Color(0xFF4DB6AC)
private val DarkPrimaryContainer = Color(0xFF00695C)
private val DarkSecondary = Color(0xFF80CBC4)
private val DarkSecondaryContainer = Color(0xFF004D40)
private val DarkTertiary = Color(0xFF66BB6A)
private val DarkTertiaryContainer = Color(0xFF1B5E20)
private val DarkError = Color(0xFFEF5350)
private val DarkErrorContainer = Color(0xFFB71C1C)
private val DarkSurface = Color(0xFF1A1A2E)
private val DarkSurfaceVariant = Color(0xFF27273E)
private val DarkBackground = Color(0xFF121212)
private val DarkOnPrimary = Color(0xFF000000)
private val DarkOnSecondary = Color(0xFF000000)
private val DarkOnSurface = Color(0xFFE0E0E0)
private val DarkOnBackground = Color(0xFFE0E0E0)
private val DarkOutline = Color(0xFF9E9E9E)
private val DarkOutlineVariant = Color(0xFF616161)

val LightColorScheme = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryContainer,
    secondary = Secondary,
    secondaryContainer = SecondaryContainer,
    tertiary = Tertiary,
    tertiaryContainer = TertiaryContainer,
    error = Error,
    errorContainer = ErrorContainer,
    surface = Surface,
    surfaceVariant = SurfaceVariant,
    background = Background,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onSurface = OnSurface,
    onBackground = OnBackground,
    outline = Outline,
    outlineVariant = OutlineVariant,
    surfaceTint = Primary,
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = DarkPrimary,
    scrim = Color.Black
)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkSecondary,
    secondaryContainer = DarkSecondaryContainer,
    tertiary = DarkTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    error = DarkError,
    errorContainer = DarkErrorContainer,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    background = DarkBackground,
    onPrimary = DarkOnPrimary,
    onSecondary = DarkOnSecondary,
    onSurface = DarkOnSurface,
    onBackground = DarkOnBackground,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    surfaceTint = DarkPrimary,
    inverseSurface = Surface,
    inverseOnSurface = OnSurface,
    inversePrimary = Primary,
    scrim = Color.Black
)
