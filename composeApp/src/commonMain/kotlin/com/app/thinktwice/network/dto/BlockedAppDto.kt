package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateBlockedAppRequest(
    val app_name: String,
    val app_package: String,
    val app_category: String,
    val app_icon_url: String? = null
)

@Serializable
data class BlockedAppResponse(
    val id: Int,
    val app_name: String,
    val app_package: String,
    val app_category: String,
    val app_icon_url: String? = null,
    val is_blocked: Boolean,
    val total_spent: Double,
    val total_time_spent: Int,
    val created_at: String,
    val updated_at: String? = null
)

@Serializable
data class RemoveBlockedAppResponse(
    val message: String
)
