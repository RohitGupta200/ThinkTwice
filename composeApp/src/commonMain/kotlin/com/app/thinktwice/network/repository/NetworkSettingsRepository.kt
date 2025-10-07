package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiExceptionMapper
import com.app.thinktwice.network.BasicApiService
import com.app.thinktwice.network.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class NetworkSettingsRepository(private val apiService: BasicApiService) {

    suspend fun getUserSettings(userId: Long): Result<List<SettingDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserSettings(userId)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to get settings"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun getSettingByKey(userId: Long, key: String): Result<SettingDto> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Get setting by key not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun createSetting(userId: Long, key: String, value: String): Result<SettingDto> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateSettingRequest(key, value)
                val response = apiService.createSetting(userId, request)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to create setting"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun updateSetting(userId: Long, key: String, value: String): Result<SettingDto> {
        return withContext(Dispatchers.IO) {
            try {
                val request = UpdateSettingRequest(value)
                val response = apiService.updateSetting(userId, key, request)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to update setting"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun deleteSetting(userId: Long, key: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Delete setting not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun bulkUpdateSettings(userId: Long, settings: Map<String, String>): Result<List<SettingDto>> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Bulk update settings not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    // Convenience methods for type-safe settings
    suspend fun getBooleanSetting(userId: Long, key: String): Result<Boolean?> {
        return getSettingByKey(userId, key).map { setting ->
            setting.value.toBooleanStrictOrNull()
        }
    }

    suspend fun getIntSetting(userId: Long, key: String): Result<Int?> {
        return getSettingByKey(userId, key).map { setting ->
            setting.value.toIntOrNull()
        }
    }

    suspend fun getLongSetting(userId: Long, key: String): Result<Long?> {
        return getSettingByKey(userId, key).map { setting ->
            setting.value.toLongOrNull()
        }
    }

    suspend fun setBooleanSetting(userId: Long, key: String, value: Boolean): Result<SettingDto> {
        return updateSetting(userId, key, value.toString())
    }

    suspend fun setIntSetting(userId: Long, key: String, value: Int): Result<SettingDto> {
        return updateSetting(userId, key, value.toString())
    }

    suspend fun setLongSetting(userId: Long, key: String, value: Long): Result<SettingDto> {
        return updateSetting(userId, key, value.toString())
    }
}