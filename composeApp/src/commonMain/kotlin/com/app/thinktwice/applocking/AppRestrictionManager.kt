package com.app.thinktwice.applocking

import com.app.thinktwice.applocking.models.RestrictedApp
import com.app.thinktwice.applocking.platform.AppInfo
import com.app.thinktwice.database.repository.AppRestrictionRepository
import com.app.thinktwice.database.utils.TimeProvider
import kotlinx.coroutines.flow.first

/**
 * Cross-platform manager for app restrictions
 */
class AppRestrictionManager(
    private val repository: AppRestrictionRepository
) {

    /**
     * Save selected apps to database and start monitoring
     */
    suspend fun setupRestrictions(
        selectedPackageNames: Set<String>,
        installedApps: List<AppInfo>
    ): Result<Unit> {
        return try {
            val now = TimeProvider.currentTimeMillis()

            // Save each selected app to database
            selectedPackageNames.forEach { packageName ->
                val appInfo = installedApps.find { it.packageName == packageName }
                if (appInfo != null) {
                    repository.addRestrictedApp(
                        appId = packageName,
                        appName = appInfo.appName,
                        packageName = packageName,
                        iconPath = appInfo.iconPath
                    )
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all restricted apps
     */
    suspend fun getRestrictedApps(): List<RestrictedApp> {
        return repository.getAllRestrictedApps().first()
    }

    /**
     * Check if an app is restricted
     */
    suspend fun isAppRestricted(packageName: String): Boolean {
        return repository.getRestrictedAppByPackage(packageName) != null
    }
}

/**
 * Platform-specific service starter
 */
expect class AppMonitoringService {
    /**
     * Start the app monitoring service
     */
    fun start()

    /**
     * Stop the app monitoring service
     */
    fun stop()

    /**
     * Check if service is running
     */
    fun isRunning(): Boolean
}
