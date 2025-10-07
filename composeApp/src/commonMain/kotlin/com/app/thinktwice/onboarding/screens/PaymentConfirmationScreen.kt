package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar

@Composable
fun PaymentConfirmationScreen(
    selectedPlan: String,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 13,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = "Start saving now!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Feature 1
                FeatureItem(
                    icon = "💳",
                    title = "Start tracking your savings",
                    description = "Get access to our features to help you monitor and track your spending habits"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feature 2
                FeatureItem(
                    icon = "📈",
                    title = "Track your progress",
                    description = "See how your spending and savings impact your progress towards your goals"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Feature 3
                FeatureItem(
                    icon = "⊗",
                    title = "Cancel at any time",
                    description = "Have the flexibility to cancel at any point without the fear of a long term commitment!"
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Payment Plans - Side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monthly Plan
                    ConfirmPlanCard(
                        modifier = Modifier.weight(1f),
                        title = "Monthly",
                        price = "\$Y.YY/month",
                        badge = null,
                        headerText = "Selected",
                        headerColor = Color(0xFFFFD966),
                        isSelected = selectedPlan == "monthly",
                        onClick = { /* Already selected in previous screen */ }
                    )

                    // Yearly Plan
                    ConfirmPlanCard(
                        modifier = Modifier.weight(1f),
                        title = "Yearly",
                        price = "\$Y.YY/month",
                        badge = "XX% off!",
                        headerText = "Recommended!",
                        headerColor = Color(0xFF2C3E50),
                        isSelected = selectedPlan == "yearly",
                        showFreeTrial = true,
                        onClick = { /* Already selected in previous screen */ }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom button
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "Start saving!",
                    onClick = onContinueClick
                )
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Icon box
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFF2C3E50),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun ConfirmPlanCard(
    title: String,
    price: String,
    badge: String?,
    headerText: String,
    headerColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFreeTrial: Boolean = false
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(2.dp, if (isSelected) headerColor else Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = headerText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (headerColor == Color(0xFFFFD966)) Color.Black else Color.White
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = price,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                if (badge != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = badge,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )
                }

                if (showFreeTrial) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "7-day free trial",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}