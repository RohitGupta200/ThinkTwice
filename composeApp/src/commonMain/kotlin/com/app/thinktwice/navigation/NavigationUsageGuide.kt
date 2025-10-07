package com.app.thinktwice.navigation

/**
 * Navigation Usage Guide for ThinkTwice App
 *
 * This guide demonstrates how to use the stack-based navigation system.
 *
 * ## Core Concepts
 *
 * 1. **Screen**: All screens must implement the `Screen` interface
 * 2. **Navigator**: Provides navigation operations (push, pop, replace, etc.)
 * 3. **NavigationState**: Manages the navigation stack state
 * 4. **NavigationHost**: Composable that hosts and renders screens
 * 5. **NavigationTransition**: Animation types for screen transitions
 *
 * ## Basic Usage
 *
 * ```kotlin
 * // 1. Define your screens
 * @Serializable
 * data object HomeScreen : SimpleScreen(), RootScreen
 *
 * @Serializable
 * data class DetailScreen(
 *     override val parameters: DetailParams
 * ) : ParameterizedScreen<DetailParams>()
 *
 * @Serializable
 * data class DetailParams(val id: Long, val title: String)
 *
 * // 2. Set up NavigationHost
 * @Composable
 * fun App() {
 *     NavigationHost(
 *         initialScreen = HomeScreen,
 *         transitionType = NavigationTransition.Slide
 *     ) { screen ->
 *         when (screen) {
 *             is HomeScreen -> HomeScreenContent()
 *             is DetailScreen -> DetailScreenContent(screen.parameters)
 *         }
 *     }
 * }
 *
 * // 3. Navigate from within composables
 * @Composable
 * fun NavigationScope.HomeScreenContent() {
 *     Button(onClick = {
 *         navigateTo(DetailScreen(DetailParams(1, "Sample")))
 *     }) {
 *         Text("Go to Details")
 *     }
 * }
 * ```
 *
 * ## Navigation Operations
 *
 * ### Basic Navigation
 * - `navigateTo(screen)`: Push a new screen onto the stack
 * - `navigateBack()`: Pop the current screen (returns Boolean for success)
 * - `replaceWith(screen)`: Replace current screen with new one
 *
 * ### Advanced Navigation
 * - `navigateBackTo(screen)`: Pop to a specific screen in the stack
 * - `navigateBackToRoot()`: Pop to the first screen in the stack
 * - `clearAndNavigateTo(screen)`: Clear stack and push new screen
 *
 * ### Navigation Properties
 * - `currentScreen`: Get the current screen
 * - `canNavigateBack`: Check if back navigation is possible
 * - `navigator.stackDepth`: Get current stack depth
 * - `navigator.backStack`: Get the full navigation stack
 *
 * ## Screen Types
 *
 * ### Simple Screens
 * ```kotlin
 * @Serializable
 * data object SettingsScreen : SimpleScreen()
 * ```
 *
 * ### Parameterized Screens
 * ```kotlin
 * @Serializable
 * data class UserScreen(
 *     override val parameters: UserParams
 * ) : ParameterizedScreen<UserParams>()
 *
 * @Serializable
 * data class UserParams(val userId: Long, val username: String)
 * ```
 *
 * ### Root Screens
 * ```kotlin
 * @Serializable
 * data object LoginScreen : SimpleScreen(), RootScreen
 * // Root screens clear the back stack when navigated to
 * ```
 *
 * ### Back Press Handling
 * ```kotlin
 * @Serializable
 * data object CustomScreen : SimpleScreen(), BackPressHandler {
 *     override fun onBackPressed(): Boolean {
 *         // Custom back press logic
 *         return true // Return true if handled, false for default behavior
 *     }
 * }
 * ```
 *
 * ## Animation Types
 *
 * - `NavigationTransition.Slide`: Horizontal slide (iOS-style)
 * - `NavigationTransition.Fade`: Fade in/out
 * - `NavigationTransition.ScaleFade`: Scale with fade (Material Design-style)
 * - `NavigationTransition.Modal`: Vertical slide up/down
 * - `NavigationTransition.None`: No animation
 *
 * ## Advanced Features
 *
 * ### Custom Transitions
 * ```kotlin
 * NavigationHost(
 *     initialScreen = HomeScreen,
 *     transitionType = NavigationTransition.ScaleFade
 * ) { screen ->
 *     // Screen content
 * }
 * ```
 *
 * ### State Management
 * ```kotlin
 * val navigationState = rememberNavigationState(HomeScreen)
 * val navigator = rememberNavigator(navigationState)
 *
 * // Access navigation state
 * val currentScreen by navigationState.currentScreenState
 * val backStack by navigationState.backStackState
 * val isNavigating by navigationState.isNavigatingState
 * ```
 *
 * ### Screen Keys and Caching
 * - Each screen has a unique `key` property for state preservation
 * - Simple screens use their class name as the key
 * - Parameterized screens include parameter hash in the key
 *
 * ## Best Practices
 *
 * 1. **Always use @Serializable**: Required for screen state persistence
 * 2. **Keep parameters simple**: Use primitive types and data classes
 * 3. **Handle back press gracefully**: Check `canNavigateBack` before calling `navigateBack()`
 * 4. **Use RootScreen for authentication flows**: Login, onboarding screens
 * 5. **Leverage NavigationScope**: Access navigation methods cleanly
 * 6. **Consider animation types**: Match your app's design language
 *
 * ## Error Handling
 *
 * ```kotlin
 * // Safe back navigation
 * if (canNavigateBack) {
 *     navigateBack()
 * } else {
 *     // Handle case where there's nowhere to go back to
 * }
 *
 * // Check if screen exists in stack
 * if (navigator.hasScreen(HomeScreen::class)) {
 *     navigateBackTo(HomeScreen)
 * }
 * ```
 *
 * ## Testing Navigation
 *
 * ```kotlin
 * @Test
 * fun testNavigation() {
 *     val navigationState = NavigationState(HomeScreen)
 *     val navigator = Navigator(navigationState, testCoroutineScope)
 *
 *     navigator.push(SettingsScreen)
 *     assertEquals(2, navigator.stackDepth)
 *     assertEquals(SettingsScreen, navigator.currentScreen)
 *
 *     navigator.pop()
 *     assertEquals(HomeScreen, navigator.currentScreen)
 * }
 * ```
 */

