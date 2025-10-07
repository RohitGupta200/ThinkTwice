package com.app.thinktwice.onboarding.repository

import com.app.thinktwice.onboarding.models.OnboardingState
import com.app.thinktwice.onboarding.models.UserOnboardingData
import com.app.thinktwice.onboarding.models.User
import com.app.thinktwice.onboarding.models.AuthProvider

interface OnboardingRepository {
    suspend fun saveOnboardingProgress(state: OnboardingState): Result<Unit>
    suspend fun loadOnboardingProgress(userId: String): Result<OnboardingState?>
    suspend fun saveOnboardingCompletion(userInfo: UserOnboardingData, userId: String): Result<Unit>
    suspend fun isOnboardingCompleted(userId: String): Result<Boolean>
    suspend fun clearOnboardingData(userId: String): Result<Unit>
    suspend fun saveSelectedAppsToDatabase(selectedApps: Set<String>): Result<Unit>
}

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithApple(): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun isFirstTimeUser(userId: String): Result<Boolean>
    suspend fun markUserAsOnboarded(userId: String): Result<Unit>
}

class MockOnboardingRepository : OnboardingRepository {
    private val onboardingData = mutableMapOf<String, OnboardingState>()
    private val completionStatus = mutableMapOf<String, Boolean>()

    override suspend fun saveOnboardingProgress(state: OnboardingState): Result<Unit> {
        return try {
            // In a real implementation, this would save to a database
            onboardingData["current_user"] = state
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadOnboardingProgress(userId: String): Result<OnboardingState?> {
        return try {
            val state = onboardingData[userId]
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveOnboardingCompletion(userInfo: UserOnboardingData, userId: String): Result<Unit> {
        return try {
            completionStatus[userId] = true
            // Save the final user info to database
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isOnboardingCompleted(userId: String): Result<Boolean> {
        return try {
            val isCompleted = completionStatus[userId] ?: false
            Result.success(isCompleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearOnboardingData(userId: String): Result<Unit> {
        return try {
            onboardingData.remove(userId)
            completionStatus.remove(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveSelectedAppsToDatabase(selectedApps: Set<String>): Result<Unit> {
        return try {
            // TODO: Implement actual database save using AppRestrictionRepository
            // For now, just return success
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class MockAuthRepository : AuthRepository {
    private val users = mutableMapOf<String, User>()
    private val firstTimeUsers = mutableSetOf<String>()
    private var currentUserId: String? = null

    override suspend fun signInWithGoogle(): Result<User> {
        return try {
            // Mock Google sign-in
            val user = User(
                id = "google_user_${kotlin.random.Random.nextLong()}",
                email = "user@gmail.com",
                name = "John Doe",
                authProvider = AuthProvider.GOOGLE
            )
            users[user.id] = user
            currentUserId = user.id
            firstTimeUsers.add(user.id) // Mark as first time user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Google sign-in failed: ${e.message}"))
        }
    }

    override suspend fun signInWithApple(): Result<User> {
        return try {
            // Mock Apple sign-in
            val user = User(
                id = "apple_user_${kotlin.random.Random.nextLong()}",
                email = "user@icloud.com",
                name = "Jane Smith",
                authProvider = AuthProvider.APPLE
            )
            users[user.id] = user
            currentUserId = user.id
            firstTimeUsers.add(user.id) // Mark as first time user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Apple sign-in failed: ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            currentUserId = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val user = currentUserId?.let { users[it] }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isFirstTimeUser(userId: String): Result<Boolean> {
        return try {
            val isFirstTime = firstTimeUsers.contains(userId)
            Result.success(isFirstTime)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markUserAsOnboarded(userId: String): Result<Unit> {
        return try {
            firstTimeUsers.remove(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}