package com.app.thinktwice.applocking.models

import com.app.thinktwice.database.utils.TimeProvider

/**
 * Configuration for app restriction feature
 */
data class AppRestrictionConfig(
    val isMonitoringEnabled: Boolean = false,
    val defaultSnoozeDuration: SnoozeDuration = SnoozeDuration.FIVE_MINUTES,
    val showFollowupScreen: Boolean = true,
    val pollingIntervalSeconds: Int = 2, // How often to check for foreground app
    val idlePollingIntervalSeconds: Int = 10, // Polling interval when no restricted app is active
    val useAccessibilityService: Boolean = false // Android only
)

/**
 * Represents an app session being monitored
 */
data class AppSession(
    val restrictedAppId: Long,
    val packageName: String,
    val startTime: Long,
    var endTime: Long? = null,
    var isActive: Boolean = true,
    var currentSnooze: SnoozeState? = null
) {
    fun getDurationSeconds(): Long {
        val end = endTime ?: TimeProvider.currentTimeMillis()
        return (end - startTime) / 1000
    }

    fun isSnoozeActive(currentTimeMillis: Long): Boolean {
        return currentSnooze?.isCurrentlyActive(currentTimeMillis) == true
    }
}

/**
 * Represents the current monitoring state
 */
data class MonitoringState(
    val isActive: Boolean = false,
    val currentSession: AppSession? = null,
    val activeSnoozes: Map<Long, SnoozeState> = emptyMap(), // restrictedAppId to SnoozeState
    val lastCheckedTime: Long = 0L
)
