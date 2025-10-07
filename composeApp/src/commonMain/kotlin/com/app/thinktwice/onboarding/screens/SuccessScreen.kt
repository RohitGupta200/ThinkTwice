package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar

@Composable
fun SuccessScreen(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Different background gradient for success screen - light mint/green
    val successGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8F5E8), // Light mint green
            Color(0xFFF0F8F0), // Very light green
            Color(0xFFFAFAFA)  // Almost white
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = successGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Progress bar only (no back button)
            OnboardingTopBar(
                currentStep = 15,
                onBackClick = { /* No back on success */ },
                showBackButton = false
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Avatar with confetti effect
                AvatarDisplay(
                    size = AvatarSize.Large,
                    state = AvatarState.Excited
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Title
                Text(
                    text = "Thank you for the purchase!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = "You've taken your first step to taking control over your spending habits!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            // Bottom section
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OnboardingContinueButton(
                    text = "Let's start saving!",
                    onClick = onCompleteOnboarding
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your free trial has been activated",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}