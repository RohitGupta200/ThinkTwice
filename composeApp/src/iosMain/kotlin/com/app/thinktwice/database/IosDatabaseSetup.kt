package com.app.thinktwice.database

/**
 * iOS-specific database setup example
 */
object IosDatabaseSetup {

    /**
     * Creates a database service instance for iOS
     * Call this in your iOS main function or view controller
     */
    fun createDatabaseService(): ThinkTwiceDatabaseService {
        val driverFactory = DatabaseDriverFactory()
        return ThinkTwiceDatabaseService(driverFactory)
    }

    /**
     * Example integration in MainViewController:
     *
     * fun MainViewController(): UIViewController {
     *     val databaseService = IosDatabaseSetup.createDatabaseService()
     *
     *     return ComposeUIViewController {
     *         App(databaseService = databaseService)
     *     }
     * }
     */
}