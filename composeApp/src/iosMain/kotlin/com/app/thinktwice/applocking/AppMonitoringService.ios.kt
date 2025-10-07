package com.app.thinktwice.applocking

/**
 * iOS implementation of AppMonitoringService
 */
actual class AppMonitoringService {

    /**
     * Start the app monitoring
     * iOS uses Screen Time API and Device Activity monitoring
     */
    actual fun start() {
        // TODO: Start iOS monitoring using DeviceActivityMonitor
    }

    /**
     * Stop the app monitoring
     */
    actual fun stop() {
        // TODO: Stop iOS monitoring
    }

    /**
     * Check if service is running
     */
    actual fun isRunning(): Boolean {
        // TODO: Check iOS monitoring state
        return false
    }
}
