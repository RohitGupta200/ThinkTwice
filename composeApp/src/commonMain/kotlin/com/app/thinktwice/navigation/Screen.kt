package com.app.thinktwice.navigation

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

/**
 * Base interface for all screens in the navigation system
 */
@Serializable
sealed interface Screen {
    /**
     * Unique identifier for the screen type
     */
    val screenId: String get() = this::class.simpleName ?: "Unknown"

    /**
     * Optional key for maintaining state across configuration changes
     */
    val key: String get() = screenId
}

/**
 * Marker interface for screens that should clear the back stack when navigated to
 */
interface RootScreen

/**
 * Marker interface for screens that can handle back press
 */
interface BackPressHandler {
    /**
     * Handle back press. Return true if handled, false to continue with default behavior
     */
    fun onBackPressed(): Boolean
}

/**
 * Screen with parameters
 */
@Serializable
abstract class ParameterizedScreen<T : Any> : Screen {
    abstract val parameters: T

    override val key: String
        get() = "${screenId}_${parameters.hashCode()}"
}

/**
 * Simple screen without parameters
 */
@Serializable
abstract class SimpleScreen : Screen