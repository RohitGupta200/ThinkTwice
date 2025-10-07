package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.CreateUnblockRequestRequest
import com.app.thinktwice.network.dto.UnblockRequestResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST

interface UnblockRequestApi {

    @POST("unblock-requests")
    suspend fun createUnblockRequest(
        @Header("Authorization") authorization: String,
        @Body request: CreateUnblockRequestRequest
    ): UnblockRequestResponse

    @GET("unblock-requests")
    suspend fun getUnblockRequests(
        @Header("Authorization") authorization: String
    ): List<UnblockRequestResponse>
}
