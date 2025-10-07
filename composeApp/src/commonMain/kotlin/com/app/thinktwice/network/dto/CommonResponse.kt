package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: ApiError? = null,
    val timestamp: Long = 0L
)

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val details: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

@Serializable
data class PaginationRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
    val sortBy: String? = null,
    val sortDirection: String = "ASC" // ASC or DESC
)