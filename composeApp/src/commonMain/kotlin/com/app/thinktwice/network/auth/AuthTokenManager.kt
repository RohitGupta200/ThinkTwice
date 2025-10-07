package com.app.thinktwice.network.auth

import com.app.thinktwice.database.dao.SettingsDao
import com.app.thinktwice.database.utils.TimeProvider

/**
 * Manages authentication tokens (access, refresh) and their expiration
 */
class AuthTokenManager(private val settingsDao: SettingsDao) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "auth_access_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        private const val KEY_TOKEN_EXPIRATION = "auth_token_expiration"
        private const val KEY_USER_ID = "auth_user_id"
        private const val BUFFER_TIME_MS = 60_000L // 1 minute buffer before expiration
    }

    /**
     * Stores authentication tokens in database
     */
    suspend fun saveTokens(
        userId: Long,
        accessToken: String,
        refreshToken: String,
        expiresInSeconds: Int
    ) {
        val expirationTime = TimeProvider.currentTimeMillis() + (expiresInSeconds * 1000L)

        settingsDao.insertOrUpdate(userId, KEY_ACCESS_TOKEN, accessToken)
        settingsDao.insertOrUpdate(userId, KEY_REFRESH_TOKEN, refreshToken)
        settingsDao.insertOrUpdate(userId, KEY_TOKEN_EXPIRATION, expirationTime.toString())
        settingsDao.insertOrUpdate(userId, KEY_USER_ID, userId.toString())
    }

    /**
     * Retrieves the current access token
     */
    suspend fun getAccessToken(userId: Long): String? {
        return settingsDao.getValueByUserAndKey(userId, KEY_ACCESS_TOKEN)
    }

    /**
     * Retrieves the current refresh token
     */
    suspend fun getRefreshToken(userId: Long): String? {
        return settingsDao.getValueByUserAndKey(userId, KEY_REFRESH_TOKEN)
    }

    /**
     * Checks if the access token is valid (not expired)
     */
    suspend fun isTokenValid(userId: Long): Boolean {
        val expirationStr = settingsDao.getValueByUserAndKey(userId, KEY_TOKEN_EXPIRATION) ?: return false
        val expiration = expirationStr.toLongOrNull() ?: return false
        val currentTime = TimeProvider.currentTimeMillis()

        // Token is valid if current time + buffer is less than expiration
        return (currentTime + BUFFER_TIME_MS) < expiration
    }

    /**
     * Gets the stored user ID
     */
    suspend fun getUserId(): Long? {
        // Try to get from any user's settings (since we store it with the userId)
        val userIdStr = settingsDao.getValueByUserAndKey(0, KEY_USER_ID)
        return userIdStr?.toLongOrNull()
    }

    /**
     * Clears all authentication tokens
     */
    suspend fun clearTokens(userId: Long) {
        settingsDao.delete(userId, KEY_ACCESS_TOKEN)
        settingsDao.delete(userId, KEY_REFRESH_TOKEN)
        settingsDao.delete(userId, KEY_TOKEN_EXPIRATION)
        settingsDao.delete(userId, KEY_USER_ID)
    }
}
