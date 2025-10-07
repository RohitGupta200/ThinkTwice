package com.app.thinktwice.navigation

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents the navigation state of the application
 */
@Stable
class NavigationState internal constructor(
    initialScreen: Screen
) {
    private val _backStack = MutableStateFlow(listOf(initialScreen))
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()

    private val _currentScreen = MutableStateFlow(initialScreen)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()

    /**
     * Current screen as a composable state
     */
    val currentScreenState: State<Screen>
        @Composable get() = currentScreen.collectAsState()

    /**
     * Back stack as a composable state
     */
    val backStackState: State<List<Screen>>
        @Composable get() = backStack.collectAsState()

    /**
     * Navigation state as a composable state
     */
    val isNavigatingState: State<Boolean>
        @Composable get() = isNavigating.collectAsState()

    /**
     * Can pop back
     */
    val canPopBack: Boolean
        get() = _backStack.value.size > 1


    internal fun push(screen: Screen, clearBackStack: Boolean = false) {
        _isNavigating.value = true

        val newStack = if (clearBackStack || screen is RootScreen) {
            listOf(screen)
        } else {
            _backStack.value + screen
        }

        _backStack.value = newStack
        _currentScreen.value = screen
        _isNavigating.value = false
    }

    internal fun pop(): Boolean {
        if (!canPopBack) return false

        _isNavigating.value = true

        val newStack = _backStack.value.dropLast(1)
        _backStack.value = newStack
        _currentScreen.value = newStack.last()
        _isNavigating.value = false

        return true
    }

    internal fun popTo(screen: Screen): Boolean {
        val index = _backStack.value.indexOfLast { it::class == screen::class }
        if (index == -1) return false

        _isNavigating.value = true

        val newStack = _backStack.value.take(index + 1)
        _backStack.value = newStack
        _currentScreen.value = newStack.last()
        _isNavigating.value = false

        return true
    }

    internal fun popToRoot(): Boolean {
        if (_backStack.value.size <= 1) return false

        _isNavigating.value = true

        val newStack = listOf(_backStack.value.first())
        _backStack.value = newStack
        _currentScreen.value = newStack.last()
        _isNavigating.value = false

        return true
    }

    internal fun replace(screen: Screen) {
        _isNavigating.value = true

        val newStack = _backStack.value.dropLast(1) + screen
        _backStack.value = newStack
        _currentScreen.value = screen
        _isNavigating.value = false
    }

    internal fun clearAndPush(screen: Screen) {
        _isNavigating.value = true

        _backStack.value = listOf(screen)
        _currentScreen.value = screen
        _isNavigating.value = false
    }
}

/**
 * Creates and remembers a NavigationState
 */
@Composable
fun rememberNavigationState(initialScreen: Screen): NavigationState {
    return remember(initialScreen) {
        NavigationState(initialScreen)
    }
}