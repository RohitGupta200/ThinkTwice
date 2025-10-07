package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.*
import de.jensklingenberg.ktorfit.http.*

interface SyncApi {

    @POST("sync")
    suspend fun syncData(@Body request: SyncDataRequest): ApiResponse<SyncDataResponse>

    @GET("sync/status")
    suspend fun getSyncStatus(): ApiResponse<SyncStatusResponse>

    @POST("sync/full")
    suspend fun fullSync(@Body request: SyncDataRequest): ApiResponse<FullSyncResponse>
}

@kotlinx.serialization.Serializable
data class SyncStatusResponse(
    val lastSyncTimestamp: Long,
    val pendingChanges: Int,
    val syncInProgress: Boolean
)

@kotlinx.serialization.Serializable
data class FullSyncResponse(
    val users: List<UserDto>,
    val notes: List<NoteDto>,
    val settings: List<SettingDto>,
    val syncTimestamp: Long
)