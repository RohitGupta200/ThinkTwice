package com.app.thinktwice.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.app.thinktwice.applocking.platform.AppInfo
import com.app.thinktwice.applocking.service.AppMonitorService
import com.app.thinktwice.database.DatabaseDriverFactory
import com.app.thinktwice.database.ThinkTwiceDatabase
import com.app.thinktwice.database.dao.RestrictedAppDao
import com.app.thinktwice.database.dao.SnoozeEventDao
import com.app.thinktwice.database.dao.FollowupResponseDao
import com.app.thinktwice.database.repository.AppRestrictionRepository
import com.app.thinktwice.database.utils.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AppSelection"

@Composable
actual fun StartMonitoringService(selectedApps: Set<String>, installedApps: List<AppInfo>) {
    // No-op
}

@Composable
actual fun rememberSaveAppsFunction(): suspend (Set<String>, List<AppInfo>) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { selectedApps: Set<String>, installedApps: List<AppInfo> ->
            saveAppsToDatabase(context, selectedApps, installedApps)
        }
    }
}

@Composable
actual fun rememberLoadAppsFunction(): suspend () -> Set<String> {
    val context = LocalContext.current
    return remember(context) {
        {
            loadAppsFromDatabase(context)
        }
    }
}

@Composable
fun rememberStartServiceFunction(): suspend (Set<String>, List<AppInfo>) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { selectedApps: Set<String>, installedApps: List<AppInfo> ->
            startMonitoringServiceImpl(context, selectedApps, installedApps)
        }
    }
}

actual suspend fun StartMonitoringServiceSuspend(selectedApps: Set<String>, installedApps: List<AppInfo>) {
    // This can't get context, so it's a no-op
    // Use rememberStartServiceFunction() instead from composable
}

private suspend fun saveAppsToDatabase(
    context: Context,
    selectedApps: Set<String>,
    installedApps: List<AppInfo>
) = withContext(Dispatchers.IO) {
    if (selectedApps.isEmpty()) {
        Log.d(TAG, "No apps to save - stopping service if running")
        // Stop service if no apps selected
        withContext(Dispatchers.Main) {
            try {
                val stopIntent = Intent(context, AppMonitorService::class.java)
                stopIntent.action = AppMonitorService.ACTION_STOP_MONITORING
                context.startService(stopIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping service", e)
            }
        }
        return@withContext
    }

    try {
        Log.d(TAG, "Saving ${selectedApps.size} apps to database AND starting service")

        // Initialize database
        val driver = DatabaseDriverFactory(context).createDriver()
        val database = ThinkTwiceDatabase(driver)

        val restrictedAppDao = RestrictedAppDao(database)
        val snoozeEventDao = SnoozeEventDao(database)
        val followupResponseDao = FollowupResponseDao(database)

        val repository = AppRestrictionRepository(
            restrictedAppDao,
            snoozeEventDao,
            followupResponseDao
        )

        // Clear existing apps first
        val existingApps = repository.getAllRestrictedAppsSync()
        existingApps.forEach { app ->
            repository.removeRestrictedApp(app.id)
        }
        Log.d(TAG, "Cleared ${existingApps.size} existing apps")

        // Save each selected app
        selectedApps.forEach { packageName ->
            val appInfo = installedApps.find { it.packageName == packageName }
            if (appInfo != null) {
                repository.addRestrictedApp(
                    appId = packageName,
                    appName = appInfo.appName,
                    packageName = packageName,
                    iconPath = appInfo.iconPath
                )
                Log.d(TAG, "Saved app: ${appInfo.appName} (${packageName})")
            } else {
                Log.w(TAG, "App info not found for package: $packageName")
            }
        }

        Log.d(TAG, "Successfully saved ${selectedApps.size} apps to database")

        // Start the monitoring service
        withContext(Dispatchers.Main) {
            try {
                val serviceIntent = Intent(context, AppMonitorService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                    Log.d(TAG, "Started foreground monitoring service")
                } else {
                    context.startService(serviceIntent)
                    Log.d(TAG, "Started monitoring service")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting monitoring service", e)
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error saving apps to database", e)
    }
}

private suspend fun loadAppsFromDatabase(context: Context): Set<String> = withContext(Dispatchers.IO) {
    try {
        Log.d(TAG, "Loading apps from database")

        // Initialize database
        val driver = DatabaseDriverFactory(context).createDriver()
        val database = ThinkTwiceDatabase(driver)

        val restrictedAppDao = RestrictedAppDao(database)
        val snoozeEventDao = SnoozeEventDao(database)
        val followupResponseDao = FollowupResponseDao(database)

        val repository = AppRestrictionRepository(
            restrictedAppDao,
            snoozeEventDao,
            followupResponseDao
        )

        val apps = repository.getAllRestrictedAppsSync()
        val packageNames = apps.map { it.packageName }.toSet()

        Log.d(TAG, "Loaded ${packageNames.size} apps from database: $packageNames")
        packageNames
    } catch (e: Exception) {
        Log.e(TAG, "Error loading apps from database", e)
        emptySet()
    }
}

private suspend fun startMonitoringServiceImpl(
    context: Context,
    selectedApps: Set<String>,
    installedApps: List<AppInfo>
) = withContext(Dispatchers.IO) {
    if (selectedApps.isEmpty()) return@withContext

    try {
        Log.d(TAG, "Saving ${selectedApps.size} apps to database")

        // Initialize database
        val driver = DatabaseDriverFactory(context).createDriver()
        val database = ThinkTwiceDatabase(driver)

        val restrictedAppDao = RestrictedAppDao(database)
        val snoozeEventDao = SnoozeEventDao(database)
        val followupResponseDao = FollowupResponseDao(database)

        val repository = AppRestrictionRepository(
            restrictedAppDao,
            snoozeEventDao,
            followupResponseDao
        )

        // Save each selected app
        selectedApps.forEach { packageName ->
            val appInfo = installedApps.find { it.packageName == packageName }
            if (appInfo != null) {
                repository.addRestrictedApp(
                    appId = packageName,
                    appName = appInfo.appName,
                    packageName = packageName,
                    iconPath = appInfo.iconPath
                )
                Log.d(TAG, "Saved app: ${appInfo.appName}")
            }
        }

        Log.d(TAG, "Apps saved, starting service from Activity context")

        // Start the monitoring service from main thread with Activity context
        withContext(Dispatchers.Main) {
            try {
                val serviceIntent = Intent(context, AppMonitorService::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                Log.d(TAG, "Monitoring service started successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start service: ${e.message}", e)
            }
        }

    } catch (e: Exception) {
        Log.e(TAG, "Error setting up monitoring", e)
    }
}
