# ThinkTwice Navigation System Documentation

## Overview

The ThinkTwice Navigation System is a comprehensive, stack-based navigation architecture designed for Kotlin Multiplatform Compose applications. It provides type-safe navigation, animated transitions, and reactive state management across Android and iOS platforms.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Core Components](#core-components)
3. [Getting Started](#getting-started)
4. [Navigation Operations](#navigation-operations)
5. [Animation System](#animation-system)
6. [Best Practices](#best-practices)
7. [API Reference](#api-reference)

## Architecture Overview

The navigation system follows a stack-based architecture similar to iOS UINavigationController, providing:

- **Stack Management**: Push and pop screens like a traditional navigation stack
- **Type Safety**: Compile-time parameter validation using Kotlin serialization
- **Reactive State**: Integration with Compose state system for automatic UI updates
- **Cross-Platform**: Single API that works consistently on Android and iOS
- **Animation Support**: Built-in transitions with customization options

### Core Principles

1. **Declarative**: Navigation state is declared and UI automatically updates
2. **Type-Safe**: Parameters are validated at compile-time
3. **Predictable**: Stack operations follow standard navigation patterns
4. **Extensible**: Easy to add new screens and customize behavior
5. **Testable**: Navigation logic is separated and easily unit testable

## Core Components

### 1. Screen Interface
```kotlin
@Serializable
sealed interface Screen {
    val screenId: String
    val key: String
}
```
Base interface for all navigation destinations.

### 2. Navigator
```kotlin
@Stable
class Navigator {
    fun push(screen: Screen)
    fun pop(): Boolean
    fun popTo(screen: Screen): Boolean
    fun replace(screen: Screen)
    // ... more operations
}
```
Provides navigation operations and stack management.

### 3. NavigationState
```kotlin
@Stable
class NavigationState {
    val currentScreen: StateFlow<Screen>
    val backStack: StateFlow<List<Screen>>
    val isNavigating: StateFlow<Boolean>
    val canPopBack: Boolean
}
```
Manages navigation state with reactive updates.

### 4. NavigationHost
```kotlin
@Composable
fun NavigationHost(
    initialScreen: Screen,
    transitionType: NavigationTransition = NavigationTransition.Slide,
    content: @Composable NavigationScope.(Screen) -> Unit
)
```
Composable that hosts screens and handles transitions.

### 5. NavigationScope
```kotlin
@Stable
class NavigationScope(val navigator: Navigator) {
    fun navigateTo(screen: Screen)
    fun navigateBack(): Boolean
    fun navigateBackTo(screen: Screen): Boolean
    // ... convenience methods
}
```
Provides navigation methods within composables.

## Getting Started

### 1. Define Your Screens

```kotlin
// Simple screens
@Serializable
data object HomeScreen : SimpleScreen(), RootScreen

@Serializable
data object SettingsScreen : SimpleScreen()

// Parameterized screens
@Serializable
data class DetailScreen(
    override val parameters: DetailParams
) : ParameterizedScreen<DetailParams>()

@Serializable
data class DetailParams(
    val id: Long,
    val title: String
)
```

### 2. Set Up NavigationHost

```kotlin
@Composable
fun App() {
    MaterialTheme {
        NavigationHost(
            initialScreen = HomeScreen,
            transitionType = NavigationTransition.Slide
        ) { screen ->
            when (screen) {
                is HomeScreen -> HomeContent()
                is DetailScreen -> DetailContent(screen.parameters)
                is SettingsScreen -> SettingsContent()
            }
        }
    }
}
```

### 3. Navigate Between Screens

```kotlin
@Composable
fun NavigationScope.HomeContent() {
    Button(
        onClick = {
            navigateTo(DetailScreen(DetailParams(1, "Sample")))
        }
    ) {
        Text("Go to Detail")
    }

    if (canNavigateBack) {
        Button(onClick = { navigateBack() }) {
            Text("Go Back")
        }
    }
}
```

## Navigation Operations

### Basic Operations

| Operation | Description | Example |
|-----------|-------------|---------|
| `push(screen)` | Push new screen onto stack | `navigator.push(DetailScreen)` |
| `pop()` | Remove current screen | `navigator.pop()` |
| `replace(screen)` | Replace current screen | `navigator.replace(NewScreen)` |

### Advanced Operations

| Operation | Description | Use Case |
|-----------|-------------|----------|
| `popTo(screen)` | Pop to specific screen | Return to specific point in flow |
| `popToRoot()` | Pop to first screen | Reset to app home |
| `clearAndPush(screen)` | Clear stack and push | Authentication flows |
| `pushAndClear(screen)` | Push and clear stack | Alternative syntax |

### Stack Inspection

| Property | Type | Description |
|----------|------|-------------|
| `currentScreen` | `Screen` | Currently visible screen |
| `backStack` | `List<Screen>` | Complete navigation stack |
| `stackDepth` | `Int` | Number of screens in stack |
| `canPopBack` | `Boolean` | Whether back navigation is possible |

## Animation System

### Built-in Transitions

| Type | Description | Use Case |
|------|-------------|----------|
| `Slide` | Horizontal slide (iOS-style) | Standard navigation |
| `Fade` | Fade in/out | Simple transitions |
| `ScaleFade` | Scale + fade (Material) | Emphasized transitions |
| `Modal` | Vertical slide | Modal presentations |
| `None` | No animation | Instant transitions |

### Custom Animation Example

```kotlin
NavigationHost(
    transitionType = NavigationTransition.ScaleFade
) { screen ->
    // Screen content
}
```

## Best Practices

### 1. Screen Design

✅ **Do:**
- Keep parameters simple and serializable
- Use data classes for parameters
- Implement `RootScreen` for entry points
- Handle back press in complex screens

❌ **Don't:**
- Pass complex objects as parameters
- Forget `@Serializable` annotation
- Create circular navigation flows

### 2. Navigation Logic

✅ **Do:**
- Check `canNavigateBack` before popping
- Use `popTo()` for multi-step flows
- Clear stack for authentication changes
- Handle navigation errors gracefully

❌ **Don't:**
- Assume navigation will always succeed
- Create deep navigation stacks
- Navigate during composition

### 3. State Management

✅ **Do:**
- Use reactive state flows
- Preserve important state in ViewModels
- Handle configuration changes
- Test navigation flows

❌ **Don't:**
- Store navigation state in UI layer
- Forget to handle process death
- Ignore navigation state changes

## API Reference

### Core Interfaces

- [Screen Interface Documentation](ScreenInterface.md)
- [Navigator API Reference](NavigatorAPI.md)
- [NavigationState Reference](NavigationState.md)
- [NavigationHost Documentation](NavigationHost.md)

### Components

- [Animation System](AnimationSystem.md)
- [Navigation Scope](NavigationScope.md)
- [State Management](StateManagement.md)

### Guides

- [Migration Guide](MigrationGuide.md)
- [Testing Guide](TestingGuide.md)
- [Advanced Usage](AdvancedUsage.md)

## Examples

Complete examples are available in:
- [Basic Navigation Example](../examples/BasicNavigationExample.kt)
- [Advanced Navigation Example](../examples/AdvancedNavigationExample.kt)
- [Custom Animations Example](../examples/CustomAnimationsExample.kt)
- [Testing Examples](../examples/NavigationTestingExample.kt)

## Support

For issues, questions, or contributions:
- Check existing documentation
- Review example implementations
- Test with provided examples
- Follow best practices outlined above