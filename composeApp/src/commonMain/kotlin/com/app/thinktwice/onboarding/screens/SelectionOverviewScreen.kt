package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar

@Composable
fun SelectionOverviewScreen(
    firstName: String,
    goalType: String,
    goalAmount: Double,
    currency: String,
    selectedApps: Set<String>,
    spendingTriggers: Set<String>,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencySymbol = when (currency) {
        "EUR (€)" -> "€"
        "GBP (£)" -> "£"
        else -> "$"
    }

    // Format goal amount with currency symbol
    val formattedAmount = "$currencySymbol${goalAmount.toInt()}"

    // Calculate days (this would come from the actual date difference in real implementation)
    val days = "350 days"

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 11,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Let's get this journey started!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Avatar - centered and larger
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AvatarDisplay(
                        size = AvatarSize.Large,
                        state = AvatarState.Happy
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // First description paragraph with blue highlights
                val firstParagraph = buildAnnotatedString {
                    append("Mini Kunal will help you save ")
                    withStyle(style = SpanStyle(color = Color(0xFF00A8E8), fontWeight = FontWeight.Bold)) {
                        append(formattedAmount)
                    }
                    append(" over the next ")
                    withStyle(style = SpanStyle(color = Color(0xFF00A8E8), fontWeight = FontWeight.Bold)) {
                        append(days)
                    }
                    append(" for your ")
                    withStyle(style = SpanStyle(color = Color(0xFF00A8E8), fontWeight = FontWeight.Bold)) {
                        append("$goalType!")
                    }
                }

                Text(
                    text = firstParagraph,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Second description paragraph with blue highlights
                val secondParagraph = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF00A8E8), fontWeight = FontWeight.Bold)) {
                        append("Mini Kunal")
                    }
                    append(" will make sure that you build the habit to make him ready for the ")
                    withStyle(style = SpanStyle(color = Color(0xFF00A8E8), fontWeight = FontWeight.Bold)) {
                        append("$goalType.")
                    }
                }

                Text(
                    text = secondParagraph,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom button
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "I'm ready!",
                    onClick = onContinueClick
                )
            }
        }
    }
}