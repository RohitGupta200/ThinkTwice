package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class SettingDto(
    val id: Long,
    val userId: Long,
    val key: String,
    val value: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateSettingRequest(
    val key: String,
    val value: String
)

@Serializable
data class UpdateSettingRequest(
    val value: String
)

@Serializable
data class BulkSettingsRequest(
    val settings: List<SettingItem>
)

@Serializable
data class SettingItem(
    val key: String,
    val value: String
)

@Serializable
data class SyncDataRequest(
    val lastSyncTimestamp: Long? = null,
    val deviceId: String,
    val clientVersion: String
)

@Serializable
data class SyncDataResponse(
    val notes: List<NoteDto>,
    val settings: List<SettingDto>,
    val syncTimestamp: Long,
    val hasMoreData: Boolean
)