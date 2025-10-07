package com.app.thinktwice.database.repository

import com.app.thinktwice.applocking.models.*
import com.app.thinktwice.database.utils.TimeProvider
import com.app.thinktwice.database.dao.FollowupResponseDao
import com.app.thinktwice.database.dao.RestrictedAppDao
import com.app.thinktwice.database.dao.SnoozeEventDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository for managing app restrictions, snooze timers, and follow-up responses
 */
class AppRestrictionRepository(
    private val restrictedAppDao: RestrictedAppDao,
    private val snoozeEventDao: SnoozeEventDao,
    private val followupResponseDao: FollowupResponseDao
) {

    // ========== Restricted Apps ==========

    /**
     * Get all restricted apps as Flow
     */
    fun getAllRestrictedApps(): Flow<List<RestrictedApp>> {
        return restrictedAppDao.getAllAsFlow()
    }

    /**
     * Get enabled restricted apps as Flow
     */
    fun getEnabledRestrictedApps(): Flow<List<RestrictedApp>> {
        return restrictedAppDao.getEnabledAsFlow()
    }

    /**
     * Get all restricted apps synchronously (for non-Flow contexts)
     */
    suspend fun getAllRestrictedAppsSync(): List<RestrictedApp> {
        return restrictedAppDao.getAllAsFlow().first()
    }

    /**
     * Get restricted app by package name
     */
    suspend fun getRestrictedAppByPackage(packageName: String): RestrictedApp? {
        return restrictedAppDao.getByPackageName(packageName)
    }

    /**
     * Add new restricted app
     */
    suspend fun addRestrictedApp(
        appId: String,
        appName: String,
        packageName: String,
        iconPath: String? = null
    ): Result<Unit> {
        return try {
            val existing = restrictedAppDao.getByPackageName(packageName)
            if (existing != null) {
                return Result.failure(Exception("App already restricted"))
            }

            val now = TimeProvider.currentTimeMillis()
            val app = RestrictedApp(
                appId = appId,
                appName = appName,
                packageName = packageName,
                iconPath = iconPath,
                isEnabled = true,
                createdAt = now,
                updatedAt = now
            )
            restrictedAppDao.insert(app)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update restricted app enabled status
     */
    suspend fun updateRestrictedAppEnabled(id: Long, isEnabled: Boolean): Result<Unit> {
        return try {
            restrictedAppDao.updateEnabled(id, isEnabled)

            // If disabling, also deactivate any active snoozes
            if (!isEnabled) {
                snoozeEventDao.deactivateAllForApp(id)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Remove restricted app
     */
    suspend fun removeRestrictedApp(id: Long): Result<Unit> {
        return try {
            restrictedAppDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if package is restricted and enabled
     */
    suspend fun isPackageRestricted(packageName: String): Boolean {
        return restrictedAppDao.isPackageRestricted(packageName)
    }

    /**
     * Get count of enabled restricted apps
     */
    suspend fun getEnabledCount(): Long {
        return restrictedAppDao.countEnabled()
    }

    // ========== Snooze Events ==========

    /**
     * Get all active snoozes as Flow
     */
    fun getActiveSnoozes(): Flow<List<SnoozeState>> {
        return snoozeEventDao.getActiveAsFlow()
    }

    /**
     * Get active snooze for package
     */
    suspend fun getActiveSnoozeForPackage(packageName: String): SnoozeState? {
        return snoozeEventDao.getActiveByPackageName(packageName)
    }

    /**
     * Create snooze for app
     */
    suspend fun createSnooze(
        packageName: String,
        durationMinutes: Int
    ): Result<SnoozeState> {
        return try {
            val app = restrictedAppDao.getByPackageName(packageName)
                ?: return Result.failure(Exception("App not found"))

            // Deactivate any existing active snoozes for this app
            snoozeEventDao.deactivateAllForApp(app.id)

            val now = TimeProvider.currentTimeMillis()
            val expiryTime = now + (durationMinutes * 60 * 1000L)

            val snooze = SnoozeState(
                restrictedAppId = app.id,
                snoozeExpiryTimestamp = expiryTime,
                snoozeDurationMinutes = durationMinutes,
                isActive = true,
                createdAt = now
            )

            val id = snoozeEventDao.insert(snooze)
            Result.success(snooze.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deactivate snooze
     */
    suspend fun deactivateSnooze(id: Long): Result<Unit> {
        return try {
            snoozeEventDao.deactivate(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Check if package has active snooze
     */
    suspend fun hasActiveSnooze(packageName: String): Boolean {
        val currentTime = TimeProvider.currentTimeMillis()
        return snoozeEventDao.hasActiveSnoozeByPackage(packageName, currentTime)
    }

    /**
     * Get expired snoozes and deactivate them
     */
    suspend fun cleanupExpiredSnoozes(): List<SnoozeState> {
        val currentTime = TimeProvider.currentTimeMillis()
        val expired = snoozeEventDao.getExpiredActive(currentTime)
        snoozeEventDao.deactivateExpired(currentTime)
        return expired
    }

    /**
     * Delete old inactive snoozes (older than 30 days)
     */
    suspend fun cleanupOldSnoozes() {
        val thirtyDaysAgo = TimeProvider.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        snoozeEventDao.deleteOldInactive(thirtyDaysAgo)
    }

    // ========== Follow-up Responses ==========

    /**
     * Get all responses as Flow
     */
    fun getAllResponses(): Flow<List<FollowupResponse>> {
        return followupResponseDao.getAllAsFlow()
    }

    /**
     * Get responses for specific app
     */
    suspend fun getResponsesForApp(restrictedAppId: Long): List<FollowupResponse> {
        return followupResponseDao.getByAppId(restrictedAppId)
    }

    /**
     * Get recent responses
     */
    suspend fun getRecentResponses(limit: Long = 20): List<FollowupResponse> {
        return followupResponseDao.getRecent(limit)
    }

    /**
     * Record follow-up response
     */
    suspend fun recordFollowupResponse(
        packageName: String,
        sessionStartTime: Long,
        sessionEndTime: Long,
        response: ResponseType
    ): Result<FollowupResponse> {
        return try {
            val app = restrictedAppDao.getByPackageName(packageName)
                ?: return Result.failure(Exception("App not found"))

            val duration = (sessionEndTime - sessionStartTime) / 1000L

            val followupResponse = FollowupResponse(
                restrictedAppId = app.id,
                sessionStartTime = sessionStartTime,
                sessionEndTime = sessionEndTime,
                sessionDurationSeconds = duration,
                response = response,
                createdAt = TimeProvider.currentTimeMillis()
            )

            val id = followupResponseDao.insert(followupResponse)
            Result.success(followupResponse.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get response statistics
     */
    suspend fun getResponseStats(): Map<ResponseType, Long> {
        return followupResponseDao.getResponseCounts()
    }

    /**
     * Get average session duration
     */
    suspend fun getAverageSessionDuration(): Double {
        return followupResponseDao.getAverageSessionDuration()
    }

    /**
     * Delete old responses (older than 90 days)
     */
    suspend fun cleanupOldResponses() {
        val ninetyDaysAgo = TimeProvider.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)
        followupResponseDao.deleteOlderThan(ninetyDaysAgo)
    }

    // ========== Combined Operations ==========

    /**
     * Check if should show blocker for package
     * Returns true if:
     * - App is restricted and enabled
     * - No active snooze OR snooze has expired
     */
    suspend fun shouldShowBlocker(packageName: String): Boolean {
        // Check if app is restricted
        if (!isPackageRestricted(packageName)) {
            return false
        }

        // Check if there's an active snooze
        val currentTime = TimeProvider.currentTimeMillis()
        val hasSnooze = snoozeEventDao.hasActiveSnoozeByPackage(packageName, currentTime)

        return !hasSnooze
    }

    /**
     * Get monitoring state for package
     */
    suspend fun getMonitoringStateForPackage(packageName: String): MonitoringState {
        val isRestricted = isPackageRestricted(packageName)
        if (!isRestricted) {
            return MonitoringState(isActive = false)
        }

        val app = restrictedAppDao.getByPackageName(packageName)
        val snooze = if (app != null) {
            snoozeEventDao.getActiveByAppId(app.id)
        } else null

        val currentTime = TimeProvider.currentTimeMillis()
        val session = if (app != null) {
            AppSession(
                restrictedAppId = app.id,
                packageName = packageName,
                startTime = currentTime,
                currentSnooze = snooze
            )
        } else null

        return MonitoringState(
            isActive = true,
            currentSession = session,
            activeSnoozes = if (app != null && snooze != null) {
                mapOf(app.id to snooze)
            } else emptyMap(),
            lastCheckedTime = currentTime
        )
    }

    /**
     * Get all monitoring states for enabled apps
     */
    suspend fun getAllMonitoringStates(): Map<String, MonitoringState> {
        val enabledApps = restrictedAppDao.getEnabled()
        val currentTime = TimeProvider.currentTimeMillis()

        return enabledApps.associate { app ->
            val snooze = snoozeEventDao.getActiveByAppId(app.id)
            val state = MonitoringState(
                isActive = true,
                currentSession = null,
                activeSnoozes = if (snooze != null) mapOf(app.id to snooze) else emptyMap(),
                lastCheckedTime = currentTime
            )
            app.packageName to state
        }
    }
}
