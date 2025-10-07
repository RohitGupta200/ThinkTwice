package com.app.thinktwice.applocking.models

/**
 * Represents the state of a snooze timer for a restricted app
 */
data class SnoozeState(
    val id: Long = 0,
    val restrictedAppId: Long,
    val snoozeExpiryTimestamp: Long, // UTC timestamp in milliseconds
    val snoozeDurationMinutes: Int,
    val isActive: Boolean = true,
    val createdAt: Long
) {
    /**
     * Check if snooze is currently active (not expired)
     */
    fun isCurrentlyActive(currentTimeMillis: Long): Boolean {
        return isActive && currentTimeMillis < snoozeExpiryTimestamp
    }

    /**
     * Check if snooze has expired
     */
    fun hasExpired(currentTimeMillis: Long): Boolean {
        return currentTimeMillis >= snoozeExpiryTimestamp
    }

    /**
     * Get remaining time in milliseconds
     */
    fun getRemainingTimeMillis(currentTimeMillis: Long): Long {
        return if (isCurrentlyActive(currentTimeMillis)) {
            snoozeExpiryTimestamp - currentTimeMillis
        } else {
            0L
        }
    }

    /**
     * Get remaining time in minutes
     */
    fun getRemainingTimeMinutes(currentTimeMillis: Long): Int {
        return (getRemainingTimeMillis(currentTimeMillis) / 60000).toInt()
    }
}

/**
 * Enum representing standard snooze durations
 */
enum class SnoozeDuration(val minutes: Int, val label: String) {
    FIVE_MINUTES(5, "5 minutes"),
    TEN_MINUTES(10, "10 minutes"),
    FIFTEEN_MINUTES(15, "15 minutes"),
    THIRTY_MINUTES(30, "30 minutes"),
    ONE_HOUR(60, "1 hour");

    fun toMillis(): Long = minutes * 60 * 1000L
}
