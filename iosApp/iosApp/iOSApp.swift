import SwiftUI

@main
struct iOSApp: App {

    init() {
        // Set up Kotlin-Swift bridge on app startup
        if #available(iOS 15.0, *) {
            KotlinBridgeSetup.setupBridge()
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // Handle deep links for blocker UI
                    handleDeepLink(url)
                }
        }
    }

    private func handleDeepLink(_ url: URL) {
        // Handle blocker://show URLs from notifications
        if url.scheme == "blocker", url.host == "show" {
            // Show blocker view
            NotificationCenter.default.post(
                name: NSNotification.Name("ShowBlockerUI"),
                object: nil,
                userInfo: ["fromURL": true]
            )
        }
    }
}