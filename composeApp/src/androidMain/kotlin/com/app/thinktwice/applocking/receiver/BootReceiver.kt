package com.app.thinktwice.applocking.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.thinktwice.applocking.service.AppMonitorService

/**
 * BroadcastReceiver that restarts monitoring service after device reboot
 * Only starts if monitoring was previously enabled
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        // Check if monitoring was enabled before reboot
        // This should be persisted in SharedPreferences or DataStore
        val prefs = context.getSharedPreferences("app_locking_prefs", Context.MODE_PRIVATE)
        val wasMonitoringEnabled = prefs.getBoolean("monitoring_enabled", false)

        if (wasMonitoringEnabled) {
            // Restart monitoring service
            val serviceIntent = Intent(context, AppMonitorService::class.java).apply {
                action = AppMonitorService.ACTION_START_MONITORING
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
