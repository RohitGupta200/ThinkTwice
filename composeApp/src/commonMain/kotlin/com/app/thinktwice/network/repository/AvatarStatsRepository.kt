package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.AvatarStatsResponse
import com.app.thinktwice.network.error.ErrorHandler

class AvatarStatsRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Get current avatar stats
     */
    suspend fun getAvatarStats(userId: Long): Result<AvatarStatsResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.avatarStatsApi.getAvatarStats(token)
        }
    }

    /**
     * Get avatar stats history
     */
    suspend fun getAvatarStatsHistory(userId: Long): Result<List<AvatarStatsResponse>> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.avatarStatsApi.getAvatarStatsHistory(token)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
