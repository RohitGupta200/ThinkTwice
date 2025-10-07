package com.app.thinktwice.database.repository

import com.app.thinktwice.database.Settings
import com.app.thinktwice.database.dao.SettingsDao
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDao: SettingsDao) {

    suspend fun saveSetting(userId: Long, key: String, value: String): Result<Unit> {
        return try {
            if (key.isBlank()) {
                Result.failure(Exception("Setting key cannot be empty"))
            } else {
                settingsDao.insertOrUpdate(userId, key, value)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSetting(userId: Long, key: String): Settings? {
        return settingsDao.getByUserAndKey(userId, key)
    }

    suspend fun getSettingValue(userId: Long, key: String): String? {
        return settingsDao.getValueByUserAndKey(userId, key)
    }

    suspend fun getSettingValue(userId: Long, key: String, defaultValue: String): String {
        return settingsDao.getValueByUserAndKey(userId, key) ?: defaultValue
    }

    suspend fun getBooleanSetting(userId: Long, key: String, defaultValue: Boolean = false): Boolean {
        return getSettingValue(userId, key)?.toBooleanStrictOrNull() ?: defaultValue
    }

    suspend fun getIntSetting(userId: Long, key: String, defaultValue: Int = 0): Int {
        return getSettingValue(userId, key)?.toIntOrNull() ?: defaultValue
    }

    suspend fun getLongSetting(userId: Long, key: String, defaultValue: Long = 0L): Long {
        return getSettingValue(userId, key)?.toLongOrNull() ?: defaultValue
    }

    suspend fun getFloatSetting(userId: Long, key: String, defaultValue: Float = 0f): Float {
        return getSettingValue(userId, key)?.toFloatOrNull() ?: defaultValue
    }

    suspend fun saveBooleanSetting(userId: Long, key: String, value: Boolean): Result<Unit> {
        return saveSetting(userId, key, value.toString())
    }

    suspend fun saveIntSetting(userId: Long, key: String, value: Int): Result<Unit> {
        return saveSetting(userId, key, value.toString())
    }

    suspend fun saveLongSetting(userId: Long, key: String, value: Long): Result<Unit> {
        return saveSetting(userId, key, value.toString())
    }

    suspend fun saveFloatSetting(userId: Long, key: String, value: Float): Result<Unit> {
        return saveSetting(userId, key, value.toString())
    }

    suspend fun getUserSettings(userId: Long): List<Settings> {
        return settingsDao.getByUserId(userId)
    }

    fun getUserSettingsFlow(userId: Long): Flow<List<Settings>> {
        return settingsDao.getByUserIdFlow(userId)
    }

    suspend fun getAllSettings(): List<Settings> {
        return settingsDao.getAll()
    }

    suspend fun deleteSetting(userId: Long, key: String): Result<Unit> {
        return try {
            settingsDao.delete(userId, key)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUserSettings(userId: Long): Result<Unit> {
        return try {
            settingsDao.deleteByUserId(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSettingsAsMap(userId: Long): Map<String, String> {
        return getUserSettings(userId).associate { it.key to it.value_ }
    }

    // Common setting keys as constants
    object SettingKeys {
        const val THEME = "theme"
        const val LANGUAGE = "language"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val AUTO_SAVE = "auto_save"
        const val FONT_SIZE = "font_size"
        const val DARK_MODE = "dark_mode"
        const val SYNC_ENABLED = "sync_enabled"
        const val BACKUP_FREQUENCY = "backup_frequency"
        const val LAST_SYNC_TIME = "last_sync_time"
        const val USER_PREFERENCES = "user_preferences"
    }
}