package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.CreateUnblockRequestRequest
import com.app.thinktwice.network.dto.UnblockRequestResponse
import com.app.thinktwice.network.error.ErrorHandler

class UnblockRequestRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Create an unblock request
     */
    suspend fun createUnblockRequest(
        userId: Long,
        blockedAppId: Int,
        durationMinutes: Int,
        plannedPurchase: String,
        plannedAmount: Double
    ): Result<UnblockRequestResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = CreateUnblockRequestRequest(
                blocked_app_id = blockedAppId,
                duration_minutes = durationMinutes,
                planned_purchase = plannedPurchase,
                planned_amount = plannedAmount
            )
            apiService.unblockRequestApi.createUnblockRequest(token, request)
        }
    }

    /**
     * Get all unblock requests
     */
    suspend fun getUnblockRequests(userId: Long): Result<List<UnblockRequestResponse>> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.unblockRequestApi.getUnblockRequests(token)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
