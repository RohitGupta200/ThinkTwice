package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.BlockedAppResponse
import com.app.thinktwice.network.dto.CreateBlockedAppRequest
import com.app.thinktwice.network.dto.RemoveBlockedAppResponse
import com.app.thinktwice.network.error.ErrorHandler

class BlockedAppRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Create a blocked app
     */
    suspend fun createBlockedApp(
        userId: Long,
        appName: String,
        appPackage: String,
        appCategory: String,
        appIconUrl: String? = null
    ): Result<BlockedAppResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = CreateBlockedAppRequest(
                app_name = appName,
                app_package = appPackage,
                app_category = appCategory,
                app_icon_url = appIconUrl
            )
            apiService.blockedAppApi.createBlockedApp(token, request)
        }
    }

    /**
     * Get all blocked apps
     */
    suspend fun getBlockedApps(userId: Long): Result<List<BlockedAppResponse>> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.blockedAppApi.getBlockedApps(token)
        }
    }

    /**
     * Remove a blocked app
     */
    suspend fun removeBlockedApp(
        userId: Long,
        appId: Int
    ): Result<RemoveBlockedAppResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.blockedAppApi.removeBlockedApp(token, appId)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
