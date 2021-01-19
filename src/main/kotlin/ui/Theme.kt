package ui.common

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

object AppTheme {
    val colors: Colors = Colors()

    class Colors(
        val backgroundDark: Color = Color(0xFF2B2B2B),
        val backgroundMedium: Color = Color(0xFF3C3F41),
        val backgroundLight: Color = Color(0xFF4E5254),

        val material: androidx.compose.material.Colors = darkColors(
            background = backgroundDark,
            surface = backgroundMedium,
            primary = Color.White
        ),

        val DarkGreen: Color = Color(16, 139, 102),
        val Gray: Color = Color.DarkGray,
        val White: Color = Color.White,
        val LightGray: Color = Color(100, 100, 100),
        val DarkGray: Color = Color(32, 32, 32),
        val PreviewImageAreaHoverColor: Color = Color(45, 45, 45),
        val ToastBackground: Color = Color(23, 23, 23),
        val MiniatureColor: Color = Color(50, 50, 50),
        val MiniatureHoverColor: Color = Color(55, 55, 55),
        val Foreground: Color = Color(210, 210, 210),
        val TranslucentBlack: Color = Color(0, 0, 0, 60),
        val TranslucentWhite: Color = Color(255, 255, 255, 20),
        val Transparent: Color = Color.Transparent
    )
}