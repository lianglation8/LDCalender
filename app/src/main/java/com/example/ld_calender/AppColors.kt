package com.example.ld_calender

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Holds the user-selected global colors.
 * - fontColor: color used for all text / primary accents
 * - bgColor:   color used for all screen backgrounds / card backgrounds
 */
data class AppColors(
    val fontColor: Color,
    val bgColor: Color
)

val DefaultAppColors = AppColors(
    fontColor = Color(0xFF2E7D32),   // deep green
    bgColor = Color(0xFFF7F8FA)      // light gray
)

val LocalAppColors = staticCompositionLocalOf { DefaultAppColors }

@Composable
fun appFontColor(): Color = LocalAppColors.current.fontColor

@Composable
fun appBgColor(): Color = LocalAppColors.current.bgColor