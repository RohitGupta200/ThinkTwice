package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.AdvancedGoalProgressResponse
import com.app.thinktwice.network.dto.AdvancedSpendingAnalyticsResponse
import com.app.thinktwice.network.dto.SpendingInsightsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface AdvancedAnalyticsApi {

    @GET("analytics/spending")
    suspend fun getAdvancedSpendingAnalytics(
        @Header("Authorization") authorization: String
    ): AdvancedSpendingAnalyticsResponse

    @GET("analytics/goals/progress")
    suspend fun getAdvancedGoalProgress(
        @Header("Authorization") authorization: String
    ): AdvancedGoalProgressResponse

    @GET("analytics/insights")
    suspend fun getSpendingInsights(
        @Header("Authorization") authorization: String
    ): SpendingInsightsResponse
}
