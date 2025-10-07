package com.app.thinktwice.network

import com.app.thinktwice.service.ThinkTwiceService

/**
 * Complete REST API Client Usage Guide for ThinkTwice KMP App
 *
 * This guide shows how to use the KtorFit-based REST API client
 * that works seamlessly with the local SQLite database.
 */
object NetworkUsageGuide {

    /**
     * FEATURES IMPLEMENTED:
     * ====================
     *
     * ✅ KtorFit REST API Client
     * ✅ Cross-platform HTTP client (Android & iOS)
     * ✅ Authentication & JWT token management
     * ✅ Automatic request/response serialization with Kotlinx Serialization
     * ✅ Comprehensive error handling & custom exceptions
     * ✅ Request/response logging
     * ✅ Network repositories for all entities (Users, Notes, Settings)
     * ✅ Sync service for local-remote data synchronization
     * ✅ Unified service combining local database + network operations
     * ✅ Pagination support
     * ✅ Search and filtering
     * ✅ Type-safe settings management
     * ✅ Bulk operations
     */

    /**
     * API ENDPOINTS COVERED:
     * =====================
     *
     * Authentication:
     * - POST /auth/login
     * - POST /auth/register
     * - POST /auth/refresh
     * - POST /auth/logout
     *
     * Users:
     * - GET /users/{id}
     * - GET /users/profile
     * - PUT /users/profile
     * - POST /users/{id}/change-password
     * - DELETE /users/{id}
     *
     * Notes:
     * - GET/POST /notes
     * - GET/PUT/DELETE /notes/{id}
     * - GET /users/{userId}/notes
     * - POST /notes/search
     * - GET /notes/important
     * - POST /notes/filter
     * - PUT/DELETE /notes/{id}/important
     *
     * Settings:
     * - GET/POST /users/{userId}/settings
     * - GET/PUT/DELETE /users/{userId}/settings/{key}
     * - POST /users/{userId}/settings/bulk
     *
     * Sync:
     * - POST /sync
     * - GET /sync/status
     * - POST /sync/full
     */

    /**
     * USAGE EXAMPLES:
     * ==============
     */

    suspend fun authenticationExample(service: ThinkTwiceService) {
        // OAuth2 Sign In
        val signInResult = service.networkUsers.oauth2SignIn(
            provider = "google",
            token = "google_id_token",
            deviceId = "device_123",
            userAgent = "ThinkTwice-Android/1.0"
        )

        // Refresh token
        val refreshResult = service.networkUsers.refreshToken("current-refresh-token")
    }

    suspend fun notesOperationsExample(service: ThinkTwiceService) {
        // Create note
        val createRequest = com.app.thinktwice.network.dto.CreateNoteRequest(
            title = "My API Note",
            content = "Created via REST API",
            category = "Work",
            isImportant = true
        )
        val createResult = service.networkNotes.createNote(createRequest)

        // Get notes with pagination
        val notesResult = service.networkNotes.getNotesByUserId(
            userId = 1L,
            page = 1,
            pageSize = 10
        )

        if (notesResult.isSuccess) {
            val paginatedNotes = notesResult.getOrThrow()
            println("Total notes: ${paginatedNotes.totalCount}")
            println("Current page: ${paginatedNotes.page}")
            println("Notes: ${paginatedNotes.items.size}")
        }

        // Search notes
        val searchResult = service.networkNotes.searchNotes(
            com.app.thinktwice.network.dto.SearchNotesRequest(
                query = "important meeting",
                category = "Work",
                isImportant = true
            )
        )

        // Update note
        val updateRequest = com.app.thinktwice.network.dto.UpdateNoteRequest(
            title = "Updated Title",
            content = "Updated content",
            isImportant = false
        )
        val updateResult = service.networkNotes.updateNote(1L, updateRequest)

        // Mark as important
        val markResult = service.networkNotes.markAsImportant(1L)
    }

