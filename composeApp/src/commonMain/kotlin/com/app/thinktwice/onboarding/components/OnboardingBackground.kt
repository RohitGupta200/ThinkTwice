package com.app.thinktwice.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object OnboardingColors {
    val GradientTopLeft = Color(0xFFFAD89C)     // #FAD89C
    val GradientTopRight = Color(0xFFF9C5BD)    // #F9C5BD
    val GradientBottomLeft = Color(0xFFF9BABA)  // #F9BABA
    val GradientBottomRight = Color(0xFFF7C56B) // #F7C56B

    val SelectionBorderDefault = Color(0xFFEBEBF2)
    val SelectionBorderSelected = Color(0xFFAF8C4C)
    val SelectionBackgroundSelected = Color(0xFFFAD89C)

    val ButtonGradientStart = Color(0xFF323241)
    val ButtonGradientEnd = Color(0xFFF7C56B)
}

@Composable
fun OnboardingBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        OnboardingColors.GradientTopLeft,
                        OnboardingColors.GradientTopRight,
                        OnboardingColors.GradientBottomLeft,
                        OnboardingColors.GradientBottomRight
                    ),
                    radius = 800f
                )
            )
    ) {
        content()
    }
}