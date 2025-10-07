package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.GoalProgressResponse
import com.app.thinktwice.network.dto.SpendingAnalyticsResponse
import com.app.thinktwice.network.error.ErrorHandler

class AnalyticsRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Get spending analytics for a specific period
     */
    suspend fun getSpendingAnalytics(
        userId: Long,
        period: String // day, week, month, year
    ): Result<SpendingAnalyticsResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.analyticsApi.getSpendingAnalytics(token, period)
        }
    }

    /**
     * Get goal progress
     */
    suspend fun getGoalProgress(userId: Long): Result<GoalProgressResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.analyticsApi.getGoalProgress(token)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
