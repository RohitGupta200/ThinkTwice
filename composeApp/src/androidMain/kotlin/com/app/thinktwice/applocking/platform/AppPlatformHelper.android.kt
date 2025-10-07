package com.app.thinktwice.applocking.platform

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Android implementation of AppPlatformHelper
 */
actual class AppPlatformHelper(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    /**
     * Check if usage stats permission is granted
     */
    actual suspend fun hasUsageStatsPermission(): Boolean = withContext(Dispatchers.IO) {
        try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Request usage stats permission
     * Opens the system settings page
     */
    actual suspend fun requestUsageStatsPermission() {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: open app details settings
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Get all installed apps that have a launcher icon (user-facing apps)
     */
    actual suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        try {
            // Get all apps with launcher intent (user-facing apps)
            val launcherIntent = Intent(Intent.ACTION_MAIN, null)
            launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

            val resolveInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(launcherIntent, 0)
            }

            android.util.Log.d("AppPlatformHelper", "Query returned ${resolveInfoList.size} raw results")

            val apps = resolveInfoList
                .mapNotNull { resolveInfo ->
                    try {
                        val activityInfo = resolveInfo.activityInfo
                        val packageName = activityInfo.packageName
                        val appName = resolveInfo.loadLabel(packageManager).toString()

                        android.util.Log.v("AppPlatformHelper", "Found app: $appName ($packageName)")

                        // Exclude our own app from the list
                        if (packageName == context.packageName) {
                            android.util.Log.d("AppPlatformHelper", "Excluding own app: $packageName")
                            null
                        } else {
                            AppInfo(
                                packageName = packageName,
                                appName = appName,
                                iconPath = null // Icons handled separately in UI
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AppPlatformHelper", "Error loading app: ${e.message}", e)
                        null
                    }
                }
                .distinctBy { it.packageName } // Remove duplicates
                .sortedBy { it.appName.lowercase() }

            android.util.Log.d("AppPlatformHelper", "Returning ${apps.size} apps to UI")
            apps
        } catch (e: Exception) {
            android.util.Log.e("AppPlatformHelper", "Error getting apps: ${e.message}")
            emptyList()
        }
    }

    /**
     * Observe permission state changes
     * Returns flow that emits permission state
     */
    actual fun observePermissionState(): Flow<Boolean> = flow {
        // Initial state
        emit(hasUsageStatsPermission())

        // Note: Android doesn't have a built-in way to observe permission changes
        // This would require polling or using lifecycle callbacks
        // For now, emit initial state only
    }.flowOn(Dispatchers.IO)
}
