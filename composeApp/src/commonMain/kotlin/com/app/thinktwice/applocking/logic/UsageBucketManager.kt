package com.app.thinktwice.applocking.logic

import com.app.thinktwice.applocking.models.*
import com.app.thinktwice.database.utils.TimeProvider
import com.app.thinktwice.database.repository.AppRestrictionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Centralized manager for app usage monitoring and restriction logic
 * This is the main business logic controller for the app locking feature
 */
class UsageBucketManager(
    private val repository: AppRestrictionRepository,
    private val snoozeLogic: SnoozeTimerLogic
) {

    // Track active sessions in memory
    private val activeSessions = mutableMapOf<String, AppSession>()

    /**
     * Get all restricted apps
     */
    fun getRestrictedApps(): Flow<List<RestrictedApp>> {
        return repository.getAllRestrictedApps()
    }

    /**
     * Get enabled restricted apps
     */
    fun getEnabledRestrictedApps(): Flow<List<RestrictedApp>> {
        return repository.getEnabledRestrictedApps()
    }

    /**
     * Check if should show blocker for the given package
     * Main decision logic:
     * 1. Is app restricted and enabled?
     * 2. Does it have an active, non-expired snooze?
     *
     * Returns true if blocker should be shown
     */
    suspend fun shouldShowBlocker(packageName: String): Boolean {
        return repository.shouldShowBlocker(packageName)
    }

    /**
     * Handle app opened event
     * Called when a restricted app is detected as foreground
     *
     * @return true if blocker should be shown immediately
     */
    suspend fun onAppOpened(packageName: String): Boolean {
        println("UsageBucketManager: onAppOpened() called for: $packageName")

        // Check if already restricted
        val isRestricted = repository.isPackageRestricted(packageName)
        println("UsageBucketManager: Is '$packageName' restricted? $isRestricted")
        if (!isRestricted) {
            return false
        }

        // Get or create session
        println("UsageBucketManager: Getting or creating session for: $packageName")
        val session = getOrCreateSession(packageName)

        // Check if snooze is active
        val hasActiveSnooze = snoozeLogic.hasActiveSnooze(packageName)
        println("UsageBucketManager: Does '$packageName' have active snooze? $hasActiveSnooze")
        if (hasActiveSnooze) {
            // Update session with snooze info
            val snooze = snoozeLogic.getActiveSnooze(packageName)
            session.currentSnooze = snooze
            println("UsageBucketManager: Not showing blocker due to active snooze")
            return false // Don't show blocker if snooze is active
        }

        // No active snooze - show blocker
        println("UsageBucketManager: Returning TRUE - blocker should be shown for: $packageName")
        return true
    }

    /**
     * Handle app closed/backgrounded event
     * Called when a restricted app moves to background
     *
     * @return AppSession if follow-up should be shown, null otherwise
     */
    suspend fun onAppClosed(packageName: String): AppSession? {
        val session = activeSessions[packageName] ?: return null

        // Mark session as inactive
        session.isActive = false
        session.endTime = TimeProvider.currentTimeMillis()

        // Remove from active sessions
        activeSessions.remove(packageName)

        // Return session for follow-up screen
        return session
    }

    /**
     * Handle snooze expiry event
     * Called when a snooze timer expires
     *
     * @return true if app is still in foreground and blocker should be re-shown
     */
    suspend fun onSnoozeExpired(packageName: String, isAppInForeground: Boolean): Boolean {
        val session = activeSessions[packageName]

        // Deactivate the snooze
        val snooze = snoozeLogic.getActiveSnooze(packageName)
        snooze?.let {
            snoozeLogic.deactivateSnooze(it.id)
        }

        // Clear snooze from session
        session?.currentSnooze = null

        // If app is still in foreground, show blocker again
        return isAppInForeground && repository.isPackageRestricted(packageName)
    }

    /**
     * Handle user selecting snooze on blocker screen
     */
    suspend fun onSnoozeSelected(packageName: String, durationMinutes: Int): Result<SnoozeState> {
        return snoozeLogic.createSnooze(packageName, durationMinutes)
    }

    /**
     * Handle user submitting follow-up response
     */
    suspend fun onFollowupResponse(
        session: AppSession,
        response: ResponseType
    ): Result<Unit> {
        val endTime = session.endTime ?: TimeProvider.currentTimeMillis()

        val result = repository.recordFollowupResponse(
            packageName = session.packageName,
            sessionStartTime = session.startTime,
            sessionEndTime = endTime,
            response = response
        )

        return if (result.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Failed to record response"))
        }
    }

    /**
     * Add app to restricted list
     */
    suspend fun addRestrictedApp(
        appId: String,
        appName: String,
        packageName: String,
        iconPath: String? = null
    ): Result<Unit> {
        return repository.addRestrictedApp(appId, appName, packageName, iconPath)
    }

    /**
     * Remove app from restricted list
     */
    suspend fun removeRestrictedApp(appId: Long): Result<Unit> {
        return repository.removeRestrictedApp(appId)
    }

    /**
     * Toggle app restriction on/off
     */
    suspend fun toggleAppRestriction(appId: Long, isEnabled: Boolean): Result<Unit> {
        return repository.updateRestrictedAppEnabled(appId, isEnabled)
    }

    /**
     * Get monitoring state for package
     */
    suspend fun getMonitoringState(packageName: String): MonitoringState {
        return repository.getMonitoringStateForPackage(packageName)
    }

    /**
     * Get all monitoring states (for all enabled apps)
     */
    suspend fun getAllMonitoringStates(): Map<String, MonitoringState> {
        return repository.getAllMonitoringStates()
    }

    /**
     * Get response statistics
     */
    suspend fun getResponseStats(): Map<ResponseType, Long> {
        return repository.getResponseStats()
    }

    /**
     * Periodic cleanup of old data
     */
    suspend fun performCleanup() {
        repository.cleanupOldSnoozes()
        repository.cleanupOldResponses()
        repository.cleanupExpiredSnoozes()
    }

    /**
     * Check for expired snoozes and return packages that need blocker re-shown
     */
    suspend fun checkExpiredSnoozes(): List<String> {
        val expired = repository.cleanupExpiredSnoozes()

        // Filter to only those that are currently in foreground
        return expired.mapNotNull { snooze ->
            val session = activeSessions.values.find { it.restrictedAppId == snooze.restrictedAppId }
            if (session?.isActive == true) {
                session.packageName
            } else {
                null
            }
        }
    }

    /**
     * Get or create session for package
     */
    private suspend fun getOrCreateSession(packageName: String): AppSession {
        return activeSessions.getOrPut(packageName) {
            val app = repository.getRestrictedAppByPackage(packageName)
            val currentTime = TimeProvider.currentTimeMillis()

            AppSession(
                restrictedAppId = app?.id ?: 0,
                packageName = packageName,
                startTime = currentTime,
                isActive = true
            )
        }
    }

    /**
     * Get active session for package
     */
    fun getActiveSession(packageName: String): AppSession? {
        return activeSessions[packageName]
    }

    /**
     * Clear all active sessions (e.g., when monitoring is disabled)
     */
    fun clearAllSessions() {
        activeSessions.clear()
    }
}
