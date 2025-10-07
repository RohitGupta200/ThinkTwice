package com.app.thinktwice.database

/**
 * Integration guide for setting up the ThinkTwice Database Layer
 */
object DatabaseIntegration {

    /**
     * Creates and returns a configured database service instance
     * Call this once in your application initialization
     */
    fun createDatabaseService(driverFactory: DatabaseDriverFactory): ThinkTwiceDatabaseService {
        return ThinkTwiceDatabaseService(driverFactory)
    }

    /**
     * Example of how to initialize the database service in your App
     */
    /*
    // In your Android Application class or Main Activity:

    class MainActivity : ComponentActivity() {
        private lateinit var databaseService: ThinkTwiceDatabaseService

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Initialize database
            val driverFactory = DatabaseDriverFactory(applicationContext)
            databaseService = DatabaseIntegration.createDatabaseService(driverFactory)

            // Use the database service throughout your app
            // You can pass it to your ViewModels or use dependency injection
        }
    }

    // In your iOS Main:

    fun MainViewController() = ComposeUIViewController {
        val driverFactory = DatabaseDriverFactory()
        val databaseService = DatabaseIntegration.createDatabaseService(driverFactory)

        // Use the database service in your Compose UI
        App(databaseService = databaseService)
    }
    */

    /**
     * Example Repository Pattern Usage
     */
    /*
    class UserViewModel(private val databaseService: ThinkTwiceDatabaseService) : ViewModel() {

        fun createUser(username: String, email: String, firstName: String, lastName: String) {
            viewModelScope.launch {
                val result = databaseService.userRepository.createUser(username, email, firstName, lastName)
                if (result.isSuccess) {
                    // Handle success
                } else {
                    // Handle error
                }
            }
        }

        fun getUserNotes(userId: Long): Flow<List<Note>> {
            return databaseService.noteRepository.getNotesByUserIdFlow(userId)
        }
    }
    */

    /**
     * Available Operations Summary:
     *
     * USER OPERATIONS:
     * - Create user: databaseService.userRepository.createUser()
     * - Get user by ID: databaseService.userRepository.getUserById()
     * - Get user by username: databaseService.userRepository.getUserByUsername()
     * - Get user by email: databaseService.userRepository.getUserByEmail()
     * - Update user: databaseService.userRepository.updateUser()
     * - Delete user: databaseService.userRepository.deleteUser()
     * - Get all users: databaseService.userRepository.getAllUsers()
     * - Get user count: databaseService.userRepository.getUserCount()
     *
     * NOTE OPERATIONS:
     * - Create note: databaseService.noteRepository.createNote()
     * - Get note by ID: databaseService.noteRepository.getNoteById()
     * - Get notes by user: databaseService.noteRepository.getNotesByUserId()
     * - Get notes by category: databaseService.noteRepository.getNotesByCategory()
     * - Get important notes: databaseService.noteRepository.getImportantNotes()
     * - Search notes: databaseService.noteRepository.searchNotes()
     * - Update note: databaseService.noteRepository.updateNote()
     * - Delete note: databaseService.noteRepository.deleteNote()
     * - Mark as important: databaseService.noteRepository.markNoteAsImportant()
     * - Get note count: databaseService.noteRepository.getNoteCount()
     *
     * SETTINGS OPERATIONS:
     * - Save setting: databaseService.settingsRepository.saveSetting()
     * - Get setting: databaseService.settingsRepository.getSettingValue()
     * - Save boolean: databaseService.settingsRepository.saveBooleanSetting()
     * - Get boolean: databaseService.settingsRepository.getBooleanSetting()
     * - Save/Get int, long, float values with type-safe methods
     * - Delete setting: databaseService.settingsRepository.deleteSetting()
     * - Get user settings: databaseService.settingsRepository.getUserSettings()
     *
     * TRANSACTION OPERATIONS:
     * - Execute in transaction: databaseService.transaction { /* operations */ }
     *
     * REACTIVE DATA (FLOWS):
     * - All major queries have Flow versions for reactive UI updates
     * - Example: getAllUsers() vs getAllUsersFlow()
     */
}