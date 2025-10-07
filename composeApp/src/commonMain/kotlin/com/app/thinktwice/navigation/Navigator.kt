package com.app.thinktwice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Navigator provides navigation operations for the stack-based navigation system
 */
@Stable
class Navigator internal constructor(
    private val navigationState: NavigationState,
    private val coroutineScope: CoroutineScope
) {
    /**
     * Current screen
     */
    val currentScreen: Screen
        get() = navigationState.currentScreen.value

    /**
     * Back stack
     */
    val backStack: List<Screen>
        get() = navigationState.backStack.value

    /**
     * Whether navigation can go back
     */
    val canPopBack: Boolean
        get() = navigationState.canPopBack

    /**
     * Whether currently navigating
     */
    val isNavigating: Boolean
        get() = navigationState.isNavigating.value

    /**
     * Push a new screen onto the navigation stack
     */
    fun push(screen: Screen) {
        coroutineScope.launch {
            navigationState.push(screen)
        }
    }

    /**
     * Push a new screen and clear the back stack (useful for login flows)
     */
    fun pushAndClear(screen: Screen) {
        coroutineScope.launch {
            navigationState.push(screen, clearBackStack = true)
        }
    }

    /**
     * Pop the current screen from the navigation stack
     * @return true if pop was successful, false if already at root
     */
    fun pop(): Boolean {
        return if (canPopBack) {
            coroutineScope.launch {
                navigationState.pop()
            }
            true
        } else {
            false
        }
    }

    /**
     * Pop to a specific screen type in the stack
     * @param screen The screen type to pop to
     * @return true if the screen was found and popped to, false otherwise
     */
    fun popTo(screen: Screen): Boolean {
        return coroutineScope.run {
            launch {
                navigationState.popTo(screen)
            }
            true
        }
    }

    /**
     * Pop to the root screen (first screen in the stack)
     * @return true if popped to root, false if already at root
     */
    fun popToRoot(): Boolean {
        return if (backStack.size > 1) {
            coroutineScope.launch {
                navigationState.popToRoot()
            }
            true
        } else {
            false
        }
    }

    /**
     * Replace the current screen with a new one
     */
    fun replace(screen: Screen) {
        coroutineScope.launch {
            navigationState.replace(screen)
        }
    }

    /**
     * Clear the entire stack and push a new screen
     */
    fun clearAndPush(screen: Screen) {
        coroutineScope.launch {
            navigationState.clearAndPush(screen)
        }
    }

    /**
     * Handle back press - checks if current screen can handle it, otherwise pops
     * @return true if back press was handled, false otherwise
     */
    fun onBackPressed(): Boolean {
        val currentScreen = navigationState.currentScreen.value

        // Check if current screen can handle back press
        if (currentScreen is BackPressHandler && currentScreen.onBackPressed()) {
            return true
        }

        // Otherwise try to pop
        return pop()
    }

    /**
     * Get screen at specific position in back stack
     */
    fun getScreenAt(index: Int): Screen? {
        return backStack.getOrNull(index)
    }

    /**
     * Check if a specific screen type exists in the back stack
     */
    fun hasScreen(screenClass: kotlin.reflect.KClass<out Screen>): Boolean {
        return backStack.any { it::class == screenClass }
    }

    /**
     * Get the depth of the navigation stack
     */
    val stackDepth: Int
        get() = backStack.size

    /**
     * Get all screens of a specific type in the back stack
     */
    inline fun <reified T : Screen> getScreensOfType(): List<T> {
        return backStack.filterIsInstance<T>()
    }
}

/**
 * Creates and remembers a Navigator instance
 */
@Composable
fun rememberNavigator(navigationState: NavigationState): Navigator {
    val coroutineScope = rememberCoroutineScope()
    return remember(navigationState, coroutineScope) {
        Navigator(navigationState, coroutineScope)
    }
}