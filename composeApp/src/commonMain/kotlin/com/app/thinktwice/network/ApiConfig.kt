package com.app.thinktwice.network

object ApiConfig {
    // Base URL for your API
    const val BASE_URL = "https://api.thinktwice.com"

    // API versioning
    const val API_VERSION = "v1"

    // Full base URL with version
    const val FULL_BASE_URL = "$BASE_URL/api/$API_VERSION"

    // Endpoints
    object Endpoints {
        // Authentication
        const val LOGIN = "/auth/login"
        const val REGISTER = "/auth/register"
        const val REFRESH_TOKEN = "/auth/refresh"
        const val LOGOUT = "/auth/logout"

        // Users
        const val USERS = "/users"
        const val USER_PROFILE = "/users/profile"
        const val UPDATE_PROFILE = "/users/profile"

        // Notes
        const val NOTES = "/notes"
        const val USER_NOTES = "/users/{userId}/notes"
        const val NOTE_BY_ID = "/notes/{id}"
        const val SEARCH_NOTES = "/notes/search"

        // Settings
        const val USER_SETTINGS = "/users/{userId}/settings"
        const val SETTING_BY_KEY = "/users/{userId}/settings/{key}"

        // Sync
        const val SYNC_DATA = "/sync"
        const val SYNC_STATUS = "/sync/status"
    }

    // Request timeouts
    const val CONNECT_TIMEOUT = 30_000L
    const val READ_TIMEOUT = 30_000L
    const val WRITE_TIMEOUT = 30_000L
}