package com.app.thinktwice.network

import com.app.thinktwice.database.DatabaseDriverFactory
import com.app.thinktwice.database.ThinkTwiceDatabaseService
import com.app.thinktwice.service.ThinkTwiceService

/**
 * iOS-specific network and service setup
 */
object IosNetworkSetup {

    /**
     * Creates a complete ThinkTwiceService with both database and network capabilities
     */
    fun createThinkTwiceService(): ThinkTwiceService {
        // Create database service
        val databaseDriverFactory = DatabaseDriverFactory()
        val databaseService = ThinkTwiceDatabaseService(databaseDriverFactory)

        // Create network service
        val httpClientFactory = HttpClientFactory(null)
        val apiService = BasicApiService(httpClientFactory.create())

        // Combine both services
        return ThinkTwiceService(databaseService, apiService)
    }

    /**
     * Example integration in iOS MainViewController:
     *
     * ```kotlin
     * fun MainViewController(): UIViewController {
     *     val thinkTwiceService = IosNetworkSetup.createThinkTwiceService()
     *
     *     return ComposeUIViewController {
     *         LaunchedEffect(Unit) {
     *             // Login
     *             val authResult = thinkTwiceService.networkUsers.login("user@example.com", "password")
     *             if (authResult.isSuccess) {
     *                 val authResponse = authResult.getOrThrow()
     *
     *                 // Save user to local database
     *                 thinkTwiceService.localUsers.createUser(
     *                     authResponse.user.username,
     *                     authResponse.user.email,
     *                     authResponse.user.firstName,
     *                     authResponse.user.lastName
     *                 )
     *
     *                 // Sync data
     *                 thinkTwiceService.syncService.syncData(
     *                     deviceId = "ios-device-456",
     *                     clientVersion = "1.0.0"
     *                 )
     *             }
     *         }
     *
     *         App(thinkTwiceService = thinkTwiceService)
     *     }
     * }
     * ```
     */
}