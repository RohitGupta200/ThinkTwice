package com.app.thinktwice.database

import com.app.thinktwice.database.dao.NoteDao
import com.app.thinktwice.database.dao.SettingsDao
import com.app.thinktwice.database.dao.UserDao

/**
 * Utility class to clear all data from the database
 */
class DatabaseCleaner(
    private val userDao: UserDao,
    private val noteDao: NoteDao,
    private val settingsDao: SettingsDao
) {

    /**
     * Clears all tables in the database
     * This will delete all users, notes, and settings
     */
    suspend fun clearAllTables() {
        // Order matters: delete child tables first due to foreign key constraints
        noteDao.deleteAll()
        settingsDao.deleteAll()
        userDao.deleteAll()
    }
}
