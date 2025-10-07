package com.app.thinktwice.applocking.platform

import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific helper for app-related operations
 */
expect class AppPlatformHelper {

    /**
     * Check if usage stats permission is granted
     */
    suspend fun hasUsageStatsPermission(): Boolean

    /**
     * Request usage stats permission
     */
    suspend fun requestUsageStatsPermission()

    /**
     * Get all installed apps
     */
    suspend fun getInstalledApps(): List<AppInfo>

    /**
     * Observe permission state changes
     */
    fun observePermissionState(): Flow<Boolean>
}