// Example implementation demonstrating all concepts
object NavigationExamples {

    // Example screens
    @kotlinx.serialization.Serializable
    data object ExampleHomeScreen : SimpleScreen(), RootScreen

    @kotlinx.serialization.Serializable
    data class ExampleDetailScreen(
        override val parameters: ExampleDetailParams
    ) : ParameterizedScreen<ExampleDetailParams>(), BackPressHandler {

        override fun onBackPressed(): Boolean {
            // Custom back press handling
            println("Detail screen handling back press")
            return false // Let navigation system handle it
        }
    }

    @kotlinx.serialization.Serializable
    data class ExampleDetailParams(
        val id: Long,
        val title: String,
        val description: String = ""
    )

    // Example usage in a composable
    /*
    @Composable
    fun NavigationScope.ExampleUsage() {
        Column {
            Text("Current screen: ${currentScreen.screenId}")
            Text("Stack depth: ${navigator.stackDepth}")
            Text("Can go back: $canNavigateBack")

            Button(onClick = {
                navigateTo(ExampleDetailScreen(
                    ExampleDetailParams(1, "Example", "This is an example")
                ))
            }) {
                Text("Go to Detail")
            }

            Button(
                onClick = { navigateBack() },
                enabled = canNavigateBack
            ) {
                Text("Go Back")
            }

            Button(onClick = { navigateBackToRoot() }) {
                Text("Go to Root")
            }
        }
    }
    */
}