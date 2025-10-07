package com.app.thinktwice.database

/**
 * Example usage of the ThinkTwice Database Layer
 * This demonstrates how to perform common database operations
 */
object DatabaseExample {

    suspend fun exampleUsage(databaseService: ThinkTwiceDatabaseService) {
        try {
            // 1. Create a new user
            val createUserResult = databaseService.userRepository.createUser(
                username = "johndoe",
                email = "john.doe@example.com",
                firstName = "John",
                lastName = "Doe"
            )

            if (createUserResult.isSuccess) {
                val userId = createUserResult.getOrThrow()
                println("Created user with ID: $userId")

                // 2. Create some notes for the user
                val noteResult = databaseService.noteRepository.createNote(
                    userId = userId,
                    title = "My First Note",
                    content = "This is the content of my first note",
                    category = "Personal",
                    isImportant = true
                )

                if (noteResult.isSuccess) {
                    val noteId = noteResult.getOrThrow()
                    println("Created note with ID: $noteId")
                }

                // 3. Create another note
                databaseService.noteRepository.createNote(
                    userId = userId,
                    title = "Shopping List",
                    content = "Milk, Bread, Eggs, Cheese",
                    category = "Tasks"
                )

                // 4. Save some user settings
                databaseService.settingsRepository.saveBooleanSetting(userId, "dark_mode", true)
                databaseService.settingsRepository.saveSetting(userId, "language", "en")
                databaseService.settingsRepository.saveIntSetting(userId, "font_size", 14)

                // 5. Retrieve data
                val user = databaseService.userRepository.getUserById(userId)
                println("Retrieved user: ${user?.username}")

                val userNotes = databaseService.noteRepository.getNotesByUserId(userId)
                println("User has ${userNotes.size} notes")

                val importantNotes = databaseService.noteRepository.getImportantNotes()
                println("Found ${importantNotes.size} important notes")

                // 6. Search notes
                val searchResults = databaseService.noteRepository.searchNotes("First")
                println("Search results: ${searchResults.size} notes found")

                // 7. Get settings
                val darkMode = databaseService.settingsRepository.getBooleanSetting(userId, "dark_mode")
                val language = databaseService.settingsRepository.getSettingValue(userId, "language", "en")
                val fontSize = databaseService.settingsRepository.getIntSetting(userId, "font_size", 12)

                println("User settings - Dark Mode: $darkMode, Language: $language, Font Size: $fontSize")

                // 8. Update a note
                if (userNotes.isNotEmpty()) {
                    val firstNote = userNotes.first()
                    databaseService.noteRepository.updateNote(
                        id = firstNote.id,
                        title = firstNote.title,
                        content = "${firstNote.content}\n\nUpdated content!",
                        category = firstNote.category,
                        isImportant = false
                    )
                    println("Updated note: ${firstNote.id}")
                }

                // 9. Use transactions for atomic operations
                databaseService.transaction {
                    // Create multiple related records atomically
                    databaseService.noteRepository.createNote(
                        userId = userId,
                        title = "Transaction Note 1",
                        content = "This note is created in a transaction"
                    )

                    databaseService.noteRepository.createNote(
                        userId = userId,
                        title = "Transaction Note 2",
                        content = "This note is also created in the same transaction"
                    )
                }

                // 10. Get database statistics
                val dbInfo = databaseService.getDatabaseInfo()
                println("Database Info - Users: ${dbInfo.userCount}, Notes: ${dbInfo.noteCount}, Settings: ${dbInfo.settingsCount}")

            } else {
                println("Failed to create user: ${createUserResult.exceptionOrNull()?.message}")
            }

        } catch (e: Exception) {
            println("Error during database operations: ${e.message}")
        }
    }

    suspend fun demonstrateFlows(databaseService: ThinkTwiceDatabaseService) {
        // Example of using Flow for reactive data
        databaseService.userRepository.getAllUsers().collect { users ->
            println("Users updated: ${users.size} users in database")
        }

        // You can also collect notes for a specific user
        val userId = 1L
        databaseService.noteRepository.getNotesByUserIdFlow(userId).collect { notes ->
            println("Notes for user $userId: ${notes.size} notes")
        }
    }
}