import Foundation
import ManagedSettings
import FamilyControls

/**
 * Manages app restrictions using ManagedSettings
 *
 * This class:
 * - Shields/unshields selected apps
 * - Manages snooze state (temporarily unshield)
 * - Provides restriction status
 */
@available(iOS 15.0, *)
class ManagedSettingsManager: ObservableObject {

    static let shared = ManagedSettingsManager()

    private let store = ManagedSettingsStore()

    @Published var restrictedApps: FamilyActivitySelection = FamilyActivitySelection()
    @Published var isRestrictionActive = false

    private init() {}

    /**
     * Set apps to be restricted
     */
    func setRestrictedApps(_ selection: FamilyActivitySelection) {
        restrictedApps = selection
        applyRestrictions()
    }

    /**
     * Apply restrictions (shield apps)
     */
    func applyRestrictions() {
        store.shield.applications = restrictedApps.applicationTokens
        store.shield.applicationCategories = restrictedApps.categoryTokens.isEmpty ? nil : ShieldSettings.ActivityCategoryPolicy.specific(restrictedApps.categoryTokens)
        isRestrictionActive = true
    }

    /**
     * Remove restrictions (unshield apps)
     * Used when snooze is active
     */
    func removeRestrictions() {
        store.shield.applications = nil
        store.shield.applicationCategories = nil
        isRestrictionActive = false
    }

    /**
     * Temporarily disable restrictions for snooze period
     */
    func snooze(durationMinutes: Int) {
        removeRestrictions()

        // Schedule re-restriction after snooze expires
        DispatchQueue.main.asyncAfter(deadline: .now() + .seconds(durationMinutes * 60)) { [weak self] in
            self?.applyRestrictions()
            // TODO: Show blocker if app is still in foreground
        }
    }

    /**
     * Check if specific app is restricted
     */
    func isAppRestricted(bundleIdentifier: String) -> Bool {
        // Check if bundle ID is in restricted apps
        // This requires converting tokens back to bundle IDs
        // which is not directly supported by the API
        return isRestrictionActive
    }

    /**
     * Clear all restrictions
     */
    func clearAllRestrictions() {
        removeRestrictions()
        restrictedApps = FamilyActivitySelection()
    }
}
