package com.app.thinktwice.onboarding.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.app.thinktwice.onboarding.models.OnboardingEvent
import com.app.thinktwice.onboarding.models.OnboardingState
import com.app.thinktwice.onboarding.models.OnboardingStep
import com.app.thinktwice.onboarding.models.OnboardingUiState
import com.app.thinktwice.onboarding.models.UserOnboardingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel {
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Loading)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private var _onboardingState by mutableStateOf(OnboardingState())
    val onboardingState: OnboardingState get() = _onboardingState

    init {
        _uiState.value = OnboardingUiState.Ready(_onboardingState)
    }

    fun handleEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NextStep -> nextStep()
            is OnboardingEvent.PreviousStep -> previousStep()
            is OnboardingEvent.SkipStep -> skipStep()
            is OnboardingEvent.UpdateUserInfo -> updateUserInfo(event.userInfo)
            is OnboardingEvent.JumpToStep -> jumpToStep(event.step)
            is OnboardingEvent.CompleteOnboarding -> completeOnboarding()
        }
    }

    private fun nextStep() {
        val currentStep = _onboardingState.currentStep
        if (currentStep < _onboardingState.totalSteps) {
            val newStep = currentStep + 1
            _onboardingState = _onboardingState.copy(
                currentStep = newStep,
                canGoBack = true,
                canSkipCurrentStep = OnboardingStep.fromStepNumber(newStep)?.canSkip ?: false
            )
            updateUiState()
        } else {
            completeOnboarding()
        }
    }

    private fun previousStep() {
        val currentStep = _onboardingState.currentStep
        if (currentStep > 1) {
            val newStep = currentStep - 1
            _onboardingState = _onboardingState.copy(
                currentStep = newStep,
                canGoBack = newStep > 1,
                canSkipCurrentStep = OnboardingStep.fromStepNumber(newStep)?.canSkip ?: false
            )
            updateUiState()
        }
    }

    private fun skipStep() {
        val currentStepInfo = OnboardingStep.fromStepNumber(_onboardingState.currentStep)
        if (currentStepInfo?.canSkip == true) {
            nextStep()
        }
    }

    private fun jumpToStep(step: Int) {
        if (step in 1.._onboardingState.totalSteps) {
            _onboardingState = _onboardingState.copy(
                currentStep = step,
                canGoBack = step > 1,
                canSkipCurrentStep = OnboardingStep.fromStepNumber(step)?.canSkip ?: false
            )
            updateUiState()
        }
    }

    private fun updateUserInfo(userInfo: UserOnboardingData) {
        _onboardingState = _onboardingState.copy(userInfo = userInfo)
        updateUiState()
    }

    private fun completeOnboarding() {
        _onboardingState = _onboardingState.copy(isCompleted = true)
        _uiState.value = OnboardingUiState.Completed
    }

    private fun updateUiState() {
        _uiState.value = OnboardingUiState.Ready(_onboardingState)
    }

    // Convenience methods for updating specific user data
    fun updateBasicInfo(firstName: String, gender: String) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            firstName = firstName,
            gender = gender
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updateSpendingTriggers(triggers: Set<String>) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            spendingTriggers = triggers
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updateSavingGoal(goalType: String) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            savingGoalType = goalType
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updateGoalDetails(goalName: String, currency: String, goalAmount: Double, goalCompletionDate: String = _onboardingState.userInfo.goalCompletionDate) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            goalName = goalName,
            currency = currency,
            goalAmount = goalAmount,
            goalCompletionDate = goalCompletionDate
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updateGoalCompletionDate(date: String) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            goalCompletionDate = date
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updatePaydayDetails(frequency: String, day: String) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            paymentFrequency = frequency,
            paymentDay = day
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updatePermissions(
        notifications: Boolean = _onboardingState.userInfo.notificationsEnabled,
        storage: Boolean = _onboardingState.userInfo.storageEnabled,
        screentime: Boolean = _onboardingState.userInfo.screentimeEnabled,
        overlay: Boolean = _onboardingState.userInfo.overlayEnabled
    ) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            notificationsEnabled = notifications,
            storageEnabled = storage,
            screentimeEnabled = screentime,
            overlayEnabled = overlay
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updateSelectedApps(apps: Set<String>) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            selectedApps = apps
        )
        updateUserInfo(updatedUserInfo)
    }

    fun updatePaymentPlan(plan: String, acceptedDiscount: Boolean = false) {
        val updatedUserInfo = _onboardingState.userInfo.copy(
            selectedPlan = plan,
            acceptedDiscount = acceptedDiscount
        )
        updateUserInfo(updatedUserInfo)
    }

    // Validation methods
    fun canProceedFromCurrentStep(): Boolean {
        return when (_onboardingState.currentStep) {
            1 -> _onboardingState.userInfo.firstName.isNotBlank() && _onboardingState.userInfo.gender.isNotBlank()
            2 -> _onboardingState.userInfo.spendingTriggers.isNotEmpty()
            3 -> true // Stats screen - always can proceed
            4 -> _onboardingState.userInfo.savingGoalType.isNotBlank()
            5 -> _onboardingState.userInfo.goalName.isNotBlank() && _onboardingState.userInfo.goalAmount > 0
            6 -> true // Breakdown screen - always can proceed
            7 -> true // Character intro - always can proceed
            8 -> true // Payday details - can skip
            9 -> true // Permissions - always can proceed
            10 -> true // App selection - always can proceed (can be empty)
            11 -> true // Overview - always can proceed
            12 -> _onboardingState.userInfo.selectedPlan.isNotBlank()
            13 -> true // Payment confirmation - always can proceed
            14 -> true // Discount - always can proceed
            15 -> true // Success - always can proceed
            else -> false
        }
    }
}