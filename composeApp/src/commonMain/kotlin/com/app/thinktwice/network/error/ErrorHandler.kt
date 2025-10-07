package com.app.thinktwice.network.error

import com.app.thinktwice.network.dto.ApiErrorResponse
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json

/**
 * Centralized error handler for API responses
 */
object ErrorHandler {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Handles HTTP response and throws appropriate exception if error
     */
    suspend fun handleResponse(response: HttpResponse) {
        if (response.status.isSuccess()) {
            return
        }

        val errorBody = try {
            response.bodyAsText()
        } catch (e: Exception) {
            null
        }

        val errorResponse = errorBody?.let {
            try {
                json.decodeFromString<ApiErrorResponse>(it)
            } catch (e: Exception) {
                null
            }
        }

        throw when (response.status.value) {
            401 -> ApiException.AuthException(
                statusCode = 401,
                errorResponse = errorResponse
            )
            403 -> ApiException.AuthException(
                statusCode = 403,
                errorResponse = errorResponse
            )
            404 -> ApiException.NotFoundException(
                resource = errorResponse?.detail ?: "Resource"
            )
            400, 422 -> {
                if (errorResponse != null) {
                    ApiException.ValidationException(errorResponse)
                } else {
                    ApiException.HttpException(response.status.value, errorBody, null)
                }
            }
            429 -> {
                val retryAfter = response.headers[HttpHeaders.RetryAfter]?.toIntOrNull()
                ApiException.RateLimitException(
                    errorResponse = errorResponse,
                    retryAfterSeconds = retryAfter
                )
            }
            in 500..599 -> ApiException.ServerException(
                statusCode = response.status.value,
                errorBody = errorBody
            )
            else -> ApiException.HttpException(
                statusCode = response.status.value,
                errorBody = errorBody,
                errorResponse = errorResponse
            )
        }
    }

    /**
     * Wraps API calls with proper error handling
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: JsonConvertException) {
            Result.failure(
                ApiException.SerializationException(
                    e.message ?: "Serialization error",
                    e
                )
            )
        } catch (e: Exception) {
            Result.failure(
                when {
                    e.message?.contains("timeout", ignoreCase = true) == true ||
                    e.message?.contains("connect", ignoreCase = true) == true -> {
                        ApiException.NetworkException(
                            e.message ?: "Network error",
                            e
                        )
                    }
                    else -> ApiException.UnknownException(
                        e.message ?: "Unknown error",
                        e
                    )
                }
            )
        }
    }
}
