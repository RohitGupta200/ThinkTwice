package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    val detail: String,
    val error_code: String? = null,
    val timestamp: String? = null
)

/**
 * Standard error codes from the API
 */
object ErrorCodes {
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val REFERRAL_CODE_EXHAUSTED = "REFERRAL_CODE_EXHAUSTED"
    const val INVALID_TOKEN = "INVALID_TOKEN"
    const val RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED"
    const val AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED"
    const val RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"
    const val INSUFFICIENT_PERMISSIONS = "INSUFFICIENT_PERMISSIONS"
}
