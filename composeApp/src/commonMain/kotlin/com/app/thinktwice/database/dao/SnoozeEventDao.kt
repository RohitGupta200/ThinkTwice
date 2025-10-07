package com.app.thinktwice.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.thinktwice.applocking.models.SnoozeState
import com.app.thinktwice.database.ThinkTwiceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Data Access Object for SnoozeEvent table
 */
class SnoozeEventDao(private val database: ThinkTwiceDatabase) {

    private val queries = database.snoozeEventQueries

    /**
     * Get all snooze events as Flow
     */
    fun getAllAsFlow(): Flow<List<SnoozeState>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toModel() } }
    }

    /**
     * Get active snooze events as Flow
     */
    fun getActiveAsFlow(): Flow<List<SnoozeState>> {
        return queries.selectActive()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toModel() } }
    }

    /**
     * Get all snooze events
     */
    suspend fun getAll(): List<SnoozeState> {
        return queries.selectAll().executeAsList().map { it.toModel() }
    }

    /**
     * Get active snooze events
     */
    suspend fun getActive(): List<SnoozeState> {
        return queries.selectActive().executeAsList().map { it.toModel() }
    }

    /**
     * Get active snooze for specific app
     */
    suspend fun getActiveByAppId(restrictedAppId: Long): SnoozeState? {
        return queries.selectActiveByAppId(restrictedAppId).executeAsOneOrNull()?.toModel()
    }

    /**
     * Get active snooze by package name
     */
    suspend fun getActiveByPackageName(packageName: String): SnoozeState? {
        return queries.selectActiveByPackageName(packageName).executeAsOneOrNull()?.toModel()
    }

    /**
     * Get expired active snoozes
     */
    suspend fun getExpiredActive(currentTimeMillis: Long): List<SnoozeState> {
        return queries.selectExpiredActive(currentTimeMillis).executeAsList().map { it.toModel() }
    }

    /**
     * Get snooze by ID
     */
    suspend fun getById(id: Long): SnoozeState? {
        return queries.selectById(id).executeAsOneOrNull()?.toModel()
    }

    /**
     * Insert new snooze event
     */
    suspend fun insert(snooze: SnoozeState): Long {
        queries.insert(
            restrictedAppId = snooze.restrictedAppId,
            snoozeExpiryTimestamp = snooze.snoozeExpiryTimestamp,
            snoozeDurationMinutes = snooze.snoozeDurationMinutes.toLong(),
            isActive = if (snooze.isActive) 1 else 0,
            createdAt = snooze.createdAt
        )
        // Return the last inserted ID
        return queries.selectAll().executeAsList().maxOfOrNull { it.id } ?: 0L
    }

    /**
     * Deactivate snooze by ID
     */
    suspend fun deactivate(id: Long) {
        queries.deactivate(id)
    }

    /**
     * Deactivate all snoozes for a specific app
     */
    suspend fun deactivateAllForApp(restrictedAppId: Long) {
        queries.deactivateAllForApp(restrictedAppId)
    }

    /**
     * Deactivate expired snoozes
     */
    suspend fun deactivateExpired(currentTimeMillis: Long) {
        queries.deactivateExpired(currentTimeMillis)
    }

    /**
     * Delete by ID
     */
    suspend fun deleteById(id: Long) {
        queries.deleteById(id)
    }

    /**
     * Delete old inactive snoozes (cleanup)
     */
    suspend fun deleteOldInactive(olderThanTimestamp: Long) {
        queries.deleteOldInactive(olderThanTimestamp)
    }

    /**
     * Check if app currently has active snooze
     */
    suspend fun hasActiveSnooze(restrictedAppId: Long, currentTimeMillis: Long): Boolean {
        val snooze = getActiveByAppId(restrictedAppId)
        return snooze?.isCurrentlyActive(currentTimeMillis) == true
    }

    /**
     * Check if package currently has active snooze
     */
    suspend fun hasActiveSnoozeByPackage(packageName: String, currentTimeMillis: Long): Boolean {
        val snooze = getActiveByPackageName(packageName)
        return snooze?.isCurrentlyActive(currentTimeMillis) == true
    }
}

/**
 * Extension function to convert database model to domain model
 */
private fun com.app.thinktwice.database.SnoozeEvent.toModel(): SnoozeState {
    return SnoozeState(
        id = id,
        restrictedAppId = restrictedAppId,
        snoozeExpiryTimestamp = snoozeExpiryTimestamp,
        snoozeDurationMinutes = snoozeDurationMinutes.toInt(),
        isActive = isActive == 1L,
        createdAt = createdAt
    )
}
