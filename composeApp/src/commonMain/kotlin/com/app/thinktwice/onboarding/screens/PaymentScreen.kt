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
fun PaymentScreen(
    selectedPlan: String,
    onPlanChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 12,
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
                    text = "Start your 7-day free trial now!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Timeline Section
                TimelineItem(
                    day = "27",
                    title = "Right Now",
                    description = "Unlock access to the application and all the built in features for blocking, stats, goal setting and more!"
                )

                Spacer(modifier = Modifier.height(24.dp))

                TimelineItem(
                    day = "30",
                    title = "In 3 days",
                    description = "You will be sent a reminder about your trail period expiring"
                )

                Spacer(modifier = Modifier.height(24.dp))

                TimelineItem(
                    day = "3",
                    title = "In 7 days",
                    description = "You'll be charged on 3rd September, 2025 for your annual membership unless you cancel and any time before the trial expires"
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Payment Plans - Side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Monthly Plan
                    PlanCard(
                        modifier = Modifier.weight(1f),
                        title = "Monthly",
                        price = "\$xx.xx/month",
                        badge = null,
                        showFreeTrial = false,
                        isSelected = selectedPlan == "monthly",
                        onClick = { onPlanChange("monthly") }
                    )

                    // Yearly Plan
                    PlanCard(
                        modifier = Modifier.weight(1f),
                        title = "Yearly",
                        price = "\$Y.YY/month",
                        badge = "XX% off!",
                        showFreeTrial = true,
                        isSelected = selectedPlan == "yearly",
                        onClick = { onPlanChange("yearly") }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom section
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "Try it risk free for 7-days!",
                    onClick = onContinueClick,
                    enabled = selectedPlan.isNotBlank()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "7 days free, then \$xx.xx per year",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(
    day: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // Calendar icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFFFF6B6B),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
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
private fun PlanCard(
    title: String,
    price: String,
    badge: String?,
    showFreeTrial: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color(0xFFFFD966) else Color.White
    val borderColor = if (isSelected) Color(0xFFFFD966) else Color(0xFFE0E0E0)

    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSelected) {
                Text(
                    text = "Selected",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

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