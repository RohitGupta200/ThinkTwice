package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.BlockedAppResponse
import com.app.thinktwice.network.dto.CreateBlockedAppRequest
import com.app.thinktwice.network.dto.RemoveBlockedAppResponse
import de.jensklingenberg.ktorfit.http.*

interface BlockedAppApi {

    @POST("blocked-apps")
    suspend fun createBlockedApp(
        @Header("Authorization") authorization: String,
        @Body request: CreateBlockedAppRequest
    ): BlockedAppResponse

    @GET("blocked-apps")
    suspend fun getBlockedApps(
        @Header("Authorization") authorization: String
    ): List<BlockedAppResponse>

    @DELETE("blocked-apps/{app_id}")
    suspend fun removeBlockedApp(
        @Header("Authorization") authorization: String,
        @Path("app_id") appId: Int
    ): RemoveBlockedAppResponse
}
