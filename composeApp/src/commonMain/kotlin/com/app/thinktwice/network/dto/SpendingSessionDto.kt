package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateSpendingSessionRequest(
    val blocked_app_id: Int,
    val session_duration: Int,
    val amount_spent: Double,
    val items_purchased: List<String>,
    val session_type: String
)

@Serializable
data class SpendingSessionResponse(
    val id: Int,
    val blocked_app_id: Int,
    val session_duration: Int,
    val amount_spent: Double,
    val items_purchased: List<String>,
    val session_type: String,
    val goal_delay_days: Int,
    val points_lost: Int,
    val started_at: String,
    val ended_at: String,
    val created_at: String
)
