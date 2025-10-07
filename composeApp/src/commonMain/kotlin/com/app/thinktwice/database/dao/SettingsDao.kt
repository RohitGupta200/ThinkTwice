package com.app.thinktwice.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.thinktwice.database.Settings
import com.app.thinktwice.database.ThinkTwiceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import com.app.thinktwice.database.utils.TimeProvider

class SettingsDao(private val database: ThinkTwiceDatabase) {

    suspend fun insertOrUpdate(userId: Long, key: String, value: String) {
        val currentTime = TimeProvider.currentTimeMillis()
        database.settingsQueries.insertOrUpdateSetting(userId, key, value, currentTime, currentTime)
    }

    suspend fun getByUserAndKey(userId: Long, key: String): Settings? {
        return database.settingsQueries.getSettingByUserAndKey(userId, key).executeAsOneOrNull()
    }

    suspend fun getValueByUserAndKey(userId: Long, key: String): String? {
        return getByUserAndKey(userId, key)?.value_
    }

    suspend fun getByUserId(userId: Long): List<Settings> {
        return database.settingsQueries.getSettingsByUserId(userId).executeAsList()
    }

    fun getByUserIdFlow(userId: Long): Flow<List<Settings>> {
        return database.settingsQueries.getSettingsByUserId(userId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getAll(): List<Settings> {
        return database.settingsQueries.getAllSettings().executeAsList()
    }

    suspend fun delete(userId: Long, key: String) {
        database.settingsQueries.deleteSetting(userId, key)
    }

    suspend fun deleteByUserId(userId: Long) {
        database.settingsQueries.deleteSettingsByUserId(userId)
    }

    suspend fun deleteAll() {
        database.settingsQueries.deleteAllSettings()
    }
}