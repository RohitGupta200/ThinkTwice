package com.app.thinktwice.service

import com.app.thinktwice.database.ThinkTwiceDatabaseService
import com.app.thinktwice.network.BasicApiService
import com.app.thinktwice.network.repository.NetworkNotesRepository
import com.app.thinktwice.network.repository.NetworkSettingsRepository
import com.app.thinktwice.network.repository.NetworkUserRepository

/**
 * Main service that combines both local database and network operations
 * This provides a unified API for the app to access all data operations
 */
class ThinkTwiceService(
    private val databaseService: ThinkTwiceDatabaseService,
    private val apiService: BasicApiService
) {
    // Local database repositories
    val localUsers = databaseService.userRepository
    val localNotes = databaseService.noteRepository
    val localSettings = databaseService.settingsRepository

    // Network repositories
    val networkUsers = NetworkUserRepository(apiService)
    val networkNotes = NetworkNotesRepository(apiService)
    val networkSettings = NetworkSettingsRepository(apiService)

    // Sync operations
    val syncService = SyncService(databaseService, apiService)

    fun close() {
        apiService.close()
    }
}

/**
 * Factory for creating ThinkTwiceService instances
 */
class ThinkTwiceServiceFactory {
    fun create(
        databaseService: ThinkTwiceDatabaseService,
        apiService: BasicApiService
    ): ThinkTwiceService {
        return ThinkTwiceService(databaseService, apiService)
    }
}