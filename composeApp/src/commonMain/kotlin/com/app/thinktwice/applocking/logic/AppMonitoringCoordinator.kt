package com.app.thinktwice.applocking.logic

import com.app.thinktwice.applocking.models.AppRestrictionConfig
import com.app.thinktwice.applocking.models.AppSession
import com.app.thinktwice.applocking.models.ResponseType
import com.app.thinktwice.applocking.platform.AppMonitorPlatform
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Main coordinator for app monitoring functionality
 * Orchestrates platform-specific monitoring with shared business logic
 */
class AppMonitoringCoordinator(
    private val usageManager: UsageBucketManager,
    private val snoozeLogic: SnoozeTimerLogic,
    private val platform: AppMonitorPlatform,
    private val config: AppRestrictionConfig
) {

    private var monitoringJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _monitoringState = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _monitoringState.asStateFlow()

    /**
     * Start monitoring for restricted apps
     */
    suspend fun startMonitoring() {
        println("AppMonitoringCoordinator: startMonitoring() called")

        if (_monitoringState.value) {
            println("AppMonitoringCoordinator: Already monitoring, returning")
            return // Already monitoring
        }

        println("AppMonitoringCoordinator: Checking permissions")
        // Check permissions first
        if (!platform.hasRequiredPermissions()) {
            println("AppMonitoringCoordinator: ERROR - Required permissions not granted")
            throw IllegalStateException("Required permissions not granted")
        }

        println("AppMonitoringCoordinator: Starting platform monitoring")
        // Start platform monitoring
        platform.startMonitoring()

        println("AppMonitoringCoordinator: Starting polling loop")
        // Start polling loop
        startPollingLoop()

        _monitoringState.value = true
        println("AppMonitoringCoordinator: Monitoring started successfully")
    }

    /**
     * Stop monitoring
     */
    suspend fun stopMonitoring() {
        if (!_monitoringState.value) {
            return // Already stopped
        }

        // Cancel polling job
        monitoringJob?.cancel()
        monitoringJob = null

        // Stop platform monitoring
        platform.stopMonitoring()

        // Clear sessions
        usageManager.clearAllSessions()

        _monitoringState.value = false
    }

    /**
     * Start the polling loop to check for foreground apps
     */
    private fun startPollingLoop() {
        println("AppMonitoringCoordinator: startPollingLoop() called")
        monitoringJob = scope.launch {
            var lastForegroundApp: String? = null
            var pollCount = 0
            var lastBlockedApp: String? = null

            while (isActive) {
                try {
                    pollCount++
                    // Get current foreground app
                    val currentApp = platform.getCurrentForegroundApp()

                    if (pollCount % 10 == 1) { // Log every 10th poll to reduce noise
                        println("AppMonitoringCoordinator: Poll #$pollCount - Current app: ${currentApp ?: "null"}")
                    }

                    // Check if app changed
                    if (currentApp != lastForegroundApp) {
                        println("AppMonitoringCoordinator: App changed from '$lastForegroundApp' to '$currentApp'")
                        handleAppChange(lastForegroundApp, currentApp)
                        lastForegroundApp = currentApp
                        lastBlockedApp = null // Reset blocked app tracking
                    }

                    // Note: Removed continuous re-launching since overlay service handles this better
                    // The overlay will stay on top until user explicitly dismisses it

                    // Check for expired snoozes
                    checkExpiredSnoozes()

                    // Use faster polling when a restricted app is in foreground
                    val interval = if (currentApp != null && usageManager.shouldShowBlocker(currentApp)) {
                        500L // 0.5 seconds for restricted apps - very aggressive
                    } else {
                        config.idlePollingIntervalSeconds * 1000L
                    }

                    delay(interval)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Log error but continue monitoring
                    println("AppMonitoringCoordinator: Error in polling loop: ${e.message}")
                    delay(config.pollingIntervalSeconds * 1000L)
                }
            }
        }
    }

    /**
     * Handle app change (foreground app switched)
     */
    private suspend fun handleAppChange(previousApp: String?, currentApp: String?) {
        println("AppMonitoringCoordinator: handleAppChange() - Previous: '$previousApp', Current: '$currentApp'")

        // Handle previous app closed
        if (previousApp != null) {
            println("AppMonitoringCoordinator: Handling closed app: $previousApp")
            val session = usageManager.onAppClosed(previousApp)
            if (session != null && config.showFollowupScreen) {
                println("AppMonitoringCoordinator: Showing follow-up screen for: $previousApp")
                // Show follow-up screen
                platform.launchFollowupUI(session)
            }
        }

        // Handle new app opened
        if (currentApp != null) {
            println("AppMonitoringCoordinator: Checking if app should be blocked: $currentApp")
            val shouldBlock = usageManager.onAppOpened(currentApp)
            println("AppMonitoringCoordinator: Should block '$currentApp'? $shouldBlock")
            if (shouldBlock) {
                println("AppMonitoringCoordinator: Launching blocker UI for: $currentApp")
                // Show blocker
                platform.launchBlockerUI(currentApp)
            }
        }
    }

    /**
     * Check for expired snoozes and re-show blocker if needed
     */
    private suspend fun checkExpiredSnoozes() {
        val packagesToBlock = usageManager.checkExpiredSnoozes()

        // Get current foreground app
        val currentApp = platform.getCurrentForegroundApp()

        // If current app has expired snooze, re-show blocker
        if (currentApp != null && packagesToBlock.contains(currentApp)) {
            platform.launchBlockerUI(currentApp)
        }
    }

    /**
     * Handle snooze selection from blocker screen
     */
    suspend fun handleSnoozeSelected(packageName: String, durationMinutes: Int): Result<Unit> {
        val result = usageManager.onSnoozeSelected(packageName, durationMinutes)

        return if (result.isSuccess) {
            val snooze = result.getOrNull()!!
            // Schedule platform alarm for snooze expiry
            platform.scheduleSnoozeAlarm(snooze)
            Result.success(Unit)
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Failed to create snooze"))
        }
    }

    /**
     * Handle snooze expiry (called by platform alarm receiver)
     */
    suspend fun handleSnoozeExpired(packageName: String) {
        val currentApp = platform.getCurrentForegroundApp()
        val isAppInForeground = currentApp == packageName

        val shouldReBlock = usageManager.onSnoozeExpired(packageName, isAppInForeground)

        if (shouldReBlock) {
            // Re-show blocker
            platform.launchBlockerUI(packageName)
        }
    }

    /**
     * Handle follow-up response submission
     */
    suspend fun handleFollowupResponse(session: AppSession, response: ResponseType): Result<Unit> {
        return usageManager.onFollowupResponse(session, response)
    }

    /**
     * Add app to restricted list and start monitoring if needed
     */
    suspend fun addRestrictedApp(
        appId: String,
        appName: String,
        packageName: String,
        iconPath: String? = null
    ): Result<Unit> {
        val result = usageManager.addRestrictedApp(appId, appName, packageName, iconPath)

        // If monitoring is active and this is the first restricted app, start monitoring
        if (result.isSuccess) {
            val count = usageManager.getEnabledRestrictedApps().first().size
            if (count == 1 && !_monitoringState.value) {
                startMonitoring()
            }
        }

        return result
    }

    /**
     * Remove app from restricted list
     */
    suspend fun removeRestrictedApp(appId: Long): Result<Unit> {
        val result = usageManager.removeRestrictedApp(appId)

        // If no more restricted apps, stop monitoring
        if (result.isSuccess) {
            val count = usageManager.getEnabledRestrictedApps().first().size
            if (count == 0 && _monitoringState.value) {
                stopMonitoring()
            }
        }

        return result
    }

    /**
     * Toggle app restriction
     */
    suspend fun toggleAppRestriction(appId: Long, isEnabled: Boolean): Result<Unit> {
        return usageManager.toggleAppRestriction(appId, isEnabled)
    }

    /**
     * Get installed apps from platform
     */
    suspend fun getInstalledApps() = platform.getInstalledApps()

    /**
     * Request permissions
     */
    suspend fun requestPermissions() = platform.requestPermissions()

    /**
     * Check if has permissions
     */
    suspend fun hasPermissions() = platform.hasRequiredPermissions()

    /**
     * Perform periodic cleanup
     */
    suspend fun performCleanup() {
        usageManager.performCleanup()
    }

    /**
     * Get all restricted apps
     */
    fun getRestrictedApps() = usageManager.getRestrictedApps()

    /**
     * Get enabled restricted apps
     */
    fun getEnabledRestrictedApps() = usageManager.getEnabledRestrictedApps()

    /**
     * Get response statistics
     */
    suspend fun getResponseStats() = usageManager.getResponseStats()

    /**
     * Cleanup resources
     */
    fun dispose() {
        scope.cancel()
        monitoringJob?.cancel()
    }
}
