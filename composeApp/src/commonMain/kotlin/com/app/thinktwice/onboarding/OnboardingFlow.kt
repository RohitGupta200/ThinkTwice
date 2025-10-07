package com.app.thinktwice.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.app.thinktwice.applocking.platform.AppPlatformHelper
import com.app.thinktwice.onboarding.models.OnboardingEvent
import com.app.thinktwice.onboarding.models.OnboardingStep
import com.app.thinktwice.onboarding.models.OnboardingUiState
import com.app.thinktwice.onboarding.screens.AppSelectionScreen
import com.app.thinktwice.onboarding.screens.BasicInfoScreen
import com.app.thinktwice.onboarding.screens.CharacterIntroScreen
import com.app.thinktwice.onboarding.screens.DiscountScreen
import com.app.thinktwice.onboarding.screens.NotificationsScreen
import com.app.thinktwice.onboarding.screens.GenericOnboardingScreen
import com.app.thinktwice.onboarding.screens.PaydayDetailsScreen
import com.app.thinktwice.onboarding.screens.PaymentConfirmationScreen
import com.app.thinktwice.onboarding.screens.PaymentScreen
import com.app.thinktwice.onboarding.screens.SavingGoalDetailsScreen
import com.app.thinktwice.onboarding.screens.SavingGoalsScreen
import com.app.thinktwice.onboarding.screens.SavingsBreakdownScreen
import com.app.thinktwice.onboarding.screens.SelectionOverviewScreen
import com.app.thinktwice.onboarding.screens.StatsScreen
import com.app.thinktwice.onboarding.screens.SuccessScreen
import com.app.thinktwice.onboarding.screens.UserBehaviorsScreen
import com.app.thinktwice.onboarding.viewmodel.OnboardingViewModel

@Composable
expect fun rememberAppPlatformHelper(): AppPlatformHelper

