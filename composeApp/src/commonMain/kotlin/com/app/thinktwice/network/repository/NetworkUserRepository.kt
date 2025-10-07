package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiExceptionMapper
import com.app.thinktwice.network.BasicApiService
import com.app.thinktwice.network.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class NetworkUserRepository(private val apiService: BasicApiService) {

    suspend fun oauth2SignIn(provider: String, token: String, deviceId: String, userAgent: String, referralCode: String? = null): Result<OAuth2SignInResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.oauth2SignIn(provider, token, deviceId, userAgent, referralCode)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<RefreshTokenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.refreshToken(refreshToken)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun getCurrentUserProfile(): Result<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentUserProfile()

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to get user profile"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun updateProfile(updateRequest: UpdateUserRequest): Result<UserDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateProfile(updateRequest)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to update profile"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun changePassword(userId: Long, currentPassword: String, newPassword: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Basic API service doesn't have change password - would need implementation
                Result.failure(Exception("Change password not implemented in BasicApiService"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }
}