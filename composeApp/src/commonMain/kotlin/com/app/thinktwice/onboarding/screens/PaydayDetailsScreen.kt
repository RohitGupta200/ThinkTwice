package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.app.thinktwice.onboarding.components.OnboardingDropdown
import com.app.thinktwice.onboarding.components.OnboardingTopBar
import com.app.thinktwice.onboarding.components.SelectableOption

@Composable
fun PaydayDetailsScreen(
    paymentFrequency: String,
    paymentDay: String,
    onFrequencyChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val frequencyOptions = listOf("Weekly", "Every 2 weeks", "Monthly", "Unscheduled")

    // Updated day/date options based on frequency
    val (dayLabel, dayOptions) = when (paymentFrequency) {
        "Weekly" -> "What day of the week?" to listOf(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        )
        "Every 2 weeks" -> "Which days?" to listOf(
            "Every other Friday", "1st & 15th of month", "15th & 30th of month"
        )
        "Monthly" -> "What date of the month?" to listOf(
            "1st", "5th", "10th", "15th", "20th", "25th", "30th", "Last day of month"
        )
        else -> "" to emptyList()
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 8,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "When do you get paid?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We'll set certain reminders accordingly",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Avatar Section
                AvatarDisplay(
                    size = AvatarSize.Large,
                    state = AvatarState.Happy
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Payment Frequency Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    frequencyOptions.forEach { frequency ->
                        SelectableOption(
                            text = frequency,
                            isSelected = paymentFrequency == frequency,
                            onClick = {
                                onFrequencyChange(frequency)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Payment Day/Date Section
                if (paymentFrequency.isNotBlank() && paymentFrequency != "Unscheduled" && dayOptions.isNotEmpty()) {
                    Text(
                        text = dayLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OnboardingDropdown(
                        options = dayOptions,
                        selectedOption = paymentDay.ifBlank { "" },
                        onSelectionChange = onDayChange,
                        placeholder = when (paymentFrequency) {
                            "Weekly" -> "Friday"
                            "Every 2 weeks" -> "Select option"
                            "Monthly" -> "15th"
                            else -> "Select"
                        }
                    )
                }

                // Show a message for Unscheduled
                if (paymentFrequency == "Unscheduled") {
                    Text(
                        text = "No problem! We'll help you save whenever you get paid.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "Continue",
                    onClick = onContinueClick,
                    enabled = paymentFrequency.isNotBlank() && (
                        paymentFrequency == "Unscheduled" || paymentDay.isNotBlank()
                    )
                )
            }
        }
    }
}