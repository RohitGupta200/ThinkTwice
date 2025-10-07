package com.app.thinktwice.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.thinktwice.applocking.models.FollowupResponse
import com.app.thinktwice.applocking.models.ResponseType
import com.app.thinktwice.database.ThinkTwiceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Data Access Object for FollowupResponse table
 */
class FollowupResponseDao(private val database: ThinkTwiceDatabase) {

    private val queries = database.followupResponseQueries

    /**
     * Get all responses as Flow
     */
    fun getAllAsFlow(): Flow<List<FollowupResponse>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toModel() } }
    }

    /**
     * Get all responses
     */
    suspend fun getAll(): List<FollowupResponse> {
        return queries.selectAll().executeAsList().map { it.toModel() }
    }

    /**
     * Get responses by app ID
     */
    suspend fun getByAppId(restrictedAppId: Long): List<FollowupResponse> {
        return queries.selectByAppId(restrictedAppId).executeAsList().map { it.toModel() }
    }

    /**
     * Get recent responses (limited)
     */
    suspend fun getRecent(limit: Long = 20): List<FollowupResponse> {
        return queries.selectRecent(limit).executeAsList().map { it.toModel() }
    }

    /**
     * Get responses in date range
     */
    suspend fun getByDateRange(startTime: Long, endTime: Long): List<FollowupResponse> {
        return queries.selectByDateRange(startTime, endTime).executeAsList().map { it.toModel() }
    }

    /**
     * Get responses by type
     */
    suspend fun getByResponse(responseType: ResponseType): List<FollowupResponse> {
        return queries.selectByResponse(responseType.value).executeAsList().map { it.toModel() }
    }

    /**
     * Get response by ID
     */
    suspend fun getById(id: Long): FollowupResponse? {
        return queries.selectById(id).executeAsOneOrNull()?.toModel()
    }

    /**
     * Get response counts grouped by type
     */
    suspend fun getResponseCounts(): Map<ResponseType, Long> {
        return queries.selectResponseCounts().executeAsList()
            .associate {
                ResponseType.fromValue(it.response) to it.responseCount
            }
    }

    /**
     * Get response counts by app
     */
    suspend fun getResponseCountsByApp(): Map<String, Map<ResponseType, Long>> {
        return queries.selectResponseCountsByApp().executeAsList()
            .groupBy { it.appName ?: "Unknown" }
            .mapValues { (_, rows) ->
                rows.associate {
                    ResponseType.fromValue(it.response) to it.responseCount
                }
            }
    }

    /**
     * Get average session duration across all apps
     */
    suspend fun getAverageSessionDuration(): Double {
        return queries.selectAverageSessionDuration().executeAsOneOrNull()?.averageDuration ?: 0.0
    }

    /**
     * Get average session duration by app
     */
    suspend fun getAverageSessionDurationByApp(): Map<String, Double> {
        return queries.selectAverageSessionDurationByApp().executeAsList()
            .associate {
                it.appName to (it.averageDuration ?: 0.0)
            }
    }

    /**
     * Insert new response
     */
    suspend fun insert(response: FollowupResponse): Long {
        queries.insert(
            restrictedAppId = response.restrictedAppId,
            sessionStartTime = response.sessionStartTime,
            sessionEndTime = response.sessionEndTime,
            sessionDurationSeconds = response.sessionDurationSeconds,
            response = response.response.value,
            createdAt = response.createdAt
        )
        // Return the last inserted ID
        return queries.selectAll().executeAsList().maxOfOrNull { it.id } ?: 0L
    }

    /**
     * Delete by ID
     */
    suspend fun deleteById(id: Long) {
        queries.deleteById(id)
    }

    /**
     * Delete all responses for a specific app
     */
    suspend fun deleteByAppId(restrictedAppId: Long) {
        queries.deleteByAppId(restrictedAppId)
    }

    /**
     * Delete old responses (cleanup)
     */
    suspend fun deleteOlderThan(timestamp: Long) {
        queries.deleteOlderThan(timestamp)
    }
}

/**
 * Extension function to convert database model to domain model
 */
private fun com.app.thinktwice.database.FollowupResponse.toModel(): FollowupResponse {
    return FollowupResponse(
        id = id,
        restrictedAppId = restrictedAppId,
        sessionStartTime = sessionStartTime,
        sessionEndTime = sessionEndTime,
        sessionDurationSeconds = sessionDurationSeconds,
        response = ResponseType.fromValue(response),
        createdAt = createdAt
    )
}
