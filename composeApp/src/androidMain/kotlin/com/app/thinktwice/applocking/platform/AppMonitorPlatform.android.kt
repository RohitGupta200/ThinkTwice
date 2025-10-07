package com.app.thinktwice.applocking.platform

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.app.thinktwice.applocking.models.AppSession
import com.app.thinktwice.applocking.models.SnoozeState
import com.app.thinktwice.applocking.service.AppMonitorService
import com.app.thinktwice.applocking.ui.BlockerActivity
import com.app.thinktwice.applocking.ui.FollowupActivity
import com.app.thinktwice.applocking.receiver.SnoozeAlarmReceiver
import com.app.thinktwice.applocking.util.PermissionManager

/**
 * Android implementation of AppMonitorPlatform
 */
actual class AppMonitorPlatform(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val permissionManager = PermissionManager(context)

    /**
     * Get list of all installed user apps
     */
    actual suspend fun getInstalledApps(): List<AppInfo> {
        return try {
            val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getInstalledApplications(
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledApplications(0)
            }

            packages
                .filter { app ->
                    // Only include user-installed apps (exclude system apps)
                    (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0
                }
                .mapNotNull { app ->
                    try {
                        AppInfo(
                            packageName = app.packageName,
                            appName = app.loadLabel(packageManager).toString(),
                            iconPath = null // Icons handled separately in UI
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                .sortedBy { it.appName }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get currently foreground app using UsageStatsManager
     */
    actual suspend fun getCurrentForegroundApp(): String? {
        if (!permissionManager.hasUsageStatsPermission()) {
            return null
        }

        return try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val currentTime = System.currentTimeMillis()

            // Query events from last 2 seconds
            val events = usageStatsManager.queryEvents(currentTime - 2000, currentTime)

            var lastPackageName: String? = null
            val event = android.app.usage.UsageEvents.Event()

            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                // Look for MOVE_TO_FOREGROUND events
                if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastPackageName = event.packageName
                }
            }

            lastPackageName
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Launch blocker overlay to block restricted app
     * Uses SYSTEM_ALERT_WINDOW permission to show overlay over other apps
     */
    actual suspend fun launchBlockerUI(packageName: String) {
        android.util.Log.d("AppMonitorPlatform", "launchBlockerUI() called for package: $packageName")

        try {
            // Use overlay service instead of activity to bypass background launch restrictions
            com.app.thinktwice.applocking.overlay.BlockerOverlayService.showBlocker(context, packageName)
            android.util.Log.d("AppMonitorPlatform", "Blocker overlay service started")
        } catch (e: Exception) {
            android.util.Log.e("AppMonitorPlatform", "ERROR launching blocker overlay", e)
        }
    }

    /**
     * Launch follow-up activity
     */
    actual suspend fun launchFollowupUI(session: AppSession) {
        try {
            val intent = Intent(context, FollowupActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(FollowupActivity.EXTRA_PACKAGE_NAME, session.packageName)
                putExtra(FollowupActivity.EXTRA_SESSION_START, session.startTime)
                putExtra(FollowupActivity.EXTRA_SESSION_END, session.endTime ?: System.currentTimeMillis())
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // If can't launch activity, show notification instead
            showNotification(
                title = "Quick Check",
                message = "Did you complete the intended action?",
                action = NotificationAction.OpenFollowupScreen
            )
        }
    }

    /**
     * Schedule exact alarm for snooze expiry
     */
    actual suspend fun scheduleSnoozeAlarm(snooze: SnoozeState) {
        try {
            val intent = Intent(context, SnoozeAlarmReceiver::class.java).apply {
                putExtra(SnoozeAlarmReceiver.EXTRA_SNOOZE_ID, snooze.id)
                putExtra(SnoozeAlarmReceiver.EXTRA_RESTRICTED_APP_ID, snooze.restrictedAppId)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                snooze.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Use setExactAndAllowWhileIdle for precise timing
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    snooze.snoozeExpiryTimestamp,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    snooze.snoozeExpiryTimestamp,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            // Log error
        }
    }

    /**
     * Cancel scheduled snooze alarm
     */
    actual suspend fun cancelSnoozeAlarm(snoozeId: Long) {
        try {
            val intent = Intent(context, SnoozeAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                snoozeId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        } catch (e: Exception) {
            // Log error
        }
    }

    /**
     * Show local notification
     */
    actual suspend fun showNotification(
        title: String,
        message: String,
        action: NotificationAction
    ) {
        // Implementation will use NotificationManager
        // For now, delegate to service
        val intent = Intent(context, AppMonitorService::class.java).apply {
            this.action = AppMonitorService.ACTION_SHOW_NOTIFICATION
            putExtra("title", title)
            putExtra("message", message)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Check if required permissions are granted
     */
    actual suspend fun hasRequiredPermissions(): Boolean {
        val hasPermission = permissionManager.hasUsageStatsPermission()
        android.util.Log.d("AppMonitorPlatform", "hasRequiredPermissions() = $hasPermission")
        return hasPermission
    }

    /**
     * Request required permissions by opening settings
     */
    actual suspend fun requestPermissions() {
        permissionManager.requestUsageStatsPermission()
    }

    /**
     * Start monitoring service
     */
    actual suspend fun startMonitoring() {
        android.util.Log.d("AppMonitorPlatform", "startMonitoring() called - this is a NO-OP because service is already started")
        // NOTE: This is a no-op because the service should already be running
        // The service is started by ActivateMonitoringButton or on app selection
        // This method exists for platform consistency but doesn't need to do anything
    }

    /**
     * Stop monitoring service
     */
    actual suspend fun stopMonitoring() {
        val intent = Intent(context, AppMonitorService::class.java).apply {
            action = AppMonitorService.ACTION_STOP_MONITORING
        }
        context.startService(intent)
    }

    /**
     * Check if monitoring service is running
     */
    actual suspend fun isMonitoringActive(): Boolean {
        return AppMonitorService.isServiceRunning.value
    }
}
