package com.app.thinktwice.network.repository

import com.app.thinktwice.database.DatabaseCleaner
import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.*
import com.app.thinktwice.network.error.ApiException
import com.app.thinktwice.network.error.ErrorHandler

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager,
    private val databaseCleaner: DatabaseCleaner
) {

    /**
     * Sign in with OAuth2 (Google or Apple)
     */
    suspend fun signInWithOAuth2(
        provider: String,
        token: String,
        deviceId: String,
        userAgent: String,
        referralCode: String? = null
    ): Result<OAuth2SignInResponse> {
        return ErrorHandler.safeApiCall {
            val request = OAuth2SignInRequest(
                provider = provider,
                token = token,
                device_id = deviceId,
                user_agent = userAgent,
                referral_code = referralCode
            )

            val response = apiService.authApi.oauth2SignIn(request)

            // Store tokens
            tokenManager.saveTokens(
                userId = response.user.id.toLong(),
                accessToken = response.access_token,
                refreshToken = response.refresh_token,
                expiresInSeconds = response.expires_in
            )

            response
        }
    }

    /**
     * Refresh access token
     */
    suspend fun refreshToken(userId: Long): Result<RefreshTokenResponse> {
        return ErrorHandler.safeApiCall {
            val refreshToken = tokenManager.getRefreshToken(userId)
                ?: throw ApiException.AuthException(401, null)

            val request = RefreshTokenRequest(refresh_token = refreshToken)
            val response = apiService.authApi.refreshToken(request)

            // Update stored tokens
            tokenManager.saveTokens(
                userId = userId,
                accessToken = response.access_token,
                refreshToken = response.refresh_token,
                expiresInSeconds = response.expires_in
            )

            response
        }
    }

    /**
     * Logout user and clear all local data
     */
    suspend fun logout(userId: Long): Result<Unit> {
        return ErrorHandler.safeApiCall {
            val refreshToken = tokenManager.getRefreshToken(userId)
                ?: throw ApiException.AuthException(401, null)

            val accessToken = tokenManager.getAccessToken(userId)
                ?: throw ApiException.AuthException(401, null)

            val request = LogoutRequest(refresh_token = refreshToken)
            apiService.authApi.logout("Bearer $accessToken", request)

            // Clear all local database tables
            databaseCleaner.clearAllTables()
        }
    }

    /**
     * Get current access token
     */
    suspend fun getAccessToken(userId: Long): String? {
        return tokenManager.getAccessToken(userId)
    }

    /**
     * Check if token is valid
     */
    suspend fun isTokenValid(userId: Long): Boolean {
        return tokenManager.isTokenValid(userId)
    }
}
