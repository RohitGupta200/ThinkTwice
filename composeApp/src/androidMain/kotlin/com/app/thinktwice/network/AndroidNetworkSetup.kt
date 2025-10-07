package com.app.thinktwice.network

import android.content.Context
import com.app.thinktwice.database.DatabaseDriverFactory
import com.app.thinktwice.database.ThinkTwiceDatabaseService
import com.app.thinktwice.service.ThinkTwiceService

/**
 * Android-specific network and service setup
 */
object AndroidNetworkSetup {

    /**
     * Creates a complete ThinkTwiceService with both database and network capabilities
     */
    fun createThinkTwiceService(context: Context): ThinkTwiceService {
        // Create database service
        val databaseDriverFactory = DatabaseDriverFactory(context)
        val databaseService = ThinkTwiceDatabaseService(databaseDriverFactory)

        // Create network service
        val httpClientFactory = HttpClientFactory(null)
        val apiService = BasicApiService(httpClientFactory.create())

        // Combine both services
        return ThinkTwiceService(databaseService, apiService)
    }

    /**
     * Example integration in Android MainActivity:
     *
     * ```kotlin
     * class MainActivity : ComponentActivity() {
     *     private lateinit var thinkTwiceService: ThinkTwiceService
     *
     *     override fun onCreate(savedInstanceState: Bundle?) {
     *         super.onCreate(savedInstanceState)
     *
     *         // Initialize complete service
     *         thinkTwiceService = AndroidNetworkSetup.createThinkTwiceService(this)
     *
     *         setContent {
     *             App(thinkTwiceService = thinkTwiceService)
     *         }
     *
     *         // Example usage
     *         lifecycleScope.launch {
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
     *                     deviceId = "android-device-123",
     *                     clientVersion = "1.0.0"
     *                 )
     *             }
     *         }
     *     }
     *
     *     override fun onDestroy() {
     *         super.onDestroy()
     *         thinkTwiceService.close()
     *     }
     * }
     * ```
     */
}