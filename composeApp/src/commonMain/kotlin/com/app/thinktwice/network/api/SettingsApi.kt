package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.*
import de.jensklingenberg.ktorfit.http.*

interface SettingsApi {

    @GET("users/{userId}/settings")
    suspend fun getUserSettings(@Path("userId") userId: Long): ApiResponse<List<SettingDto>>

    @GET("users/{userId}/settings/{key}")
    suspend fun getSettingByKey(
        @Path("userId") userId: Long,
        @Path("key") key: String
    ): ApiResponse<SettingDto>

    @POST("users/{userId}/settings")
    suspend fun createSetting(
        @Path("userId") userId: Long,
        @Body request: CreateSettingRequest
    ): ApiResponse<SettingDto>

    @PUT("users/{userId}/settings/{key}")
    suspend fun updateSetting(
        @Path("userId") userId: Long,
        @Path("key") key: String,
        @Body request: UpdateSettingRequest
    ): ApiResponse<SettingDto>

    @DELETE("users/{userId}/settings/{key}")
    suspend fun deleteSetting(
        @Path("userId") userId: Long,
        @Path("key") key: String
    ): ApiResponse<Unit>

    @POST("users/{userId}/settings/bulk")
    suspend fun bulkUpdateSettings(
        @Path("userId") userId: Long,
        @Body request: BulkSettingsRequest
    ): ApiResponse<List<SettingDto>>
}