package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.CreateSpendingSessionRequest
import com.app.thinktwice.network.dto.SpendingSessionResponse
import com.app.thinktwice.network.error.ErrorHandler

class SpendingSessionRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Create a spending session
     */
    suspend fun createSpendingSession(
        userId: Long,
        blockedAppId: Int,
        sessionDuration: Int,
        amountSpent: Double,
        itemsPurchased: List<String>,
        sessionType: String
    ): Result<SpendingSessionResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = CreateSpendingSessionRequest(
                blocked_app_id = blockedAppId,
                session_duration = sessionDuration,
                amount_spent = amountSpent,
                items_purchased = itemsPurchased,
                session_type = sessionType
            )
            apiService.spendingSessionApi.createSpendingSession(token, request)
        }
    }

    /**
     * Get all spending sessions
     */
    suspend fun getSpendingSessions(userId: Long): Result<List<SpendingSessionResponse>> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.spendingSessionApi.getSpendingSessions(token)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
