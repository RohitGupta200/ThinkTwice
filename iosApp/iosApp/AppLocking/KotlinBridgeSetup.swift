import Foundation
import ComposeApp

/**
 * Sets up the bridge between Swift (IOSAppLockingBridge) and Kotlin (AppMonitorPlatform)
 *
 * This must be called early in the app lifecycle to connect Swift functionality
 * to Kotlin code.
 */
@available(iOS 15.0, *)
class KotlinBridgeSetup {

    static func setupBridge() {
        let bridge = IOSAppLockingBridge.shared

        // Set up permission checking
        AppMonitorPlatform_iosKt.hasPermissionFunc = {
            return KotlinBoolean(bool: bridge.hasScreenTimePermission())
        }

        // Set up permission request
        AppMonitorPlatform_iosKt.requestPermissionFunc = { callback in
            bridge.requestScreenTimePermission { success, error in
                callback(KotlinBoolean(bool: success), error)
            }
        }

        // Set up monitoring control
        AppMonitorPlatform_iosKt.startMonitoringFunc = {
            return KotlinBoolean(bool: bridge.startMonitoring())
        }

        AppMonitorPlatform_iosKt.stopMonitoringFunc = {
            bridge.stopMonitoring()
        }

        AppMonitorPlatform_iosKt.isMonitoringFunc = {
            return KotlinBoolean(bool: bridge.isMonitoringActive())
        }

        // Set up blocker UI
        AppMonitorPlatform_iosKt.showBlockerFunc = { packageName in
            bridge.showBlockerUI(forApp: packageName)
        }

        // Set up snooze
        AppMonitorPlatform_iosKt.activateSnoozeFunc = { minutes in
            bridge.activateSnooze(durationMinutes: Int(truncating: minutes))
        }

        print("KotlinBridgeSetup: Bridge configured successfully")
    }
}
