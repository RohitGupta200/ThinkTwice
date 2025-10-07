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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTextField
import com.app.thinktwice.onboarding.components.OnboardingTopBar
import com.app.thinktwice.onboarding.components.SelectableOption

@Composable
fun BasicInfoScreen(
    firstName: String,
    gender: String,
    onFirstNameChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val genderOptions = listOf("Female", "Male", "Non-Binary", "Other")
    val canContinue = firstName.isNotBlank() && gender.isNotBlank()

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 1,
                onBackClick = onBackClick,
                showBackButton = false
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Help us understand you better",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "What's your first name?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Underlined text field matching PDF design
                Column {
                    BasicTextField(
                        value = firstName,
                        onValueChange = onFirstNameChange,
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) { innerTextField ->
                        if (firstName.isEmpty()) {
                            Text(
                                text = "Enter your first name",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.3f)
                            )
                        }
                        innerTextField()
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "What do you identify as?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    genderOptions.forEach { genderOption ->
                        SelectableOption(
                            text = genderOption,
                            isSelected = gender == genderOption,
                            onClick = {
                                onGenderChange(genderOption)
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