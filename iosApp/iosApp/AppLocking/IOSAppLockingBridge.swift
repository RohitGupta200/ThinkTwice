import Foundation
import FamilyControls
import ManagedSettings
import DeviceActivity
import UserNotifications

/**
 * Bridge between Kotlin/Native and Swift for iOS app locking functionality
 *
 * This class exposes iOS-specific app blocking features to Kotlin code
 * via @objc annotations for Objective-C interoperability.
 */
@available(iOS 15.0, *)
@objc public class IOSAppLockingBridge: NSObject {

    // MARK: - Singleton

    @objc public static let shared = IOSAppLockingBridge()

    // MARK: - Managers

    private let familyControls = FamilyControlsManager.shared
    private let managedSettings = ManagedSettingsManager.shared
    private let center = DeviceActivityCenter()

    // MARK: - State

    private var isMonitoring = false
    private var blockerCallback: ((String) -> Void)?

    // MARK: - Initialization

    private override init() {
        super.init()
        setupNotifications()
    }

    // MARK: - Authorization

    /**
     * Check if Screen Time authorization is granted
     */
    @objc public func hasScreenTimePermission() -> Bool {
        return familyControls.isAuthorized
    }

    /**
     * Request Screen Time authorization
     * Must be called from main thread
     */
    @objc public func requestScreenTimePermission(completion: @escaping (Bool, String?) -> Void) {
        Task { @MainActor in
            do {
                try await familyControls.requestAuthorization()
                completion(familyControls.isAuthorized, nil)
            } catch {
                completion(false, error.localizedDescription)
            }
        }
    }

    // MARK: - App Selection & Persistence

    /**
     * Get current restricted apps selection
     */
    @objc public func getCurrentSelection() -> [String: Any] {
        // FamilyActivitySelection cannot be directly converted to Objective-C types
        // We'll store it internally and return a status dictionary
        return [
            "hasSelection": !managedSettings.restrictedApps.applicationTokens.isEmpty,
            "appCount": managedSettings.restrictedApps.applicationTokens.count
        ]
    }

    /**
     * Set restricted apps from FamilyActivitySelection
     * This will be called from SwiftUI after user selects apps
     * Note: Not @objc because FamilyActivitySelection is not representable in Objective-C
     */
    public func setRestrictedApps(_ selection: FamilyActivitySelection) {
        managedSettings.setRestrictedApps(selection)

        // Save to UserDefaults for persistence
        saveSelectionToUserDefaults(selection)

        // Apply restrictions immediately
        managedSettings.applyRestrictions()

        print("IOSBridge: Restricted apps set, count: \(selection.applicationTokens.count)")
    }

    /**
     * Load saved app selection from persistence
     */
    @objc public func loadSavedSelection() {
        // Load from UserDefaults if available
        if let savedSelection = loadSelectionFromUserDefaults() {
            managedSettings.setRestrictedApps(savedSelection)
            print("IOSBridge: Loaded saved selection")
        }
    }

    // MARK: - Monitoring Control

    /**
     * Start monitoring restricted apps
     */
    @objc public func startMonitoring() -> Bool {
        guard hasScreenTimePermission() else {
            print("IOSBridge: Cannot start monitoring - no permission")
            return false
        }

        guard !managedSettings.restrictedApps.applicationTokens.isEmpty else {
            print("IOSBridge: Cannot start monitoring - no apps selected")
            return false
        }

        do {
            // Create 24-hour monitoring schedule
            let schedule = DeviceActivitySchedule(
                intervalStart: DateComponents(hour: 0, minute: 0),
                intervalEnd: DateComponents(hour: 23, minute: 59),
                repeats: true
            )

            // Create event for app launches
            let event = DeviceActivityEvent(
                applications: managedSettings.restrictedApps.applicationTokens,
                threshold: DateComponents(second: 1) // Trigger after 1 second
            )

            // Start monitoring
            try center.startMonitoring(
                .daily,
                during: schedule,
                events: [.appLaunch: event]
            )

            isMonitoring = true
            print("IOSBridge: Started monitoring successfully")
            return true

        } catch {
            print("IOSBridge: Failed to start monitoring: \(error)")
            return false
        }
    }

