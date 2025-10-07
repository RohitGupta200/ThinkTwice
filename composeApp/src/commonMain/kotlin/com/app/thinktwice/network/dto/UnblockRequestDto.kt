package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateUnblockRequestRequest(
    val blocked_app_id: Int,
    val duration_minutes: Int,
    val planned_purchase: String,
    val planned_amount: Double
)

@Serializable
data class UnblockRequestResponse(
    val id: Int,
    val blocked_app_id: Int,
    val duration_minutes: Int,
    val planned_purchase: String,
    val planned_amount: Double,
    val actual_amount: Double? = null,
    val status: String,
    val approved_at: String? = null,
    val completed_at: String? = null,
    val expires_at: String? = null,
    val created_at: String
)
