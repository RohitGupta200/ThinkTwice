package com.app.thinktwice.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.thinktwice.applocking.models.RestrictedApp
import com.app.thinktwice.database.ThinkTwiceDatabase
import com.app.thinktwice.database.utils.TimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Data Access Object for RestrictedApp table
 */
class RestrictedAppDao(private val database: ThinkTwiceDatabase) {

    private val queries = database.restrictedAppQueries

    /**
     * Get all restricted apps as Flow
     */
    fun getAllAsFlow(): Flow<List<RestrictedApp>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toModel() } }
    }

    /**
     * Get all enabled restricted apps as Flow
     */
    fun getEnabledAsFlow(): Flow<List<RestrictedApp>> {
        return queries.selectEnabled()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toModel() } }
    }

    /**
     * Get all restricted apps
     */
    suspend fun getAll(): List<RestrictedApp> {
        return queries.selectAll().executeAsList().map { it.toModel() }
    }

    /**
     * Get enabled restricted apps
     */
    suspend fun getEnabled(): List<RestrictedApp> {
        return queries.selectEnabled().executeAsList().map { it.toModel() }
    }

    /**
     * Get restricted app by package name
     */
    suspend fun getByPackageName(packageName: String): RestrictedApp? {
        return queries.selectByPackageName(packageName).executeAsOneOrNull()?.toModel()
    }

    /**
     * Get restricted app by ID
     */
    suspend fun getById(id: Long): RestrictedApp? {
        return queries.selectById(id).executeAsOneOrNull()?.toModel()
    }

    /**
     * Insert or update restricted app
     */
    suspend fun insert(app: RestrictedApp) {
        queries.insert(
            appId = app.appId,
            appName = app.appName,
            packageName = app.packageName,
            iconPath = app.iconPath,
            isEnabled = if (app.isEnabled) 1 else 0,
            createdAt = app.createdAt,
            updatedAt = app.updatedAt
        )
    }

    /**
     * Update restricted app
     */
    suspend fun update(app: RestrictedApp) {
        queries.update(
            appName = app.appName,
            packageName = app.packageName,
            iconPath = app.iconPath,
            isEnabled = if (app.isEnabled) 1 else 0,
            updatedAt = TimeProvider.currentTimeMillis(),
            id = app.id
        )
    }

    /**
     * Update enabled status
     */
    suspend fun updateEnabled(id: Long, isEnabled: Boolean) {
        queries.updateEnabled(
            isEnabled = if (isEnabled) 1 else 0,
            updatedAt = TimeProvider.currentTimeMillis(),
            id = id
        )
    }

    /**
     * Delete by ID
     */
    suspend fun deleteById(id: Long) {
        queries.deleteById(id)
    }

    /**
     * Delete by package name
     */
    suspend fun deleteByPackageName(packageName: String) {
        queries.deleteByPackageName(packageName)
    }

    /**
     * Delete all
     */
    suspend fun deleteAll() {
        queries.deleteAll()
    }

    /**
     * Count enabled apps
     */
    suspend fun countEnabled(): Long {
        return queries.countEnabled().executeAsOne()
    }

    /**
     * Check if package is restricted
     */
    suspend fun isPackageRestricted(packageName: String): Boolean {
        return getByPackageName(packageName)?.isEnabled == true
    }
}

/**
 * Extension function to convert database model to domain model
 */
private fun com.app.thinktwice.database.RestrictedApp.toModel(): RestrictedApp {
    return RestrictedApp(
        id = id,
        appId = appId,
        appName = appName,
        packageName = packageName,
        iconPath = iconPath,
        isEnabled = isEnabled == 1L,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
