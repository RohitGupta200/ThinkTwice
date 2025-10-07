import DeviceActivity
import Foundation
import UserNotifications

/**
 * DeviceActivityMonitor extension for monitoring app launches
 *
 * This extension runs in the background and receives callbacks when:
 * - Monitoring interval starts
 * - Monitoring interval ends
 * - Event threshold is reached (app launch)
 * - Interruption occurs
 *
 * NOTE: This must be a separate app extension target in Xcode
 */
@available(iOS 15.0, *)
class DeviceActivityMonitorExtension: DeviceActivityMonitor {

    /**
     * Called when a device activity interval starts
     */
    override func intervalDidStart(for activity: DeviceActivityName) {
        super.intervalDidStart(for: activity)
        print("DeviceActivity: Interval started for \(activity)")

        // Apply restrictions at the start of monitoring
        // This is when the user has enabled restrictions
    }

    /**
     * Called when a device activity interval ends
     */
    override func intervalDidEnd(for activity: DeviceActivityName) {
        super.intervalDidEnd(for: activity)
        print("DeviceActivity: Interval ended for \(activity)")

        // Remove restrictions at the end of monitoring
    }

    /**
     * Called when a monitored event threshold is reached
     * This is our main hook for detecting app launches
     */
    override func eventDidReachThreshold(
        _ event: DeviceActivityEvent.Name,
        activity: DeviceActivityName
    ) {
        super.eventDidReachThreshold(event, activity: activity)
        print("DeviceActivity: Event threshold reached for \(event)")

        // This is called when a restricted app is launched
        // We should:
        // 1. Check if snooze is active
        // 2. If not, show blocker (via notification or direct UI)
        // 3. If yes, do nothing

        // Send notification to show blocker
        sendBlockerNotification()
    }

    /**
     * Called when monitoring is interrupted
     */
    override func intervalWillStartWarning(for activity: DeviceActivityName) {
        super.intervalWillStartWarning(for: activity)
        print("DeviceActivity: Interval will start warning for \(activity)")
    }

    /**
     * Called when interval warning ends
     */
    override func intervalWillEndWarning(for activity: DeviceActivityName) {
        super.intervalWillEndWarning(for: activity)
        print("DeviceActivity: Interval will end warning for \(activity)")
    }

    /**
     * Send notification to show blocker UI
     */
    private func sendBlockerNotification() {
        // Create notification content
        let content = UNMutableNotificationContent()
        content.title = "App Blocked"
        content.body = "You're trying to open a restricted app"
        content.sound = .default
        content.categoryIdentifier = "BLOCKER_CATEGORY"
        content.badge = 1

        // Create request with immediate trigger
        let request = UNNotificationRequest(
            identifier: "blocker_\(UUID().uuidString)",
            content: content,
            trigger: nil // Deliver immediately
        )

        // Add notification
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("DeviceActivity: Failed to send notification: \(error.localizedDescription)")
            } else {
                print("DeviceActivity: Blocker notification sent successfully")
            }
        }

        // Also post internal notification for app to handle
        // (Note: Extension and main app have shared container)
        let sharedDefaults = UserDefaults(suiteName: "group.com.app.thinktwice")
        sharedDefaults?.set(Date().timeIntervalSince1970, forKey: "lastBlockerTrigger")
        sharedDefaults?.synchronize()
    }
}
