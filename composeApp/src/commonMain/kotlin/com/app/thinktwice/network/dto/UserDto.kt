package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val gender: String? = null,
    val currency: String,
    val pay_frequency: String? = null,
    val is_active: Boolean,
    val is_premium: Boolean,
    val premium_expires_at: String? = null,
    val referral_code_used: String? = null,
    val referral_code_used_at: String? = null,
    val created_at: String,
    val updated_at: String? = null,
    val last_login_at: String? = null
)

@Serializable
data class UsersResponse(
    val users: List<UserDto>,
    val total_count: Int
)

@Serializable
data class UpdateUserRequest(
    val full_name: String? = null,
    val is_active: Boolean? = null
)

@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val full_name: String
)