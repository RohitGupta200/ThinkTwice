package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.CompleteOnboardingRequest
import com.app.thinktwice.network.dto.CompleteOnboardingResponse
import com.app.thinktwice.network.error.ErrorHandler

class ProfileRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Complete user onboarding
     */
    suspend fun completeOnboarding(
        userId: Long,
        gender: String,
        spendingTriggers: List<String>,
        spendingHabits: List<String>,
        payFrequency: String,
        currency: String
    ): Result<CompleteOnboardingResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = CompleteOnboardingRequest(
                gender = gender,
                spending_triggers = spendingTriggers,
                spending_habits = spendingHabits,
                pay_frequency = payFrequency,
                currency = currency
            )
            apiService.profileApi.completeOnboarding(token, request)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
