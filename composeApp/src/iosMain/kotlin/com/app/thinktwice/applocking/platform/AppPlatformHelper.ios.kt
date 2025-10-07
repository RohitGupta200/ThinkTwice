package com.app.thinktwice.applocking.platform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * iOS implementation of AppPlatformHelper
 */
actual class AppPlatformHelper {

    /**
     * Check if Screen Time authorization is granted
     */
    actual suspend fun hasUsageStatsPermission(): Boolean {
        // TODO: Implement using FamilyControls authorization check
        // For now, return false - this will trigger permission request
        return false
    }

    /**
     * Request Screen Time authorization
     */
    actual suspend fun requestUsageStatsPermission() {
        // TODO: Implement using FamilyControls authorization request
        // This should call FamilyControlsManager.shared.requestAuthorization()
    }

    /**
     * Get all installed apps
     * Note: iOS doesn't allow listing all installed apps for privacy reasons
     */
    actual suspend fun getInstalledApps(): List<AppInfo> {
        // iOS uses FamilyActivityPicker instead of programmatic app listing
        // Return empty list - UI should use FamilyActivityPicker
        return emptyList()
    }

    /**
     * Observe permission state changes
     */
    actual fun observePermissionState(): Flow<Boolean> {
        // TODO: Implement using FamilyControls authorization observation
        return flowOf(false)
    }
}
