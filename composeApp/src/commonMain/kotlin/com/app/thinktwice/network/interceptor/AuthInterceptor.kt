package com.app.thinktwice.network.interceptor

import com.app.thinktwice.network.api.AuthApi
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.RefreshTokenRequest
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Interceptor that handles automatic token refresh and adds authorization headers
 */
class AuthInterceptor(
    private val tokenManager: AuthTokenManager,
    private val authApi: AuthApi
) {

    private val refreshMutex = Mutex()
    private var isRefreshing = false

    companion object {
        val Plugin = createClientPlugin("AuthInterceptor", ::PluginConfiguration) {
            val tokenManager = pluginConfig.tokenManager
            val authApi = pluginConfig.authApi

            onRequest { request, _ ->
                // Skip adding auth header for auth endpoints
                if (isAuthEndpoint(request.url.encodedPath)) {
                    return@onRequest
                }

                val userId = tokenManager.getUserId() ?: return@onRequest

                // Check if token is valid
                if (!tokenManager.isTokenValid(userId)) {
                    // Refresh token before making request
                    refreshTokenIfNeeded(tokenManager, authApi, userId)
                }

                // Add authorization header
                val accessToken = tokenManager.getAccessToken(userId)
                if (accessToken != null) {
                    request.header(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }

            onResponse { response ->
                // Handle 401 Unauthorized - token expired
                if (response.status == HttpStatusCode.Unauthorized) {
                    val userId = tokenManager.getUserId()
                    if (userId != null) {
                        // Try to refresh token
                        val refreshed = refreshTokenIfNeeded(tokenManager, authApi, userId)
                        if (!refreshed) {
                            // Refresh failed, clear tokens
                            tokenManager.clearTokens(userId)
                        }
                    }
                }
            }
        }

        private fun isAuthEndpoint(path: String): Boolean {
            return path.contains("/auth/") || path.contains("/oauth2")
        }

        private suspend fun refreshTokenIfNeeded(
            tokenManager: AuthTokenManager,
            authApi: AuthApi,
            userId: Long
        ): Boolean {
            return try {
                val refreshToken = tokenManager.getRefreshToken(userId) ?: return false

                val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))

                tokenManager.saveTokens(
                    userId = userId,
                    accessToken = response.access_token,
                    refreshToken = response.refresh_token,
                    expiresInSeconds = response.expires_in
                )

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    class PluginConfiguration {
        lateinit var tokenManager: AuthTokenManager
        lateinit var authApi: AuthApi
    }
}
