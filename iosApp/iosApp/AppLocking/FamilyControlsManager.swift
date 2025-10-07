import Foundation
import FamilyControls
import ManagedSettings

/**
 * Manages FamilyControls authorization and app selection
 *
 * This class:
 * - Requests Screen Time API authorization
 * - Manages selected apps via FamilyActivityPicker
 * - Provides authorization status
 */
@available(iOS 15.0, *)
class FamilyControlsManager: ObservableObject {

    static let shared = FamilyControlsManager()

    @Published var isAuthorized = false
    @Published var authorizationStatus: AuthorizationStatus = .notDetermined

    private let authCenter = AuthorizationCenter.shared

    private init() {
        checkAuthorizationStatus()
    }

    /**
     * Check current authorization status
     */
    func checkAuthorizationStatus() {
        authorizationStatus = authCenter.authorizationStatus
        isAuthorized = (authorizationStatus == .approved)
    }

    /**
     * Request Screen Time authorization
     * Must be called from main thread
     */
    @MainActor
    func requestAuthorization() async throws {
        print("FamilyControlsManager: Starting authorization request...")
        print("FamilyControlsManager: Current status BEFORE request: \(authorizationStatus)")

        do {
            try await authCenter.requestAuthorization(for: .individual)
            print("FamilyControlsManager: Authorization request completed")
            checkAuthorizationStatus()
            print("FamilyControlsManager: Current status AFTER request: \(authorizationStatus)")
        } catch {
            print("FamilyControlsManager: ❌ Failed to request authorization: \(error)")
            print("FamilyControlsManager: Error type: \(type(of: error))")
            print("FamilyControlsManager: Error description: \(error.localizedDescription)")
            throw error
        }
    }

    /**
     * Get authorization status string for UI
     */
    func getAuthorizationStatusString() -> String {
        switch authorizationStatus {
        case .notDetermined:
            return "Not Determined"
        case .denied:
            return "Denied"
        case .approved:
            return "Approved"
        @unknown default:
            return "Unknown"
        }
    }

    /**
     * Check if user needs to grant authorization
     */
    func needsAuthorization() -> Bool {
        return authorizationStatus != .approved
    }
}
