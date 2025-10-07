package com.app.thinktwice.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * NavigationHost manages the display of screens and handles navigation transitions
 */
@Composable
fun NavigationHost(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    transitionType: NavigationTransition = NavigationTransition.Slide,
    content: @Composable NavigationScope.(Screen) -> Unit
) {
    val currentScreen by navigationState.currentScreenState
    val isNavigating by navigationState.isNavigatingState

    // Track navigation direction for proper animations
    var isPopping by remember { mutableStateOf(false) }
    var lastStackSize by remember { mutableStateOf(navigationState.backStack.value.size) }

    // Update navigation direction based on stack size changes
    LaunchedEffect(navigationState.backStack.value.size) {
        val currentStackSize = navigationState.backStack.value.size
        isPopping = currentStackSize < lastStackSize
        lastStackSize = currentStackSize
    }

    val navigationScope = remember(navigator) {
        NavigationScope(navigator)
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                createTransitionSpec(transitionType, isPopping)
            },
            modifier = Modifier.fillMaxSize(),
            contentKey = { screen -> screen.key }
        ) { screen ->
            content(navigationScope, screen)
        }
    }
}

/**
 * Creates transition specification based on transition type and direction
 */
private fun AnimatedContentTransitionScope<Screen>.createTransitionSpec(
    transitionType: NavigationTransition,
    isPopping: Boolean
): ContentTransform {
    val enterTransition = transitionType.getEnterTransition(isPopping)
    val exitTransition = transitionType.getExitTransition(isPopping)

    return enterTransition togetherWith exitTransition
}

/**
 * Scope for navigation-aware composables
 */
@Stable
class NavigationScope internal constructor(
    val navigator: Navigator
) {
    /**
     * Current screen
     */
    val currentScreen: Screen
        get() = navigator.currentScreen

    /**
     * Whether we can navigate back
     */
    val canNavigateBack: Boolean
        get() = navigator.canPopBack

    /**
     * Navigate to a new screen
     */
    fun navigateTo(screen: Screen) {
        navigator.push(screen)
    }

    /**
     * Navigate back
     */
    fun navigateBack(): Boolean {
        return navigator.pop()
    }

    /**
     * Navigate back to a specific screen
     */
    fun navigateBackTo(screen: Screen): Boolean {
        return navigator.popTo(screen)
    }

    /**
     * Navigate back to root
     */
    fun navigateBackToRoot(): Boolean {
        return navigator.popToRoot()
    }

    /**
     * Replace current screen
     */
    fun replaceWith(screen: Screen) {
        navigator.replace(screen)
    }

    /**
     * Clear navigation stack and navigate to new screen
     */
    fun clearAndNavigateTo(screen: Screen) {
        navigator.clearAndPush(screen)
    }
}

/**
 * Provides the current NavigationScope
 */
val LocalNavigationScope = compositionLocalOf<NavigationScope?> { null }

/**
 * Gets the current NavigationScope or throws if not available
 */
@Composable
fun requireNavigationScope(): NavigationScope {
    return LocalNavigationScope.current
        ?: error("NavigationScope not found. Make sure you're using NavigationHost.")
}

/**
 * Optional NavigationScope access
 */
@Composable
fun navigationScope(): NavigationScope? {
    return LocalNavigationScope.current
}

/**
 * NavigationHost with automatic NavigationScope provision
 */
@Composable
fun NavigationHost(
    initialScreen: Screen,
    modifier: Modifier = Modifier,
    transitionType: NavigationTransition = NavigationTransition.Slide,
    content: @Composable NavigationScope.(Screen) -> Unit
) {
    val navigationState = rememberNavigationState(initialScreen)
    val navigator = rememberNavigator(navigationState)

    CompositionLocalProvider(
        LocalNavigationScope provides NavigationScope(navigator)
    ) {
        NavigationHost(
            navigationState = navigationState,
            navigator = navigator,
            modifier = modifier,
            transitionType = transitionType,
            content = content
        )
    }
}