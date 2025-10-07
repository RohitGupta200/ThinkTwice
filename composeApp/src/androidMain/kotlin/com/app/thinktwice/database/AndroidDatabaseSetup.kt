package com.app.thinktwice.database

import android.content.Context

/**
 * Android-specific database setup example
 */
object AndroidDatabaseSetup {

    /**
     * Creates a database service instance for Android
     * Call this in your Application class or Activity
     */
    fun createDatabaseService(context: Context): ThinkTwiceDatabaseService {
        val driverFactory = DatabaseDriverFactory(context)
        return ThinkTwiceDatabaseService(driverFactory)
    }

    /**
     * Example integration in MainActivity:
     *
     * class MainActivity : ComponentActivity() {
     *     private lateinit var databaseService: ThinkTwiceDatabaseService
     *
     *     override fun onCreate(savedInstanceState: Bundle?) {
     *         super.onCreate(savedInstanceState)
     *
     *         // Initialize database
     *         databaseService = AndroidDatabaseSetup.createDatabaseService(this)
     *
     *         setContent {
     *             App(databaseService = databaseService)
     *         }
     *     }
     * }
     */
}