package com.app.thinktwice.dashboard.models

/**
 * Data models for the dashboard
 */

data class VisitedApp(
    val name: String,
    val visitCount: Int,
    val isHighFrequency: Boolean = false
)

data class UserStats(
    val avgDailySaving: Double,
    val avgDailySpending: Double,
    val avgShoppingAttempts: Double,
    val healthyStreak: Int
)

data class SavingsGoal(
    val name: String,
    val currentAmount: Double,
    val targetAmount: Double,
    val deadline: String,
    val status: GoalStatus = GoalStatus.ONGOING
)

enum class GoalStatus {
    ONGOING,
    COMPLETED,
    PAUSED
}

data class CharacterHealth(
    val value: Int, // 0-100
    val date: String
)

data class DashboardData(
    val characterProgress: Float,
    val currentSavings: Double,
    val targetSavings: Double,
    val daysRemaining: Int,
    val characterName: String,
    val frequentlyVisited: List<VisitedApp>,
    val userStats: UserStats,
    val savingsGoals: List<SavingsGoal>
)