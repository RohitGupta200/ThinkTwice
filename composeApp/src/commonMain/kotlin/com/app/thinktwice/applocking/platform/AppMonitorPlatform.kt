package com.app.thinktwice.applocking.platform

import com.app.thinktwice.applocking.models.AppSession
import com.app.thinktwice.applocking.models.SnoozeState

/**
 * Platform-specific interface for app monitoring functionality
 * Implemented differently for Android and iOS
 */
expect class AppMonitorPlatform {

    /**
     * Get list of all installed apps on the device
     * Returns list of AppInfo containing package name, app name, and icon path
     */
    suspend fun getInstalledApps(): List<AppInfo>

    /**
     * Get currently foreground app package name
     * Returns null if cannot determine or no permission
     */
    suspend fun getCurrentForegroundApp(): String?

    /**
     * Launch the blocker UI for the given package
     * Android: Launches BlockerActivity
     * iOS: Shows blocker view or notification
     */
    suspend fun launchBlockerUI(packageName: String)

    /**
     * Launch the follow-up UI for the given session
     * Android: Launches FollowupActivity
     * iOS: Shows local notification or follow-up view
     */
    suspend fun launchFollowupUI(session: AppSession)

    /**
     * Schedule an alarm/timer for snooze expiry
     * Android: Uses AlarmManager.setExactAndAllowWhileIdle()
     * iOS: Uses Timer or Background Task
     */
    suspend fun scheduleSnoozeAlarm(snooze: SnoozeState)

    /**
     * Cancel a scheduled snooze alarm
     */
    suspend fun cancelSnoozeAlarm(snoozeId: Long)

    /**
     * Show a local notification
     * Used for follow-up reminders or alerts
     */
    suspend fun showNotification(
        title: String,
        message: String,
        action: NotificationAction
    )

    /**
     * Check if required permissions are granted
     * Android: Usage Stats, Notification, Accessibility (optional)
     * iOS: Screen Time authorization
     */
    suspend fun hasRequiredPermissions(): Boolean

    /**
     * Request required permissions
     * Opens settings screen or authorization dialog
     */
    suspend fun requestPermissions()

    /**
     * Start monitoring service
     * Android: Starts ForegroundService
     * iOS: Schedules DeviceActivityMonitor
     */
    suspend fun startMonitoring()

    /**
     * Stop monitoring service
     */
    suspend fun stopMonitoring()

    /**
     * Check if monitoring is currently active
     */
    suspend fun isMonitoringActive(): Boolean
}

/**
 * Represents an installed app on the device
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val iconPath: String? = null
)

/**
 * Actions that can be triggered from notifications
 */
sealed class NotificationAction {
    data object OpenFollowupScreen : NotificationAction()
    data object OpenBlockerScreen : NotificationAction()
    data object OpenDashboard : NotificationAction()
}
