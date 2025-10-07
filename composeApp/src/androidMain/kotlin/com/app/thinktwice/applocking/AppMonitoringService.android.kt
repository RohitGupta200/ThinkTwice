package com.app.thinktwice.applocking

import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.thinktwice.applocking.service.AppMonitorService

/**
 * Android implementation of AppMonitoringService
 */
actual class AppMonitoringService(private val context: Context) {

    /**
     * Start the app monitoring foreground service
     */
    actual fun start() {
        val intent = Intent(context, AppMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Stop the app monitoring service
     */
    actual fun stop() {
        val intent = Intent(context, AppMonitorService::class.java)
        context.stopService(intent)
    }

    /**
     * Check if service is running
     * Note: This is a simplified check
     */
    actual fun isRunning(): Boolean {
        // In a real implementation, you'd check if the service is actually running
        // For now, return false - the service will handle its own state
        return false
    }
}
