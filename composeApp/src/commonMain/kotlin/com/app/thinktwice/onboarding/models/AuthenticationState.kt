package com.app.thinktwice.onboarding.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val isFirstTime: Boolean = true,
    val user: User? = null,
    val errorMessage: String? = null
)

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val authProvider: AuthProvider
)

enum class AuthProvider {
    GOOGLE,
    APPLE,
    GUEST
}

sealed class AuthEvent {
    data object SignInWithGoogle : AuthEvent()
    data object SignInWithApple : AuthEvent()
    data object SignOut : AuthEvent()
    data object ClearError : AuthEvent()
    data class AuthSuccess(val user: User) : AuthEvent()
    data class AuthError(val message: String) : AuthEvent()
}

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class Authenticated(val user: User, val isFirstTime: Boolean) : AuthUiState()
    data object Unauthenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}