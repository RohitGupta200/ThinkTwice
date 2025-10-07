package com.app.thinktwice.applocking.platform

import com.app.thinktwice.applocking.models.AppSession
import com.app.thinktwice.applocking.models.SnoozeState
import platform.Foundation.NSLog

// Top-level bridge functions that will be set from Swift
var hasPermissionFunc: () -> Boolean = { false }
var requestPermissionFunc: (callback: (Boolean, String?) -> Unit) -> Unit = { _ -> }
var startMonitoringFunc: () -> Boolean = { false }
var stopMonitoringFunc: () -> Unit = { }
var isMonitoringFunc: () -> Boolean = { false }
var showBlockerFunc: (String) -> Unit = { }
var activateSnoozeFunc: (Int) -> Unit = { }

/**
 * iOS implementation of AppMonitorPlatform
 *
 * This bridges to Swift implementation via IOSAppLockingBridge
 *
 * Note: The bridge connection happens at runtime through Swift.
 * We use NSLog for now and will connect via Swift interop in the main app.
 */
actual class AppMonitorPlatform {

    /**
     * Get installed apps using FamilyActivityPicker
     * NOTE: iOS requires user to select apps via FamilyActivityPicker UI
     * We cannot programmatically list all apps
     */
    actual suspend fun getInstalledApps(): List<AppInfo> {
        // On iOS, apps are selected via FamilyActivityPicker
        // This returns an empty list - actual selection happens in SwiftUI
        NSLog("iOS: getInstalledApps - Use FamilyActivityPicker from SwiftUI")
        return emptyList()
    }

    /**
     * Get current foreground app
     * NOTE: iOS doesn't provide direct API for this
     * We rely on DeviceActivityMonitor callbacks instead
     */
    actual suspend fun getCurrentForegroundApp(): String? {
        // Not available on iOS
        // DeviceActivityMonitor provides launch events instead
        return null
    }

    /**
     * Launch blocker UI
     * iOS: Shows blocker view or notification
     */
    actual suspend fun launchBlockerUI(packageName: String) {
        NSLog("iOS: Launching blocker for $packageName")
        showBlockerFunc(packageName)
    }

    /**
     * Launch follow-up UI
     * iOS: Shows local notification or follow-up view
     */
    actual suspend fun launchFollowupUI(session: AppSession) {
        NSLog("iOS: Launching followup for ${session.packageName}")
        // Call Swift bridge to show follow-up
        // Or show local notification
    }

    /**
     * Schedule snooze alarm using Timer or Background Task
     */
    actual suspend fun scheduleSnoozeAlarm(snooze: SnoozeState) {
        NSLog("iOS: Scheduling snooze alarm for ${snooze.snoozeExpiryTimestamp}")
        // Calculate duration in minutes - just use a default for now
        val duration = 15 // Default 15 minutes
        activateSnoozeFunc(duration)
    }

    /**
     * Cancel snooze alarm
     */
    actual suspend fun cancelSnoozeAlarm(snoozeId: Long) {
        NSLog("iOS: Canceling snooze alarm $snoozeId")
        // Cancel Timer
    }

    /**
     * Show local notification
     */
    actual suspend fun showNotification(
        title: String,
        message: String,
        action: NotificationAction
    ) {
        NSLog("iOS: Showing notification - $title: $message")
        // Use UNUserNotificationCenter
    }

    /**
     * Check if Screen Time authorization is granted
     */
    actual suspend fun hasRequiredPermissions(): Boolean {
        return hasPermissionFunc()
    }

    /**
     * Request Screen Time authorization
     */
    actual suspend fun requestPermissions() {
        NSLog("iOS: Requesting Screen Time permission")
        // Swift bridge handles async callback
        requestPermissionFunc { success, error ->
            NSLog("iOS: Permission request result: $success, error: $error")
        }
    }

    /**
     * Start monitoring via DeviceActivityMonitor
     */
    actual suspend fun startMonitoring() {
        NSLog("iOS: Starting device activity monitoring")
        val success = startMonitoringFunc()
        NSLog("iOS: Start monitoring result: $success")
    }

    /**
     * Stop monitoring
     */
    actual suspend fun stopMonitoring() {
        NSLog("iOS: Stopping device activity monitoring")
        stopMonitoringFunc()
    }

    /**
     * Check if monitoring is active
     */
    actual suspend fun isMonitoringActive(): Boolean {
        return isMonitoringFunc()
    }
}