    suspend fun settingsOperationsExample(service: ThinkTwiceService) {
        val userId = 1L

        // Get all user settings
        val settingsResult = service.networkSettings.getUserSettings(userId)

        // Set individual settings
        val darkModeResult = service.networkSettings.setBooleanSetting(userId, "dark_mode", true)
        val fontSizeResult = service.networkSettings.setIntSetting(userId, "font_size", 14)

        // Get typed settings
        val darkMode = service.networkSettings.getBooleanSetting(userId, "dark_mode")
        val fontSize = service.networkSettings.getIntSetting(userId, "font_size")

        // Bulk update settings
        val bulkSettings = mapOf(
            "theme" to "dark",
            "language" to "en",
            "notifications" to "true"
        )
        val bulkResult = service.networkSettings.bulkUpdateSettings(userId, bulkSettings)
    }

    suspend fun syncOperationsExample(service: ThinkTwiceService) {
        // Sync data incrementally
        val syncResult = service.syncService.syncData(
            lastSyncTimestamp = 1640995200000L, // Optional: last sync time
            deviceId = "device-123",
            clientVersion = "1.0.0"
        )

        if (syncResult.isSuccess) {
            val syncData = syncResult.getOrThrow()
            println("Synced ${syncData.notes.size} notes")
            println("Synced ${syncData.settings.size} settings")
            println("Has more data: ${syncData.hasMoreData}")
        }

        // Get sync status
        val statusResult = service.syncService.getSyncStatus()
        if (statusResult.isSuccess) {
            val status = statusResult.getOrThrow()
            println("Last sync: ${status.lastSyncTimestamp}")
            println("Pending changes: ${status.pendingChanges}")
        }

        // Full sync (replaces all local data)
        val fullSyncResult = service.syncService.fullSync("device-123")

        // Push local changes to server
        val pushResult = service.syncService.pushLocalChanges(userId = 1L)
    }

    suspend fun hybridOperationsExample(service: ThinkTwiceService) {
        // This shows the power of combining local database with network operations

        // 1. Work offline - save to local database
        val localNoteResult = service.localNotes.createNote(
            userId = 1L,
            title = "Offline Note",
            content = "Created while offline"
        )

        // 2. When online - sync with server
        if (localNoteResult.isSuccess) {
            val noteId = localNoteResult.getOrThrow()
            val localNote = service.localNotes.getNoteById(noteId)

            // Create on server
            localNote?.let { note ->
                val createRequest = com.app.thinktwice.network.dto.CreateNoteRequest(
                    title = note.title,
                    content = note.content,
                    category = note.category,
                    isImportant = note.isImportant == 1L
                )
                service.networkNotes.createNote(createRequest)
            }
        }

        // 3. Reactive local data with server updates
        service.localNotes.getAllNotesFlow().collect { localNotes ->
            println("Local notes updated: ${localNotes.size}")

            // Periodically sync with server
            service.syncService.syncData(
                deviceId = "device-123",
                clientVersion = "1.0.0"
            )
        }
    }

    /**
     * ERROR HANDLING:
     * ==============
     */
    suspend fun errorHandlingExample(service: ThinkTwiceService) {
        val signInResult = service.networkUsers.oauth2SignIn("google", "token", "device", "agent")

        signInResult.fold(
            onSuccess = { response ->
                println("Login successful: ${response.user.email}")
            },
            onFailure = { exception ->
                when (exception) {
                    is com.app.thinktwice.network.error.ApiException.AuthException ->
                        println("Authentication failed")

                    is com.app.thinktwice.network.error.ApiException.NetworkException ->
                        println("Network error - check connection")

                    is com.app.thinktwice.network.error.ApiException.ServerException ->
                        println("Server error - try again later")

                    else ->
                        println("Unknown error")
                }
            }
        )
    }

    /**
     * CONFIGURATION:
     * =============
     *
     * Base URL: Set in ApiConfig.BASE_URL
     * Timeouts: Configured in HttpClientFactory
     * Authentication: JWT Bearer tokens with auto-refresh
     * Serialization: Kotlinx Serialization JSON
     * Logging: Request/response logging enabled in debug builds
     * SSL: Configured for production use
     */
}