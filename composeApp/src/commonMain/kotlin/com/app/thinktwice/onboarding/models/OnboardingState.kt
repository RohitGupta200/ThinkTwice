package com.app.thinktwice.onboarding.models

import kotlinx.serialization.Serializable

@Serializable
data class OnboardingState(
    val currentStep: Int = 1,
    val totalSteps: Int = 15,
    val isCompleted: Boolean = false,
    val userInfo: UserOnboardingData = UserOnboardingData(),
    val canGoBack: Boolean = true,
    val canSkipCurrentStep: Boolean = false
)

@Serializable
data class UserOnboardingData(
    // Basic Info (Step 1)
    val firstName: String = "",
    val gender: String = "",

    // User Behaviors (Step 2)
    val spendingTriggers: Set<String> = emptySet(),

    // Saving Goals (Step 4)
    val savingGoalType: String = "",

    // Saving Goal Details (Step 5)
    val goalName: String = "",
    val currency: String = "USD",
    val goalAmount: Double = 0.0,
    val goalCompletionDate: String = "",

    // Payday Details (Step 8)
    val paymentFrequency: String = "",
    val paymentDay: String = "",

    // Permissions (Step 9)
    val notificationsEnabled: Boolean = false,
    val storageEnabled: Boolean = false,
    val screentimeEnabled: Boolean = false,
    val overlayEnabled: Boolean = false,

    // App Selection (Step 10)
    val selectedApps: Set<String> = emptySet(),

    // Payment Plan (Steps 12-14)
    val selectedPlan: String = "",
    val acceptedDiscount: Boolean = false
)

enum class OnboardingStep(val stepNumber: Int, val title: String, val canSkip: Boolean = false) {
    BASIC_INFO(1, "Basic Information"),
    USER_BEHAVIORS(2, "User Behaviors"),
    STATS(3, "Statistics"),
    SAVING_GOALS(4, "Saving Goals"),
    GOAL_DETAILS(5, "Goal Details"),
    SAVINGS_BREAKDOWN(6, "Savings Breakdown"),
    CHARACTER_INTRO(7, "Character Introduction"),
    PAYDAY_DETAILS(8, "Payday Details", canSkip = true),
    NOTIFICATIONS(9, "Notifications"),
    APP_SELECTION(10, "App Selection"),
    SELECTION_OVERVIEW(11, "Selection Overview"),
    PAYMENT(12, "Payment"),
    PAYMENT_CONFIRMATION(13, "Payment Confirmation"),
    DISCOUNT(14, "Discount"),
    SUCCESS(15, "Success");

    companion object {
        fun fromStepNumber(stepNumber: Int): OnboardingStep? {
            return entries.find { it.stepNumber == stepNumber }
        }
    }
}

sealed class OnboardingEvent {
    data object NextStep : OnboardingEvent()
    data object PreviousStep : OnboardingEvent()
    data object SkipStep : OnboardingEvent()
    data class UpdateUserInfo(val userInfo: UserOnboardingData) : OnboardingEvent()
    data class JumpToStep(val step: Int) : OnboardingEvent()
    data object CompleteOnboarding : OnboardingEvent()
}

sealed class OnboardingUiState {
    data object Loading : OnboardingUiState()
    data class Ready(val state: OnboardingState) : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
    data object Completed : OnboardingUiState()
}