    /**
     * Stop monitoring restricted apps
     */
    @objc public func stopMonitoring() {
        center.stopMonitoring([.daily])
        isMonitoring = false
        print("IOSBridge: Stopped monitoring")
    }

    /**
     * Check if monitoring is active
     */
    @objc public func isMonitoringActive() -> Bool {
        return isMonitoring
    }

    // MARK: - Blocker UI

    /**
     * Show blocker UI for a specific app
     * This will be called from the DeviceActivity extension via notification
     */
    @objc public func showBlockerUI(forApp appName: String) {
        print("IOSBridge: Showing blocker for app: \(appName)")

        // Post notification to show blocker
        NotificationCenter.default.post(
            name: NSNotification.Name("ShowBlockerUI"),
            object: nil,
            userInfo: ["appName": appName]
        )

        // Send local notification as backup
        sendBlockerNotification(appName: appName)
    }

    // MARK: - Snooze Control

    /**
     * Activate snooze for specified duration
     */
    @objc public func activateSnooze(durationMinutes: Int) {
        managedSettings.snooze(durationMinutes: durationMinutes)
        print("IOSBridge: Snooze activated for \(durationMinutes) minutes")
    }

    /**
     * Check if snooze is currently active
     */
    @objc public func isSnoozed() -> Bool {
        return !managedSettings.isRestrictionActive
    }

    // MARK: - Restrictions Control

    /**
     * Apply restrictions (shield apps)
     */
    @objc public func applyRestrictions() {
        managedSettings.applyRestrictions()
        print("IOSBridge: Restrictions applied")
    }

    /**
     * Remove restrictions (unshield apps)
     */
    @objc public func removeRestrictions() {
        managedSettings.removeRestrictions()
        print("IOSBridge: Restrictions removed")
    }

    /**
     * Clear all restrictions and stop monitoring
     */
    @objc public func clearAll() {
        stopMonitoring()
        managedSettings.clearAllRestrictions()
        clearSavedSelection()
        print("IOSBridge: All restrictions cleared")
    }

    // MARK: - Notifications

    private func setupNotifications() {
        // Request notification permission
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                print("IOSBridge: Notification permission granted")
            } else if let error = error {
                print("IOSBridge: Notification permission error: \(error)")
            }
        }
    }

    private func sendBlockerNotification(appName: String) {
        let content = UNMutableNotificationContent()
        content.title = "App Blocked"
        content.body = "You're trying to open a restricted app"
        content.sound = .default
        content.categoryIdentifier = "BLOCKER_CATEGORY"
        content.userInfo = ["appName": appName]

        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: nil // Immediate delivery
        )

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("IOSBridge: Failed to send notification: \(error)")
            }
        }
    }

    // MARK: - Persistence Helpers

    private func saveSelectionToUserDefaults(_ selection: FamilyActivitySelection) {
        // Encode FamilyActivitySelection to Data
        do {
            let encoder = JSONEncoder()
            // Store app count and categories count for now
            // FamilyActivitySelection tokens cannot be directly serialized
            let data: [String: Int] = [
                "appCount": selection.applicationTokens.count,
                "categoryCount": selection.categoryTokens.count
            ]
            let encoded = try encoder.encode(data)
            UserDefaults.standard.set(encoded, forKey: "savedAppSelection")
            print("IOSBridge: Saved selection to UserDefaults")
        } catch {
            print("IOSBridge: Failed to save selection: \(error)")
        }
    }

    private func loadSelectionFromUserDefaults() -> FamilyActivitySelection? {
        // Note: We cannot fully restore FamilyActivitySelection from persistence
        // because tokens are opaque and app-specific
        // This is a limitation of the FamilyControls API
        // The actual selection is maintained by ManagedSettingsManager
        return managedSettings.restrictedApps.applicationTokens.isEmpty ? nil : managedSettings.restrictedApps
    }

    private func clearSavedSelection() {
        UserDefaults.standard.removeObject(forKey: "savedAppSelection")
        print("IOSBridge: Cleared saved selection")
    }
}

// MARK: - DeviceActivityName Extension

@available(iOS 15.0, *)
extension DeviceActivityName {
    static let daily = Self("daily")
}

@available(iOS 15.0, *)
extension DeviceActivityEvent.Name {
    static let appLaunch = Self("appLaunch")
}
