package com.app.thinktwice.applocking.logic

import com.app.thinktwice.applocking.models.SnoozeState
import com.app.thinktwice.database.utils.TimeProvider
import com.app.thinktwice.database.repository.AppRestrictionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Business logic for managing snooze timers
 */
class SnoozeTimerLogic(
    private val repository: AppRestrictionRepository
) {

    /**
     * Get all active snoozes
     */
    fun getActiveSnoozes(): Flow<List<SnoozeState>> {
        return repository.getActiveSnoozes()
    }

    /**
     * Create snooze for package
     */
    suspend fun createSnooze(packageName: String, durationMinutes: Int): Result<SnoozeState> {
        return repository.createSnooze(packageName, durationMinutes)
    }

    /**
     * Deactivate snooze
     */
    suspend fun deactivateSnooze(snoozeId: Long): Result<Unit> {
        return repository.deactivateSnooze(snoozeId)
    }

    /**
     * Check if package has active non-expired snooze
     */
    suspend fun hasActiveSnooze(packageName: String): Boolean {
        return repository.hasActiveSnooze(packageName)
    }

    /**
     * Get active snooze for package
     */
    suspend fun getActiveSnooze(packageName: String): SnoozeState? {
        return repository.getActiveSnoozeForPackage(packageName)
    }

    /**
     * Check if snooze has expired for a package
     * Returns true if there's a snooze but it has expired
     */
    suspend fun hasSnoozeExpired(packageName: String): Boolean {
        val snooze = repository.getActiveSnoozeForPackage(packageName) ?: return false
        val currentTime = TimeProvider.currentTimeMillis()
        return snooze.hasExpired(currentTime)
    }

    /**
     * Process expired snoozes and return list of packages that need blocker re-shown
     */
    suspend fun processExpiredSnoozes(): List<String> {
        val expired = repository.cleanupExpiredSnoozes()

        // Get package names for expired snoozes
        return expired.mapNotNull { snooze ->
            repository.getRestrictedAppByPackage("")?.let { app ->
                if (app.id == snooze.restrictedAppId) app.packageName else null
            }
        }
    }

    /**
     * Get remaining time for snooze in milliseconds
     */
    suspend fun getRemainingTime(packageName: String): Long {
        val snooze = repository.getActiveSnoozeForPackage(packageName) ?: return 0L
        val currentTime = TimeProvider.currentTimeMillis()
        return snooze.getRemainingTimeMillis(currentTime)
    }

    /**
     * Get remaining time for snooze in minutes
     */
    suspend fun getRemainingTimeMinutes(packageName: String): Int {
        val snooze = repository.getActiveSnoozeForPackage(packageName) ?: return 0
        val currentTime = TimeProvider.currentTimeMillis()
        return snooze.getRemainingTimeMinutes(currentTime)
    }

    /**
     * Flow that emits packages with expired snoozes
     */
    fun getPackagesWithExpiredSnoozes(): Flow<List<String>> {
        return getActiveSnoozes().map { snoozes ->
            val currentTime = TimeProvider.currentTimeMillis()
            snoozes
                .filter { it.hasExpired(currentTime) }
                .mapNotNull { snooze ->
                    // Need to fetch app details to get package name
                    // This is a simplified version - in practice, join with RestrictedApp
                    null // TODO: Implement proper join or lookup
                }
        }
    }

    /**
     * Schedule platform-specific alarm for snooze expiry
     * This is called by platform code to set up OS-level timers
     */
    suspend fun scheduleSnoozeExpiry(snoozeState: SnoozeState) {
        // This is handled by platform-specific code
        // Android: AlarmManager
        // iOS: Timer or Background Task
        // The expect/actual pattern will be used in AppMonitorPlatform
    }

    /**
     * Cancel scheduled snooze alarm
     */
    suspend fun cancelSnoozeAlarm(snoozeId: Long) {
        // Platform-specific - handled by expect/actual
    }
}
