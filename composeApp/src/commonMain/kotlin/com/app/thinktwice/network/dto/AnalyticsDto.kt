package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SpendingAnalyticsResponse(
    val period: String,
    val total_spent: Double,
    val average_daily: Double,
    val transactions_count: Int,
    val categories: List<CategorySpending>,
    val trends: SpendingTrends
)

@Serializable
data class CategorySpending(
    val category: String,
    val amount: Double,
    val percentage: Double
)

@Serializable
data class SpendingTrends(
    val change_percentage: Double,
    val direction: String
)

@Serializable
data class GoalProgressResponse(
    val active_goals: Int,
    val completed_goals: Int,
    val total_saved: Double,
    val goals: List<GoalProgress>
)

@Serializable
data class GoalProgress(
    val id: Int,
    val name: String,
    val target_amount: Double,
    val current_amount: Double,
    val progress_percentage: Double,
    val days_remaining: Int
)
