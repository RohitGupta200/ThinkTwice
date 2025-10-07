package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompleteOnboardingRequest(
    val gender: String,
    val spending_triggers: List<String>,
    val spending_habits: List<String>,
    val pay_frequency: String,
    val currency: String
)

@Serializable
data class CompleteOnboardingResponse(
    val id: Int,
    val email: String,
    val full_name: String,
    val avatar_url: String? = null,
    val gender: String,
    val currency: String,
    val pay_frequency: String,
    val is_premium: Boolean,
    val premium_expires_at: String? = null,
    val created_at: String,
    val last_login_at: String? = null
)
