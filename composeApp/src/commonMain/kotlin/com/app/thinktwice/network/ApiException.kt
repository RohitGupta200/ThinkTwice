package com.app.thinktwice.network

import com.app.thinktwice.network.dto.ApiError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode

sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class NetworkException(message: String = "Network error occurred", cause: Throwable? = null) : ApiException(message, cause)

    class TimeoutException(message: String = "Request timed out", cause: Throwable? = null) : ApiException(message, cause)

    class UnauthorizedException(message: String = "Unauthorized access", cause: Throwable? = null) : ApiException(message, cause)

    class ForbiddenException(message: String = "Access forbidden", cause: Throwable? = null) : ApiException(message, cause)

    class NotFoundException(message: String = "Resource not found", cause: Throwable? = null) : ApiException(message, cause)

    class BadRequestException(message: String = "Bad request", cause: Throwable? = null) : ApiException(message, cause)

    class ServerException(message: String = "Server error", cause: Throwable? = null) : ApiException(message, cause)

    class UnknownException(message: String = "Unknown error occurred", cause: Throwable? = null) : ApiException(message, cause)

    class ApiErrorException(
        val apiError: ApiError,
        message: String = apiError.message
    ) : ApiException(message)
}

object ApiExceptionMapper {

    fun mapException(throwable: Throwable): ApiException {
        return when (throwable) {
            is ApiException -> throwable

            is ClientRequestException -> {
                when (throwable.response.status) {
                    HttpStatusCode.BadRequest -> ApiException.BadRequestException(cause = throwable)
                    HttpStatusCode.Unauthorized -> ApiException.UnauthorizedException(cause = throwable)
                    HttpStatusCode.Forbidden -> ApiException.ForbiddenException(cause = throwable)
                    HttpStatusCode.NotFound -> ApiException.NotFoundException(cause = throwable)
                    else -> ApiException.BadRequestException("Client error: ${throwable.response.status}", throwable)
                }
            }

            is ServerResponseException -> {
                ApiException.ServerException("Server error: ${throwable.response.status}", throwable)
            }

            is RedirectResponseException -> {
                ApiException.UnknownException("Redirect error: ${throwable.response.status}", throwable)
            }

            else -> {
                when {
                    throwable.message?.contains("timeout", ignoreCase = true) == true ->
                        ApiException.TimeoutException(cause = throwable)

                    throwable.message?.contains("network", ignoreCase = true) == true ||
                    throwable.message?.contains("connection", ignoreCase = true) == true ->
                        ApiException.NetworkException(cause = throwable)

                    else -> ApiException.UnknownException(throwable.message ?: "Unknown error", throwable)
                }
            }
        }
    }
}