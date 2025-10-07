package com.app.thinktwice.applocking.models

/**
 * Represents an app that the user has chosen to restrict/monitor
 */
data class RestrictedApp(
    val id: Long = 0,
    val appId: String,
    val appName: String,
    val packageName: String, // Android package name or iOS bundle identifier
    val iconPath: String? = null,
    val isEnabled: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
