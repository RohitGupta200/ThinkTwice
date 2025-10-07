import SwiftUI
import FamilyControls

/**
 * View for selecting apps to restrict using FamilyActivityPicker
 */
@available(iOS 16.0, *)
struct AppSelectionView: View {

    @StateObject private var familyControlsManager = FamilyControlsManager.shared
    @StateObject private var settingsManager = ManagedSettingsManager.shared

    @State private var isPickerPresented = false
    @State private var selection = FamilyActivitySelection()
    @State private var isMonitoring = false
    @State private var showAlert = false
    @State private var alertMessage = ""
    @State private var isRequestingPermission = false

    var body: some View {
        VStack(spacing: 24) {
            if familyControlsManager.needsAuthorization() {
                // Show authorization request
                authorizationView
            } else {
                // Show app selection
                appSelectionView
            }
        }
        .padding(24)
    }

    private var authorizationView: some View {
        VStack(spacing: 24) {
            Image(systemName: "lock.shield")
                .font(.system(size: 60))
                .foregroundColor(.blue)

            Text("Screen Time Permission Required")
                .font(.system(size: 24, weight: .bold))
                .multilineTextAlignment(.center)

            Text("ThinkTwice uses Apple's Screen Time API to help you manage app usage. This requires your authorization.")
                .font(.system(size: 16))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .lineSpacing(4)

            // Show current status
            HStack {
                Circle()
                    .fill(statusColor)
                    .frame(width: 10, height: 10)
                Text(familyControlsManager.getAuthorizationStatusString())
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
            }
            .padding(.vertical, 8)

            Button(action: {
                requestPermission()
            }) {
                HStack {
                    if isRequestingPermission {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .scaleEffect(0.8)
                    } else {
                        Image(systemName: "checkmark.shield.fill")
                    }
                    Text(isRequestingPermission ? "Requesting..." : "Grant Permission")
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(isRequestingPermission ? Color.gray : Color.blue)
                .foregroundColor(.white)
                .cornerRadius(12)
            }
            .disabled(isRequestingPermission)

            VStack(spacing: 8) {
                Text("Important:")
                    .font(.system(size: 14, weight: .semibold))
                Text("• You'll see an iOS system dialog\n• Tap 'Allow' to grant access\n• This is required for app blocking to work")
                    .font(.system(size: 12))
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.leading)
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(8)
        }
        .padding()
    }

    private var statusColor: Color {
        switch familyControlsManager.authorizationStatus {
        case .approved:
            return .green
        case .denied:
            return .red
        case .notDetermined:
            return .orange
        @unknown default:
            return .gray
        }
    }

    private func requestPermission() {
        print("AppSelectionView: requestPermission() called")
        print("AppSelectionView: Current authorization status: \(familyControlsManager.authorizationStatus)")

        isRequestingPermission = true

        Task { @MainActor in
            print("AppSelectionView: Task started on MainActor")

            do {
                print("AppSelectionView: Calling familyControlsManager.requestAuthorization()...")
                try await familyControlsManager.requestAuthorization()
                print("AppSelectionView: requestAuthorization() returned successfully")

                // Check status after request
                familyControlsManager.checkAuthorizationStatus()
                print("AppSelectionView: Checked status after request: \(familyControlsManager.authorizationStatus)")

                if familyControlsManager.isAuthorized {
                    alertMessage = "✅ Permission granted! You can now select apps to restrict."
                    print("AppSelectionView: ✅ Authorization GRANTED")
                } else {
                    alertMessage = "⚠️ Permission was not granted. Status: \(familyControlsManager.getAuthorizationStatusString())\n\nPlease try again or check Settings > Screen Time."
                    print("AppSelectionView: ⚠️ Authorization NOT granted. Status: \(familyControlsManager.authorizationStatus)")
                }
                showAlert = true

            } catch {
                let errorDetails = """
                ❌ Error requesting permission:
                \(error.localizedDescription)

                Error type: \(type(of: error))

                Possible causes:
                • Missing entitlements
                • Screen Time not enabled
                • iOS restrictions in place

                Please check Settings > Screen Time
                """
                alertMessage = errorDetails
                showAlert = true

                print("AppSelectionView: ❌ ERROR: \(error)")
                print("AppSelectionView: Error details: \(error.localizedDescription)")
            }

            isRequestingPermission = false
            print("AppSelectionView: requestPermission() completed")
        }
    }

    private var appSelectionView: some View {
        VStack(spacing: 24) {
            Text("Choose Apps to Restrict")
                .font(.system(size: 28, weight: .bold))

            Text("Select the apps you want to block to help achieve your goals")
                .font(.system(size: 16))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)

            // Selected apps display
            if !selection.applicationTokens.isEmpty || !selection.categoryTokens.isEmpty {
                VStack(alignment: .leading, spacing: 12) {
                    Text("Selected Apps")
                        .font(.system(size: 18, weight: .semibold))

                    // Show count (actual app names not available via API)
                    Text("\(selection.applicationTokens.count) apps selected")
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
            }

            // Choose apps button
            Button(action: {
                isPickerPresented = true
            }) {
                HStack {
                    Image(systemName: "app.badge.checkmark")
                    Text(selection.applicationTokens.isEmpty ? "Choose Apps" : "Modify Selection")
                }
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(Color(hex: "3A3D4A"))
                .cornerRadius(12)
            }

            // Save and Start Monitoring button
            if !selection.applicationTokens.isEmpty {
                VStack(spacing: 12) {
                    Button(action: {
                        // Save via bridge
                        IOSAppLockingBridge.shared.setRestrictedApps(selection)
                        settingsManager.setRestrictedApps(selection)

                        // Start monitoring
                        let success = IOSAppLockingBridge.shared.startMonitoring()
                        isMonitoring = success

                        alertMessage = success ?
                            "Restrictions applied! \(selection.applicationTokens.count) apps are now blocked." :
                            "Failed to start monitoring. Please check permissions."
                        showAlert = true
                    }) {
                        HStack {
                            Image(systemName: "checkmark.shield.fill")
                            Text("Save & Start Monitoring")
                        }
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                        .background(Color.green)
                        .cornerRadius(12)
                    }

                    // Monitoring status indicator
                    if isMonitoring {
                        HStack {
                            Circle()
                                .fill(Color.green)
                                .frame(width: 10, height: 10)
                            Text("Monitoring Active")
                                .font(.system(size: 14))
                                .foregroundColor(.green)
                        }
                    }
                }
            }

            Spacer()

            Text("You can modify these at any point")
                .font(.system(size: 16))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .familyActivityPicker(
            isPresented: $isPickerPresented,
            selection: $selection
        )
        .alert("Status", isPresented: $showAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(alertMessage)
        }
        .onAppear {
            // Load saved selection on appear
            IOSAppLockingBridge.shared.loadSavedSelection()
            selection = settingsManager.restrictedApps
            isMonitoring = IOSAppLockingBridge.shared.isMonitoringActive()
        }
    }
}

#Preview {
    if #available(iOS 16.0, *) {
        AppSelectionView()
    }
}
