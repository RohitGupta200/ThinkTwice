package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar

@Composable
fun SavingsBreakdownScreen(
    goalAmount: Double,
    goalName: String,
    userName: String,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysToGoal = 350 // Based on design
    val monthlyAmount = goalAmount / (daysToGoal / 30.0)
    val weeklyAmount = goalAmount / (daysToGoal / 7.0)
    val dailyAmount = goalAmount / daysToGoal

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 6,
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
                    text = "Here's what to expect if you maintain your habits!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BreakdownCard(
                        label = "Your Goal",
                        amount = "$$${goalAmount.toInt()}",
                        isHighlighted = false
                    )

                    BreakdownCard(
                        label = "Monthly",
                        amount = "$$${monthlyAmount.toInt()}",
                        isHighlighted = false
                    )

                    BreakdownCard(
                        label = "Weekly",
                        amount = "$$${weeklyAmount.toInt()}",
                        isHighlighted = false
                    )

                    BreakdownCard(
                        label = "Daily",
                        amount = "$$${dailyAmount.toInt()}",
                        isHighlighted = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "You just have to take small steps to hit your targets!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$userName, you just have to save $$${ dailyAmount.toInt()} a day to achieve your goals!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
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

@Composable
private fun BreakdownCard(
    label: String,
    amount: String,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isHighlighted) Color(0xFFFFE4CC) else Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 2.dp,
                color = Color(0xFF2C2C2C),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )

            Text(
                text = amount,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}