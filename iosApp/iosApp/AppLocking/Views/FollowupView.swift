import SwiftUI

/**
 * Follow-up view shown after user closes a restricted app
 * Asks: "Did you complete the intended action?"
 */
struct FollowupView: View {

    @Environment(\.dismiss) var dismiss

    let appName: String
    let sessionDurationSeconds: Int
    let onResponse: (String) -> Void

    var body: some View {
        ZStack {
            Color(hex: "F5F5F5")
                .ignoresSafeArea()

            VStack(spacing: 24) {
                Spacer()

                // Title
                Text("Quick Check")
                    .font(.system(size: 32, weight: .bold))
                    .foregroundColor(Color(hex: "1A1A1A"))
                    .multilineTextAlignment(.center)

                Spacer()
                    .frame(height: 24)

                // Question
                Text("Did you complete the intended action?")
                    .font(.system(size: 20, weight: .medium))
                    .foregroundColor(Color(hex: "1A1A1A"))
                    .multilineTextAlignment(.center)
                    .lineSpacing(8)

                Spacer()
                    .frame(height: 16)

                // App info card
                VStack(spacing: 8) {
                    Text(appName)
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(Color(hex: "1A1A1A"))

                    Text("Session: \(formatDuration(sessionDurationSeconds))")
                        .font(.system(size: 14))
                        .foregroundColor(Color(hex: "666666"))
                }
                .frame(maxWidth: .infinity)
                .padding(16)
                .background(Color.white)
                .cornerRadius(12)

                Spacer()
                    .frame(height: 48)

                // Response buttons
                VStack(spacing: 12) {
                    ResponseButton(
                        text: "Yes",
                        backgroundColor: Color(hex: "4CAF50")
                    ) {
                        onResponse("yes")
                        dismiss()
                    }

                    ResponseButton(
                        text: "No",
                        backgroundColor: Color(hex: "E53238")
                    ) {
                        onResponse("no")
                        dismiss()
                    }

                    ResponseButton(
                        text: "Skip",
                        backgroundColor: Color(hex: "666666")
                    ) {
                        onResponse("skip")
                        dismiss()
                    }
                }

                Spacer()
                    .frame(height: 24)

                // Helper text
                Text("Your response helps improve your app usage insights")
                    .font(.system(size: 14))
                    .foregroundColor(Color(hex: "666666"))
                    .multilineTextAlignment(.center)

                Spacer()
            }
            .padding(24)
        }
    }

    private func formatDuration(_ seconds: Int) -> String {
        let minutes = seconds / 60
        let remainingSeconds = seconds % 60

        if minutes == 0 {
            return "\(remainingSeconds)s"
        } else if remainingSeconds == 0 {
            return "\(minutes)m"
        } else {
            return "\(minutes)m \(remainingSeconds)s"
        }
    }
}

struct ResponseButton: View {
    let text: String
    let backgroundColor: Color
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(backgroundColor)
                .cornerRadius(12)
        }
    }
}

#Preview {
    FollowupView(
        appName: "Instagram",
        sessionDurationSeconds: 325,
        onResponse: { response in
            print("Response: \(response)")
        }
    )
}
