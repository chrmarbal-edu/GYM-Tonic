package edu.gymtonic_app.ui.theme

import androidx.compose.ui.graphics.Color

data class AppColors(
    val isDark: Boolean,

    // Gradient background (used by all screens)
    val gradientColors: List<Color>,

    // Surfaces
    val surfaceMain: Color,       // Big rounded card panel
    val surfaceCard: Color,       // Inner secondary cards
    val surfaceAccent: Color,     // Purple accent section cards
    val surfaceBottomNav: Color,  // Bottom navigation bar
    val surfaceDanger: Color,     // Danger/delete zone card

    // Text
    val textPrimary: Color,       // Main text on surfaces
    val textSecondary: Color,     // Subtitle / helper text
    val textSubtle: Color,        // Hint / caption text
    val textOnGradient: Color,    // Text on gradient background
    val textOnAccent: Color,      // Text on accent (purple) sections

    // Navigation
    val navSelectedTint: Color,
    val navUnselectedTint: Color,

    // Brand accent
    val accent: Color,
    val accentDark: Color,

    // Text field underline indicators
    val fieldIndicator: Color,
)

val LightColors = AppColors(
    isDark = false,
    gradientColors = listOf(Color(0xFF1F3F73), Color(0xFF3A2F7A), Color(0xFF2A3344)),
    surfaceMain = Color(0xFFD9D9D9),
    surfaceCard = Color(0xFFE9EBF2),
    surfaceAccent = Color(0xFF8B8EEA),
    surfaceBottomNav = Color(0xFFF3F4F8),
    surfaceDanger = Color(0xFFFDE9E9),
    textPrimary = Color(0xFF1D1D1D),
    textSecondary = Color(0xFF5D6270),
    textSubtle = Color(0xFF464A57),
    textOnGradient = Color.White,
    textOnAccent = Color(0xFF1D1D1D),
    navSelectedTint = Color(0xFF111111),
    navUnselectedTint = Color(0xFF4A4A4A),
    accent = Color(0xFF3B4EE8),
    accentDark = Color(0xFF2C3ED6),
    fieldIndicator = Color(0xFF2D2D2D),
)

val DarkColors = AppColors(
    isDark = true,
    gradientColors = listOf(Color(0xFF08102A), Color(0xFF130D30), Color(0xFF0C1018)),
    surfaceMain = Color(0xFF1C1C2E),
    surfaceCard = Color(0xFF252538),
    surfaceAccent = Color(0xFF3A3D8E),
    surfaceBottomNav = Color(0xFF14142A),
    surfaceDanger = Color(0xFF2E1010),
    textPrimary = Color(0xFFE8E8F0),
    textSecondary = Color(0xFF9898B0),
    textSubtle = Color(0xFF8080A0),
    textOnGradient = Color(0xFFE8E8F0),
    textOnAccent = Color(0xFFE8E8F0),
    navSelectedTint = Color(0xFFE0E0F0),
    navUnselectedTint = Color(0xFF8080A0),
    accent = Color(0xFF6B7EFF),
    accentDark = Color(0xFF5B6EF0),
    fieldIndicator = Color(0xFFB0B0D0),
)
