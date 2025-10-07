package com.app.thinktwice.applocking.models

/**
 * Represents a user's response to the post-app-close follow-up question
 */
data class FollowupResponse(
    val id: Long = 0,
    val restrictedAppId: Long,
    val sessionStartTime: Long, // UTC timestamp
    val sessionEndTime: Long, // UTC timestamp
    val sessionDurationSeconds: Long,
    val response: ResponseType,
    val createdAt: Long
)

/**
 * Possible responses to "Did you complete the intended action?"
 */
enum class ResponseType(val value: String) {
    YES("yes"),
    NO("no"),
    SKIP("skip");

    companion object {
        fun fromValue(value: String): ResponseType {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: SKIP
        }
    }
}
