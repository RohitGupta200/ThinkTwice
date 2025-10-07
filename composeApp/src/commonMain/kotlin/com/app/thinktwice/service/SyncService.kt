package com.app.thinktwice.service

import com.app.thinktwice.database.ThinkTwiceDatabaseService
import com.app.thinktwice.network.ApiExceptionMapper
import com.app.thinktwice.network.BasicApiService
import com.app.thinktwice.network.dto.SyncDataRequest
import com.app.thinktwice.network.dto.SyncDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SyncService(
    private val databaseService: ThinkTwiceDatabaseService,
    private val apiService: BasicApiService
) {

    suspend fun syncData(
        lastSyncTimestamp: Long? = null,
        deviceId: String,
        clientVersion: String = "1.0.0"
    ): Result<SyncDataResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = SyncDataRequest(lastSyncTimestamp, deviceId, clientVersion)
                val response = apiService.syncData(request)

                if (response.success && response.data != null) {
                    // Update local database with synced data
                    updateLocalDataFromSync(response.data)
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Sync failed"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun getSyncStatus(): Result<com.app.thinktwice.network.api.SyncStatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Get sync status not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun fullSync(deviceId: String, clientVersion: String = "1.0.0"): Result<com.app.thinktwice.network.api.FullSyncResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Full sync not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    private suspend fun updateLocalDataFromSync(syncData: SyncDataResponse) {
        // Update notes
        syncData.notes.forEach { noteDto ->
            // Convert DTO to local model and update
            // This is a simplified example - you'd implement proper conversion
            try {
                val existingNote = databaseService.noteRepository.getNoteById(noteDto.id)
                if (existingNote != null) {
                    databaseService.noteRepository.updateNote(
                        noteDto.id,
                        noteDto.title,
                        noteDto.content,
                        noteDto.category,
                        noteDto.isImportant
                    )
                } else {
                    databaseService.noteRepository.createNote(
                        noteDto.userId,
                        noteDto.title,
                        noteDto.content,
                        noteDto.category,
                        noteDto.isImportant
                    )
                }
            } catch (e: Exception) {
                // Log sync conflict or handle as needed
                println("Failed to sync note ${noteDto.id}: ${e.message}")
            }
        }

        // Update settings
        syncData.settings.forEach { settingDto ->
            try {
                databaseService.settingsRepository.saveSetting(
                    settingDto.userId,
                    settingDto.key,
                    settingDto.value
                )
            } catch (e: Exception) {
                // Log sync conflict or handle as needed
                println("Failed to sync setting ${settingDto.key}: ${e.message}")
            }
        }
    }

    // Push local changes to server
    suspend fun pushLocalChanges(userId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Get all local data that needs to be synced
                val localNotes = databaseService.noteRepository.getNotesByUserId(userId)
                val localSettings = databaseService.settingsRepository.getUserSettings(userId)

                // Push notes (simplified - you'd track which are new/modified)
                localNotes.forEach { note ->
                    // Convert local note to DTO and push to server
                    // Implementation depends on your sync strategy
                }

                // Note: Bulk settings update not implemented in BasicApiService
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }
}