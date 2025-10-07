package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.CreateUserRequest
import com.app.thinktwice.network.dto.UpdateUserRequest
import com.app.thinktwice.network.dto.UserDto
import com.app.thinktwice.network.dto.UsersResponse
import com.app.thinktwice.network.error.ErrorHandler

class UserRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Get all users with pagination
     */
    suspend fun getAllUsers(
        userId: Long,
        skip: Int = 0,
        limit: Int = 100
    ): Result<UsersResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.userApi.getAllUsers(token, skip, limit)
        }
    }

    /**
     * Get user by ID
     */
    suspend fun getUserById(
        currentUserId: Long,
        targetUserId: Int
    ): Result<UserDto> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(currentUserId)
            apiService.userApi.getUserById(token, targetUserId)
        }
    }

    /**
     * Create a new user
     */
    suspend fun createUser(
        userId: Long,
        username: String,
        email: String,
        password: String,
        fullName: String
    ): Result<UserDto> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = CreateUserRequest(
                username = username,
                email = email,
                password = password,
                full_name = fullName
            )
            apiService.userApi.createUser(token, request)
        }
    }

    /**
     * Update user information
     */
    suspend fun updateUser(
        userId: Long,
        targetUserId: Int,
        fullName: String? = null,
        isActive: Boolean? = null
    ): Result<UserDto> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = UpdateUserRequest(
                full_name = fullName,
                is_active = isActive
            )
            apiService.userApi.updateUser(token, targetUserId, request)
        }
    }

    /**
     * Delete a user
     */
    suspend fun deleteUser(
        userId: Long,
        targetUserId: Int
    ): Result<Unit> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.userApi.deleteUser(token, targetUserId)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
