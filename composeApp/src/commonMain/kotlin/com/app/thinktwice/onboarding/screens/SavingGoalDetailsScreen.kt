package com.app.thinktwice.onboarding.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingDropdown
import com.app.thinktwice.onboarding.components.OnboardingTextField
import com.app.thinktwice.onboarding.components.OnboardingTopBar
import com.app.thinktwice.onboarding.components.SelectableGrid

@Composable
fun SavingGoalDetailsScreen(
    selectedGoalType: String,
    goalName: String,
    currency: String,
    goalAmount: Double,
    goalCompletionDate: String,
    onGoalNameChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onGoalAmountChange: (Double) -> Unit,
    onDateChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyOptions = listOf("USD ($)", "EUR (€)", "GBP (£)", "CAD ($)", "AUD ($)")
    val amountOptions = listOf(1500.0, 3500.0, 5000.0, 10000.0)

    var customAmount by remember { mutableStateOf(goalAmount.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Update custom amount when goalAmount changes
    if (goalAmount > 0 && customAmount != goalAmount.toString()) {
        customAmount = goalAmount.toString()
    }

    // Helper function to format currency
    val currencySymbol = when (currency) {
        "EUR (€)" -> "€"
        "GBP (£)" -> "£"
        else -> "$"
    }

    val formatAmount = { amount: Double ->
        when (currency) {
            "EUR (€)" -> "€ ${amount.toInt()}"
            "GBP (£)" -> "£ ${amount.toInt()}"
            else -> "$ ${amount.toInt()}"
        }
    }

    val canContinue = goalName.isNotBlank() && goalAmount > 0

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 5,
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
                    text = "That's wonderful! Could you tell us more about your habits?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "What brings you here?\nSetting a goal makes saving 3x more effective",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selected Goal Card (golden background with X button)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFFAD89C),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedGoalType,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "✕",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Avatar and Title Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AvatarDisplay(
                        size = AvatarSize.Medium,
                        state = AvatarState.Happy
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Savings Goals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Lets set your savings goals!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Currency Section
                Text(
                    text = "What's your currency?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                OnboardingDropdown(
                    options = currencyOptions,
                    selectedOption = if (currency.isNotBlank()) currency else "USD ($)",
                    onSelectionChange = onCurrencyChange,
                    placeholder = "USD ($)"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Goal Name Section
                Text(
                    text = "Lets set a name for your goal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                OnboardingTextField(
                    value = goalName,
                    onValueChange = onGoalNameChange,
                    placeholder = "Write Your Goal"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Goal Amount Section
                Text(
                    text = "Set a savings goal amount",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Editable Amount Field - White rounded box with formatted currency
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = if (goalAmount > 0) {
                            when (currency) {
                                "EUR (€)" -> "€ ${goalAmount.toInt()}"
                                "GBP (£)" -> "£ ${goalAmount.toInt()}"
                                else -> "$ ${goalAmount.toInt()}"
                            }
                        } else {
                            "$ 0"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Selection Grid - Quick select buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    amountOptions.forEach { amount ->
                        val isSelected = goalAmount == amount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) Color(0xFFFAD89C) else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onGoalAmountChange(amount)
                                    customAmount = amount.toString()
                                }
                                .padding(vertical = 12.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (currency) {
                                    "EUR (€)" -> "€ ${amount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1,")}"
                                    "GBP (£)" -> "£ ${amount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1,")}"
                                    else -> "$ ${amount.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1,")}"
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Section
                Text(
                    text = "Complete the goal by",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date field - white rounded box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = if (goalCompletionDate.isNotBlank()) goalCompletionDate else "Select date",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (goalCompletionDate.isNotBlank()) Color.Black else Color.Black.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Daily Savings Calculation Text
                if (goalAmount > 0 && goalCompletionDate.isNotBlank()) {
                    // Calculate days to goal based on selected date
                    val daysToGoal = when {
                        goalCompletionDate.contains("03/05/2025") -> 90  // 3 months
                        goalCompletionDate.contains("06/28/2025") -> 180 // 6 months
                        goalCompletionDate.contains("09/28/2025") -> 365 // 1 year
                        goalCompletionDate.contains("03/28/2026") -> 547 // 18 months
                        goalCompletionDate.contains("09/28/2026") -> 730 // 2 years
                        goalCompletionDate.contains("09/28/2027") -> 1095 // 3 years
                        else -> 365 // Default to 1 year
                    }
                    val dailyAmount = goalAmount / daysToGoal
                    val formattedDaily = when (currency) {
                        "EUR (€)" -> "€${dailyAmount.toInt()}"
                        "GBP (£)" -> "£${dailyAmount.toInt()}"
                        else -> "$$${dailyAmount.toInt()}"
                    }

                    Text(
                        text = "You just have to save around $formattedDaily a day to hit your goal on time!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
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
                    enabled = canContinue
                )
            }
        }
    }

    // Simple Date Picker Dialog
    if (showDatePicker) {
        SimpleDatePickerDialog(
            onDateSelected = { selectedDate ->
                onDateChange(selectedDate)
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun SimpleDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Provide realistic date options for goal completion
    val dateOptions = listOf(
        "3 months (03/05/2025)",
        "6 months (06/28/2025)",
        "1 year (09/28/2025)",
        "18 months (03/28/2026)",
        "2 years (09/28/2026)",
        "3 years (09/28/2027)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "When do you want to reach your goal?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dateOptions.forEach { dateOption ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Extract just the date part (MM/dd/yyyy)
                                val date = dateOption.substringAfter("(").substringBefore(")")
                                onDateSelected(date)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8F9FA)
                        )
                    ) {
                        Text(
                            text = dateOption,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    )
}