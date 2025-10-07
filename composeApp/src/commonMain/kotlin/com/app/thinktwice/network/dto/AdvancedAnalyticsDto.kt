package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppUsageStats(
    val app_name: String,
    val spent: Double,
    val sessions: Int,
    val time_minutes: Int
)

@Serializable
data class SpendingTrend(
    val date: String,
    val amount: Double
)

@Serializable
data class GoalAtRisk(
    val goal_name: String,
    val target_amount: Double,
    val risk_level: String
)

@Serializable
data class GoalImpact(
    val delay_days: Int,
    val progress_affected: Boolean,
    val goals_at_risk: List<GoalAtRisk>
)

@Serializable
data class AvatarPerformance(
    val current_points: Int,
    val points_lost_today: Int,
    val streak_days: Int,
    val average_points: Double
)

@Serializable
data class AdvancedSpendingAnalyticsResponse(
    val total_spent: Double,
    val total_saved: Double,
    val average_daily_spending: Double,
    val most_used_apps: List<AppUsageStats>,
    val spending_trends: List<SpendingTrend>,
    val goal_impact: GoalImpact,
    val avatar_performance: AvatarPerformance
)

@Serializable
data class GoalDetail(
    val id: Int,
    val name: String,
    val target_amount: Double,
    val current_amount: Double,
    val progress_percentage: Double,
    val days_remaining: Int,
    val daily_target: Double
)

@Serializable
data class AdvancedGoalProgressResponse(
    val total_goals: Int,
    val total_target_amount: Double,
    val total_current_amount: Double,
    val overall_progress: Double,
    val goals_on_track: Int,
    val goals_at_risk: Int,
    val goals: List<GoalDetail>
)

@Serializable
data class Insight(
    val type: String,
    val message: String,
    val severity: String
)

@Serializable
data class Recommendation(
    val type: String,
    val message: String,
    val action: String
)

@Serializable
data class Pattern(
    val type: String,
    val message: String,
    val hour: Int? = null,
    val count: Int
)

@Serializable
data class InsightsSummary(
    val total_sessions: Int,
    val total_spent: Double,
    val average_session_amount: Double,
    val days_analyzed: Int
)

@Serializable
data class SpendingInsightsResponse(
    val insights: List<Insight>,
    val recommendations: List<Recommendation>,
    val patterns: List<Pattern>,
    val summary: InsightsSummary
)
