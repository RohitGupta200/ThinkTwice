package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar

@Composable
fun CharacterIntroScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableStateOf(1f) }

    // Map slider position to avatar state
    val avatarState = when {
//        sliderPosition < 0.25f -> AvatarState.Sad
        sliderPosition < 0.5f -> AvatarState.Neutral
        sliderPosition < 0.75f -> AvatarState.Happy
        else -> AvatarState.Happy // Wealthy state
    }

    val stateLabel = when {
        sliderPosition < 0.25f -> "Poor"
        sliderPosition < 0.5f -> "Okay"
        sliderPosition < 0.75f -> "Good"
        else -> "Wealthy"
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 7,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Large Avatar Display
                AvatarDisplay(
                    size = AvatarSize.Large,
                    state = avatarState
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Slider
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color(0xFF2D5F3F),
                        inactiveTrackColor = Color(0xFFD1D1D1)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // State Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF2D5F3F),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stateLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Meet Mini Kunal!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mini Kunal will be your avatar and will let you know how they feel based on your spending habits!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "They will be shown in various states to serve as a visual indicator of how they feel.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Feel free to move the slider around to see the various states!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "Continue",
                    onClick = onContinueClick
                )
            }
        }
    }
}