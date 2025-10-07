package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AvatarStatsResponse(
    val id: Int,
    val date: String,
    val starting_points: Int,
    val current_points: Int,
    val points_lost: Int,
    val total_spent: Double,
    val total_time_spent: Int,
    val apps_used: Int,
    val goal_progress_percentage: Double,
    val days_to_goal: Int,
    val created_at: String,
    val updated_at: String? = null
)
