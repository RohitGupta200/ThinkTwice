package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.AdvancedGoalProgressResponse
import com.app.thinktwice.network.dto.AdvancedSpendingAnalyticsResponse
import com.app.thinktwice.network.dto.SpendingInsightsResponse
import com.app.thinktwice.network.error.ErrorHandler

class AdvancedAnalyticsRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Get advanced spending analytics
     */
    suspend fun getAdvancedSpendingAnalytics(
        userId: Long
    ): Result<AdvancedSpendingAnalyticsResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.advancedAnalyticsApi.getAdvancedSpendingAnalytics(token)
        }
    }

    /**
     * Get advanced goal progress
     */
    suspend fun getAdvancedGoalProgress(
        userId: Long
    ): Result<AdvancedGoalProgressResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.advancedAnalyticsApi.getAdvancedGoalProgress(token)
        }
    }

    /**
     * Get spending insights
     */
    suspend fun getSpendingInsights(userId: Long): Result<SpendingInsightsResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.advancedAnalyticsApi.getSpendingInsights(token)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
