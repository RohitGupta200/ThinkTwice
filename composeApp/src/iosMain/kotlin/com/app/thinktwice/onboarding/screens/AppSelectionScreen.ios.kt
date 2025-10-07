package com.app.thinktwice.onboarding.screens

import androidx.compose.runtime.Composable
import com.app.thinktwice.applocking.platform.AppInfo

@Composable
actual fun StartMonitoringService(selectedApps: Set<String>, installedApps: List<AppInfo>) {
    // iOS implementation - use Screen Time API
    // TODO: Implement iOS monitoring
}

actual suspend fun StartMonitoringServiceSuspend(selectedApps: Set<String>, installedApps: List<AppInfo>) {
    // iOS implementation - use Screen Time API
    // TODO: Implement iOS monitoring
}

@Composable
actual fun rememberSaveAppsFunction(): suspend (Set<String>, List<AppInfo>) -> Unit {
    return { _, _ ->
        // TODO: Implement iOS persistence
    }
}

@Composable
actual fun rememberLoadAppsFunction(): suspend () -> Set<String> {
    return {
        // TODO: Implement iOS loading
        emptySet()
    }
}
