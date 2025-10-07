package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar

@Composable
fun StatsScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = listOf(
        "60% of women make at least one impulse purchase every month.",
        "The average person spends $314/month on unplanned buys.",
        "70% of impulse purchases happen online— often within 10 minutes of seeing an item.",
        "Over 50% of people regret their impulse purchases within 24 hours."
    )

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 3,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Did you know?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "We'll send gentle reminders during these times",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    stats.forEach { stat ->
                        StatCard(text = stat)
                    }
                }

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

@Composable
private fun StatCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AvatarDisplay(
                size = AvatarSize.Small,
                state = AvatarState.Happy
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
        }
    }
}