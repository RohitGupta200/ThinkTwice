package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar
import com.app.thinktwice.onboarding.components.SelectableGrid

@Composable
fun UserBehaviorsScreen(
    selectedTriggers: Set<String>,
    onTriggersChange: (Set<String>) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val triggerOptions = listOf(
        "Late nights",
        "Work breaks",
        "Weekends",
        "After Paydays",
        "Stressful days",
        "Bored moments",
        "Social media ads",
        "Sales"
    )

    val canContinue = selectedTriggers.isNotEmpty()

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 2,
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
                    text = "Tell us more about when you spend!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "When do you shop impulsively?\nWe'll send gentle reminders during these times",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Display options in single column with proper spacing
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    triggerOptions.forEach { trigger ->
                        com.app.thinktwice.onboarding.components.SelectableOption(
                            text = trigger,
                            isSelected = selectedTriggers.contains(trigger),
                            onClick = {
                                val newTriggers = if (selectedTriggers.contains(trigger)) {
                                    selectedTriggers - trigger
                                } else {
                                    selectedTriggers + trigger
                                }
                                onTriggersChange(newTriggers)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "Continue",
                    onClick = onContinueClick,
                    enabled = canContinue
                )
            }
        }
    }
}