package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.GoalProgressResponse
import com.app.thinktwice.network.dto.SpendingAnalyticsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query

interface AnalyticsApi {

    @GET("api/v1/analytics/spending")
    suspend fun getSpendingAnalytics(
        @Header("Authorization") authorization: String,
        @Query("period") period: String // day, week, month, year
    ): SpendingAnalyticsResponse

    @GET("api/v1/analytics/goals/progress")
    suspend fun getGoalProgress(
        @Header("Authorization") authorization: String
    ): GoalProgressResponse
}
