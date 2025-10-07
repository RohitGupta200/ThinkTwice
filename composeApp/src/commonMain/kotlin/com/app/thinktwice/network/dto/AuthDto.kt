package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class OAuth2SignInRequest(
    val provider: String, // "google" or "apple"
    val token: String, // OAuth token from provider
    val device_id: String,
    val user_agent: String,
    val referral_code: String? = null
)

@Serializable
data class OAuth2SignInResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String = "bearer",
    val expires_in: Int,
    val user: AuthUserDto
)

@Serializable
data class AuthUserDto(
    val id: Int,
    val email: String,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val gender: String? = null,
    val currency: String,
    val is_premium: Boolean,
    val created_at: String,
    val last_login_at: String? = null
)

@Serializable
data class RefreshTokenRequest(
    val refresh_token: String
)

@Serializable
data class RefreshTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String = "bearer",
    val expires_in: Int
)

@Serializable
data class LogoutRequest(
    val refresh_token: String
)