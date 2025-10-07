package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.AvatarStatsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

interface AvatarStatsApi {

    @GET("avatar/stats")
    suspend fun getAvatarStats(
        @Header("Authorization") authorization: String
    ): AvatarStatsResponse

    @GET("avatar/stats/history")
    suspend fun getAvatarStatsHistory(
        @Header("Authorization") authorization: String
    ): List<AvatarStatsResponse>
}
