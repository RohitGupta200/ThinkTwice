package com.app.thinktwice.applocking.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.app.thinktwice.applocking.logic.AppMonitoringCoordinator
import com.app.thinktwice.applocking.logic.SnoozeTimerLogic
import com.app.thinktwice.applocking.logic.UsageBucketManager
import com.app.thinktwice.applocking.models.AppRestrictionConfig
import com.app.thinktwice.applocking.platform.AppMonitorPlatform
import com.app.thinktwice.database.DatabaseDriverFactory
import com.app.thinktwice.database.ThinkTwiceDatabase
import com.app.thinktwice.database.dao.FollowupResponseDao
import com.app.thinktwice.database.dao.RestrictedAppDao
import com.app.thinktwice.database.dao.SnoozeEventDao
import com.app.thinktwice.database.repository.AppRestrictionRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Foreground service that monitors app usage and triggers blocker when needed
 *
 * This service:
 * 1. Runs as foreground service (required by Android)
 * 2. Periodically checks foreground app using UsageStatsManager
 * 3. Delegates to AppMonitoringCoordinator for business logic
 * 4. Handles snooze timer expirations
 * 5. Persists across reboots (with BootReceiver)
 */
class AppMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var coordinator: AppMonitoringCoordinator? = null

    companion object {
        const val CHANNEL_ID = "app_monitor_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_MONITORING = "com.app.thinktwice.START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.app.thinktwice.STOP_MONITORING"
        const val ACTION_SHOW_NOTIFICATION = "com.app.thinktwice.SHOW_NOTIFICATION"

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // MUST call startForeground() immediately to avoid crash
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)

        when (intent?.action) {
            ACTION_STOP_MONITORING -> {
                stopMonitoring()
            }
            ACTION_SHOW_NOTIFICATION -> {
                val title = intent.getStringExtra("title") ?: ""
                val message = intent.getStringExtra("message") ?: ""
                showCustomNotification(title, message)
            }
            else -> {
                // Default: start monitoring (including null action)
                startMonitoring()
            }
        }

        return START_STICKY // Restart if killed by system
    }

    private fun startMonitoring() {
        if (_isServiceRunning.value) {
            android.util.Log.d("AppMonitorService", "Service already running")
            return // Already running
        }

        android.util.Log.d("AppMonitorService", "Starting monitoring service")
        _isServiceRunning.value = true

        // Initialize coordinator if not already created
        if (coordinator == null) {
            android.util.Log.d("AppMonitorService", "Initializing AppMonitoringCoordinator")
            coordinator = createCoordinator()
        }

        // Start monitoring
        serviceScope.launch {
            try {
                android.util.Log.d("AppMonitorService", "Starting coordinator monitoring")
                coordinator?.startMonitoring()
            } catch (e: Exception) {
                // Log error and stop service
                android.util.Log.e("AppMonitorService", "Error in monitoring", e)
                stopMonitoring()
            }
        }
    }

    private fun createCoordinator(): AppMonitoringCoordinator {
        // Initialize database
        val driver = DatabaseDriverFactory(this).createDriver()
        val database = ThinkTwiceDatabase(driver)

        // Create DAOs
        val restrictedAppDao = RestrictedAppDao(database)
        val snoozeEventDao = SnoozeEventDao(database)
        val followupResponseDao = FollowupResponseDao(database)

        // Create repository
        val repository = AppRestrictionRepository(
            restrictedAppDao,
            snoozeEventDao,
            followupResponseDao
        )

        // Create snooze logic
        val snoozeLogic = SnoozeTimerLogic(repository)

        // Create usage manager
        val usageManager = UsageBucketManager(repository, snoozeLogic)

        // Create platform
        val platform = AppMonitorPlatform(this)

        // Create config
        val config = AppRestrictionConfig()

        return AppMonitoringCoordinator(
            usageManager,
            snoozeLogic,
            platform,
            config
        )
    }

    private fun stopMonitoring() {
        serviceScope.launch {
            coordinator?.stopMonitoring()
            _isServiceRunning.value = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Monitoring",
                NotificationManager.IMPORTANCE_HIGH // HIGH importance required for full-screen intents
            ).apply {
                description = "Monitors restricted apps and shows blocker when needed"
                setShowBadge(false)
                // Allow full-screen intents (for blocker activity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowBubbles(true)
                }
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundNotification(): Notification {
        // TODO: Replace with actual main activity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ThinkTwice Active")
            .setContentText("Monitoring restricted apps")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with app icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun showCustomNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        _isServiceRunning.value = false
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Restart service when task is removed (if monitoring is enabled)
        if (_isServiceRunning.value) {
            val restartIntent = Intent(applicationContext, AppMonitorService::class.java).apply {
                action = ACTION_START_MONITORING
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(restartIntent)
            } else {
                startService(restartIntent)
            }
        }
    }
}
