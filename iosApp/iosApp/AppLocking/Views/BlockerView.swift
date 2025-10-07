import SwiftUI

/**
 * Blocker view shown when user tries to open a restricted app
 *
 * Features:
 * - Fullscreen overlay
 * - Snooze duration selection
 * - Exit/dismiss button
 */
@available(iOS 15.0, *)
struct BlockerView: View {

    @Environment(\.dismiss) var dismiss
    @StateObject private var settingsManager = ManagedSettingsManager.shared

    let appName: String
    let onSnooze: (Int) -> Void
    let onExit: () -> Void

    @State private var selectedDuration: Int? = nil

    let snoozeDurations = [
        (minutes: 5, label: "5 minutes"),
        (minutes: 10, label: "10 minutes"),
        (minutes: 15, label: "15 minutes"),
        (minutes: 30, label: "30 minutes"),
        (minutes: 60, label: "1 hour")
    ]

    var body: some View {
        ZStack {
            Color(hex: "F5F5F5")
                .ignoresSafeArea()

            VStack(spacing: 24) {
                Spacer()

                // Title
                Text("Focus Mode Active")
                    .font(.system(size: 32, weight: .bold))
                    .foregroundColor(Color(hex: "1A1A1A"))
                    .multilineTextAlignment(.center)

                // Message
                VStack(spacing: 8) {
                    Text("You're trying to open a restricted app")
                        .font(.system(size: 18))
                        .foregroundColor(Color(hex: "666666"))
                        .multilineTextAlignment(.center)

                    Text(appName)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(Color(hex: "1A1A1A"))
                        .multilineTextAlignment(.center)
                }

                Spacer()
                    .frame(height: 48)

                // Snooze options
                VStack(spacing: 16) {
                    Text("Snooze for:")
                        .font(.system(size: 20, weight: .medium))
                        .foregroundColor(Color(hex: "1A1A1A"))

                    VStack(spacing: 12) {
                        ForEach(snoozeDurations, id: \.minutes) { duration in
                            SnoozeButton(
                                label: duration.label,
                                isSelected: selectedDuration == duration.minutes
                            ) {
                                selectedDuration = duration.minutes
                                onSnooze(duration.minutes)
                                dismiss()
                            }
                        }
                    }
                }

                Spacer()
                    .frame(height: 32)

                // Divider
                HStack {
                    Rectangle()
                        .fill(Color(hex: "CCCCCC"))
                        .frame(height: 1)

                    Text("OR")
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "666666"))
                        .padding(.horizontal, 16)

                    Rectangle()
                        .fill(Color(hex: "CCCCCC"))
                        .frame(height: 1)
                }

                Spacer()
                    .frame(height: 32)

                // Exit button
                Button(action: {
                    onExit()
                    dismiss()
                }) {
                    Text("Close")
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                        .background(Color(hex: "E53238"))
                        .cornerRadius(12)
                }

                // Info text
                Text("After snooze expires, the blocker will reappear if this app is still open.")
                    .font(.system(size: 14))
                    .foregroundColor(Color(hex: "666666"))
                    .multilineTextAlignment(.center)
                    .lineSpacing(6)

                Spacer()
            }
            .padding(24)
        }
        .interactiveDismissDisabled() // Prevent swipe to dismiss
    }
}

struct SnoozeButton: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 18, weight: .medium))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(isSelected ? Color(hex: "4CAF50") : Color(hex: "3A3D4A"))
                .cornerRadius(12)
        }
    }
}

// Helper extension for hex colors
extension Color {
    init(hex: String) {
        let scanner = Scanner(string: hex)
        var rgbValue: UInt64 = 0
        scanner.scanHexInt64(&rgbValue)

        let r = Double((rgbValue & 0xFF0000) >> 16) / 255.0
        let g = Double((rgbValue & 0x00FF00) >> 8) / 255.0
        let b = Double(rgbValue & 0x0000FF) / 255.0

        self.init(red: r, green: g, blue: b)
    }
}

#Preview {
    BlockerView(
        appName: "Instagram",
        onSnooze: { minutes in
            print("Snooze for \(minutes) minutes")
        },
        onExit: {
            print("Exit tapped")
        }
    )
}
