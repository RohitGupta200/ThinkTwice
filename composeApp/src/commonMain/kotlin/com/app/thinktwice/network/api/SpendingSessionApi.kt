package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.CreateSpendingSessionRequest
import com.app.thinktwice.network.dto.SpendingSessionResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST

interface SpendingSessionApi {

    @POST("spending-sessions")
    suspend fun createSpendingSession(
        @Header("Authorization") authorization: String,
        @Body request: CreateSpendingSessionRequest
    ): SpendingSessionResponse

    @GET("spending-sessions")
    suspend fun getSpendingSessions(
        @Header("Authorization") authorization: String
    ): List<SpendingSessionResponse>
}
