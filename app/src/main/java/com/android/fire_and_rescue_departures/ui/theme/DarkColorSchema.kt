package com.android.fire_and_rescue_departures.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme

val md_theme_dark_background = Color(0xFF181A20)
val md_theme_dark_surface = Color(0xFF23262F)
val md_theme_dark_surfaceVariant = Color(0xFF323645)

// Text colors
val md_theme_dark_onBackground = Color(0xFFF3F6FB)
val md_theme_dark_onSurface = Color(0xFFECECEC)
val md_theme_dark_onSurfaceVariant = Color(0xFFB0B8C1)

// Indigo/blue accents
val md_theme_dark_primary = Color(0xFF82A1F8)
val md_theme_dark_onPrimary = Color(0xFF181A20)
val md_theme_dark_primaryContainer = Color(0xFF2D3A68)
val md_theme_dark_onPrimaryContainer = Color(0xFFDDE7FF)

val md_theme_dark_secondary = Color(0xFF90CAF9)
val md_theme_dark_onSecondary = Color(0xFF181A20)
val md_theme_dark_secondaryContainer = Color(0xFF274472)
val md_theme_dark_onSecondaryContainer = Color(0xFFDDE7FF)

// Tertiary (alerts/icons)
val md_theme_dark_tertiary = Color(0xFFFF6B6B)
val md_theme_dark_onTertiary = Color(0xFFFFFFFF)
val md_theme_dark_tertiaryContainer = Color(0xFF8B0000)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFE5E5)

// Error
val md_theme_dark_error = Color(0xFFFF5370)
val md_theme_dark_errorContainer = Color(0xFF8B0000)
val md_theme_dark_onError = Color(0xFFFFFFFF)
val md_theme_dark_onErrorContainer = Color(0xFFFFE5E5)

// Outline, scrim, etc.
val md_theme_dark_outline = Color(0xFF4F5B62)
val md_theme_dark_outlineVariant = Color(0xFF6C7A89)
val md_theme_dark_scrim = Color(0xFF000000)
val md_theme_dark_inverseOnSurface = Color(0xFF23262F)
val md_theme_dark_inverseSurface = Color(0xFFF3F6FB)
val md_theme_dark_inversePrimary = md_theme_dark_primary
val md_theme_dark_surfaceTint = md_theme_dark_primary

val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)
