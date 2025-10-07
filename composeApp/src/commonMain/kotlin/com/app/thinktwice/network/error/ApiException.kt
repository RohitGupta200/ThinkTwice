package com.app.thinktwice.network.error

import com.app.thinktwice.network.dto.ApiErrorResponse

/**
 * Base exception for all API-related errors
 */
sealed class ApiException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Network-related errors (no internet, timeout, etc.)
     */
    data class NetworkException(
        val originalMessage: String,
        val originalCause: Throwable? = null
    ) : ApiException("Network error: $originalMessage", originalCause)

    /**
     * HTTP errors with status codes
     */
    data class HttpException(
        val statusCode: Int,
        val errorBody: String?,
        val errorResponse: ApiErrorResponse? = null
    ) : ApiException("HTTP $statusCode: ${errorResponse?.detail ?: errorBody}")

    /**
     * Authentication/Authorization errors (401, 403)
     */
    data class AuthException(
        val statusCode: Int,
        val errorResponse: ApiErrorResponse? = null
    ) : ApiException("Auth error: ${errorResponse?.detail ?: "Unauthorized"}")

    /**
     * Validation errors (400, 422)
     */
    data class ValidationException(
        val errorResponse: ApiErrorResponse
    ) : ApiException("Validation error: ${errorResponse.detail}")

    /**
     * Rate limit exceeded (429)
     */
    data class RateLimitException(
        val errorResponse: ApiErrorResponse? = null,
        val retryAfterSeconds: Int? = null
    ) : ApiException("Rate limit exceeded: ${errorResponse?.detail ?: "Try again later"}")

    /**
     * Resource not found (404)
     */
    data class NotFoundException(
        val resource: String
    ) : ApiException("Resource not found: $resource")

    /**
     * Server errors (5xx)
     */
    data class ServerException(
        val statusCode: Int,
        val errorBody: String?
    ) : ApiException("Server error: $statusCode - $errorBody")

    /**
     * Serialization/Deserialization errors
     */
    data class SerializationException(
        val originalMessage: String,
        val originalCause: Throwable? = null
    ) : ApiException("Serialization error: $originalMessage", originalCause)

    /**
     * Unknown/Unexpected errors
     */
    data class UnknownException(
        val originalMessage: String,
        val originalCause: Throwable? = null
    ) : ApiException("Unknown error: $originalMessage", originalCause)
}