@Composable
fun OnboardingFlow(
    viewModel: OnboardingViewModel = remember { OnboardingViewModel() },
    onOnboardingComplete: () -> Unit,
    onBackToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is OnboardingUiState.Loading -> {
            // Loading screen - could show spinner
        }
        is OnboardingUiState.Ready -> {
            val onboardingState = state.state
            val currentStep = OnboardingStep.fromStepNumber(onboardingState.currentStep)

            when (currentStep) {
                OnboardingStep.BASIC_INFO -> {
                    BasicInfoScreen(
                        firstName = onboardingState.userInfo.firstName,
                        gender = onboardingState.userInfo.gender,
                        onFirstNameChange = { name ->
                            viewModel.updateBasicInfo(name, onboardingState.userInfo.gender)
                        },
                        onGenderChange = { gender ->
                            viewModel.updateBasicInfo(onboardingState.userInfo.firstName, gender)
                        },
                        onBackClick = {
                            if (onboardingState.currentStep == 1) {
                                onBackToAuth()
                            } else {
                                viewModel.handleEvent(OnboardingEvent.PreviousStep)
                            }
                        },
                        onContinueClick = {
                            if (viewModel.canProceedFromCurrentStep()) {
                                viewModel.handleEvent(OnboardingEvent.NextStep)
                            }
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.USER_BEHAVIORS -> {
                    UserBehaviorsScreen(
                        selectedTriggers = onboardingState.userInfo.spendingTriggers,
                        onTriggersChange = { triggers ->
                            viewModel.updateSpendingTriggers(triggers)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            if (viewModel.canProceedFromCurrentStep()) {
                                viewModel.handleEvent(OnboardingEvent.NextStep)
                            }
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.STATS -> {
                    StatsScreen(
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.SAVING_GOALS -> {
                    SavingGoalsScreen(
                        selectedGoal = onboardingState.userInfo.savingGoalType,
                        onGoalChange = { goal ->
                            viewModel.updateSavingGoal(goal)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            if (viewModel.canProceedFromCurrentStep()) {
                                viewModel.handleEvent(OnboardingEvent.NextStep)
                            }
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.GOAL_DETAILS -> {
                    SavingGoalDetailsScreen(
                        selectedGoalType = onboardingState.userInfo.savingGoalType,
                        goalName = onboardingState.userInfo.goalName,
                        currency = onboardingState.userInfo.currency,
                        goalAmount = onboardingState.userInfo.goalAmount,
                        goalCompletionDate = onboardingState.userInfo.goalCompletionDate,
                        onGoalNameChange = { name ->
                            viewModel.updateGoalDetails(
                                name,
                                onboardingState.userInfo.currency,
                                onboardingState.userInfo.goalAmount,
                                onboardingState.userInfo.goalCompletionDate
                            )
                        },
                        onCurrencyChange = { currency ->
                            viewModel.updateGoalDetails(
                                onboardingState.userInfo.goalName,
                                currency,
                                onboardingState.userInfo.goalAmount,
                                onboardingState.userInfo.goalCompletionDate
                            )
                        },
                        onGoalAmountChange = { amount ->
                            viewModel.updateGoalDetails(
                                onboardingState.userInfo.goalName,
                                onboardingState.userInfo.currency,
                                amount,
                                onboardingState.userInfo.goalCompletionDate
                            )
                        },
                        onDateChange = { date ->
                            viewModel.updateGoalCompletionDate(date)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            if (viewModel.canProceedFromCurrentStep()) {
                                viewModel.handleEvent(OnboardingEvent.NextStep)
                            }
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.SAVINGS_BREAKDOWN -> {
                    SavingsBreakdownScreen(
                        goalAmount = onboardingState.userInfo.goalAmount,
                        goalName = onboardingState.userInfo.goalName,
                        userName = onboardingState.userInfo.firstName,
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.CHARACTER_INTRO -> {
                    CharacterIntroScreen(
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.PAYDAY_DETAILS -> {
                    PaydayDetailsScreen(
                        paymentFrequency = onboardingState.userInfo.paymentFrequency,
                        paymentDay = onboardingState.userInfo.paymentDay,
                        onFrequencyChange = { frequency ->
                            viewModel.updatePaydayDetails(frequency, "")  // Reset day when frequency changes
                        },
                        onDayChange = { day ->
                            viewModel.updatePaydayDetails(onboardingState.userInfo.paymentFrequency, day)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notificationsEnabled = onboardingState.userInfo.notificationsEnabled,
                        storageEnabled = onboardingState.userInfo.storageEnabled,
                        screentimeEnabled = onboardingState.userInfo.screentimeEnabled,
                        overlayEnabled = onboardingState.userInfo.overlayEnabled,
                        onNotificationsChange = { enabled ->
                            viewModel.updatePermissions(notifications = enabled)
                        },
                        onStorageChange = { enabled ->
                            viewModel.updatePermissions(storage = enabled)
                        },
                        onScreentimeChange = { enabled ->
                            viewModel.updatePermissions(screentime = enabled)
                        },
                        onOverlayChange = { enabled ->
                            viewModel.updatePermissions(overlay = enabled)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.APP_SELECTION -> {
                    val platformHelper = rememberAppPlatformHelper()
                    AppSelectionScreen(
                        platformHelper = platformHelper,
                        selectedApps = onboardingState.userInfo.selectedApps,
                        onAppsChange = { apps ->
                            viewModel.updateSelectedApps(apps)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.SELECTION_OVERVIEW -> {
                    SelectionOverviewScreen(
                        firstName = onboardingState.userInfo.firstName,
                        goalType = onboardingState.userInfo.savingGoalType,
                        goalAmount = onboardingState.userInfo.goalAmount,
                        currency = onboardingState.userInfo.currency,
                        selectedApps = onboardingState.userInfo.selectedApps,
                        spendingTriggers = onboardingState.userInfo.spendingTriggers,
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.PAYMENT -> {
                    PaymentScreen(
                        selectedPlan = onboardingState.userInfo.selectedPlan,
                        onPlanChange = { plan ->
                            viewModel.updatePaymentPlan(plan)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            if (viewModel.canProceedFromCurrentStep()) {
                                viewModel.handleEvent(OnboardingEvent.NextStep)
                            }
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.PAYMENT_CONFIRMATION -> {
                    PaymentConfirmationScreen(
                        selectedPlan = onboardingState.userInfo.selectedPlan,
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        onContinueClick = {
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.DISCOUNT -> {
                    DiscountScreen(
                        selectedPlan = onboardingState.userInfo.selectedPlan,
                        onAcceptDiscount = {
                            // Update to yearly plan with discount
                            viewModel.updatePaymentPlan("yearly", acceptedDiscount = true)
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        onDeclineDiscount = {
                            // Continue with current plan
                            viewModel.handleEvent(OnboardingEvent.NextStep)
                        },
                        onBackClick = {
                            viewModel.handleEvent(OnboardingEvent.PreviousStep)
                        },
                        modifier = modifier
                    )
                }

                OnboardingStep.SUCCESS -> {
                    SuccessScreen(
                        onCompleteOnboarding = {
                            viewModel.handleEvent(OnboardingEvent.CompleteOnboarding)
                        },
                        modifier = modifier
                    )
                }

                else -> {
                    // Use the generic screen for any remaining screens
                    currentStep?.let { step ->
                        GenericOnboardingScreen(
                            step = step,
                            onBackClick = {
                                viewModel.handleEvent(OnboardingEvent.PreviousStep)
                            },
                            onContinueClick = {
                                viewModel.handleEvent(OnboardingEvent.NextStep)
                            },
                            modifier = modifier
                        )
                    }
                }
            }
        }

        is OnboardingUiState.Error -> {
            // Error screen - could show error message with retry
        }

        is OnboardingUiState.Completed -> {
            onOnboardingComplete()
        }
    }
}