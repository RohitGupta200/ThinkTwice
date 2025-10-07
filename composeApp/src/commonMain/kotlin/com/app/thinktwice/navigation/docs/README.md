# ThinkTwice Navigation System - Complete Documentation

## 📚 Documentation Overview

This is the complete documentation suite for the ThinkTwice Navigation System - a stack-based navigation architecture for Kotlin Multiplatform Compose applications.

## 📋 Table of Contents

### Core Documentation

1. **[Navigation Overview](NavigationOverview.md)**
   - System architecture and core principles
   - Component overview and relationships
   - Getting started guide
   - Quick reference

2. **[Screen Interface](ScreenInterface.md)**
   - Screen types and definitions
   - Parameter design patterns
   - State preservation mechanics
   - Serialization and validation

3. **[Navigator API](NavigatorAPI.md)**
   - Complete API reference
   - Navigation operations
   - Stack management methods
   - Error handling patterns

4. **[NavigationHost](NavigationHost.md)**
   - NavigationHost composable usage
   - NavigationScope functionality
   - Performance optimization
   - Integration patterns

5. **[Animation System](AnimationSystem.md)**
   - Built-in transition types
   - Animation configuration
   - Performance considerations
   - Custom animation patterns

### Advanced Guides

6. **[Usage Examples](UsageExamples.md)**
   - Common navigation patterns
   - Screen design examples
   - State management patterns
   - Best practices and anti-patterns

7. **[State Management](StateManagement.md)**
   - Navigation state lifecycle
   - Memory management
   - Configuration change handling
   - Process death recovery

8. **[Migration Guide](MigrationGuide.md)**
   - Integration into existing projects
   - Migration from other libraries
   - Gradual migration strategies
   - Compatibility considerations

## 🚀 Quick Start

### 1. Basic Setup
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
                is SettingsScreen -> SettingsContent()
            }
        }
    }
}
```

### 2. Define Screens
```kotlin
@Serializable
data object HomeScreen : SimpleScreen(), RootScreen

@Serializable
data class DetailScreen(
    override val parameters: DetailParams
) : ParameterizedScreen<DetailParams>()

@Serializable
data class DetailParams(val id: Long, val title: String)
```

### 3. Navigate Between Screens
```kotlin
@Composable
fun NavigationScope.HomeContent() {
    Button(onClick = { navigateTo(DetailScreen(DetailParams(1, "Sample"))) }) {
        Text("Go to Detail")
    }

    if (canNavigateBack) {
        Button(onClick = { navigateBack() }) {
            Text("Go Back")
        }
    }
}
```

## 🏗️ Architecture Summary

### Core Components

```
┌─────────────────────────────────────────┐
│           NavigationHost                │
│  ┌─────────────────────────────────────┐ │
│  │        NavigationScope              │ │
│  │  ┌─────────────────────────────────┐ │ │
│  │  │        Screen Content          │ │ │
│  │  └─────────────────────────────────┘ │ │
│  └─────────────────────────────────────┘ │
└─────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────┐
│           Navigator                     │
│  • push(screen)                        │
│  • pop()                               │
│  • popTo(screen)                       │
│  • replace(screen)                     │
└─────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────┐
│         NavigationState                 │
│  • currentScreen: StateFlow<Screen>     │
│  • backStack: StateFlow<List<Screen>>   │
│  • isNavigating: StateFlow<Boolean>     │
└─────────────────────────────────────────┘
```

### Screen Hierarchy

```
Screen (interface)
├── SimpleScreen (abstract class)
│   ├── HomeScreen
│   ├── SettingsScreen
│   └── AboutScreen
└── ParameterizedScreen<T> (abstract class)
    ├── DetailScreen(DetailParams)
    ├── EditScreen(EditParams)
    └── UserScreen(UserParams)

Marker Interfaces:
• RootScreen - Clears navigation stack
• BackPressHandler - Custom back press handling
```

## 🎯 Key Features

### ✅ Stack-Based Navigation
- Push/pop operations like iOS navigation
- Full stack management and inspection
- Root screen support for authentication flows

### ✅ Type-Safe Parameters
- Compile-time parameter validation
- Kotlinx.serialization integration
- Automatic parameter marshalling

### ✅ Reactive State Management
- StateFlow integration
- Automatic UI updates
- State preservation across navigation

### ✅ Animation System
- 5 built-in transition types
- Direction-aware animations
- Performance optimized transitions

### ✅ Cross-Platform Support
- Works on Android and iOS
- Shared navigation logic
- Platform-specific optimizations

### ✅ Testing Support
- Navigation testing utilities
- State inspection methods
- Integration test patterns

## 📊 Performance Characteristics

| Aspect | Performance |
|--------|------------|
| Stack Operations | O(1) push/pop, O(n) popTo |
| Memory Usage | Minimal overhead, automatic cleanup |
| Animation Performance | 60fps on most devices |
| Startup Time | Negligible impact |
| State Preservation | Automatic with minimal cost |

## 🔧 Configuration Options

### Animation Types
```kotlin
NavigationTransition.Slide      // iOS-style horizontal slide
NavigationTransition.Fade       // Cross-fade transitions
NavigationTransition.ScaleFade  // Material Design scale+fade
NavigationTransition.Modal      // Vertical slide (modals)
NavigationTransition.None       // No animation
```

### Transition Durations
```kotlin
NavigationTransitions.DEFAULT_ANIMATION_DURATION = 300  // Standard
NavigationTransitions.FAST_ANIMATION_DURATION = 150     // Quick
NavigationTransitions.SLOW_ANIMATION_DURATION = 500     // Emphasized
```

## 🧪 Testing

### Unit Testing
```kotlin
@Test
fun testNavigation() {
    val navigator = createTestNavigator()
    navigator.push(DetailScreen(DetailParams(1, "Test")))

    assertEquals(DetailScreen::class, navigator.currentScreen::class)
    assertEquals(2, navigator.stackDepth)
    assertTrue(navigator.canPopBack)
}
```

### UI Testing
```kotlin
@Test
fun testNavigationFlow() {
    composeTestRule.setContent {
        NavigationHost(initialScreen = HomeScreen) { screen ->
            ScreenContent(screen)
        }
    }

    composeTestRule.onNodeWithText("Navigate").performClick()
    composeTestRule.onNodeWithText("Detail Screen").assertIsDisplayed()
}
```

## 🤝 Contributing

### Code Style
- Follow Kotlin coding conventions
- Use meaningful names for screens and parameters
- Document public APIs
- Write tests for navigation logic

### Adding New Features
1. Update core components
2. Add comprehensive documentation
3. Include usage examples
4. Write unit and integration tests
5. Update migration guide if needed

## 📄 License

This navigation system is part of the ThinkTwice project and follows the same licensing terms.

## 🆘 Support and Troubleshooting

### Common Issues

1. **Compilation Errors**: Check screen serialization annotations
2. **Animation Glitches**: Ensure proper screen background colors
3. **Memory Issues**: Monitor navigation stack depth
4. **State Loss**: Use `rememberSaveable` for important state

### Getting Help

1. Check the relevant documentation section
2. Review usage examples for similar patterns
3. Test with the provided example screens
4. Check migration guide for compatibility issues

### Reporting Issues

When reporting issues, include:
- Navigation system version
- Platform (Android/iOS/Desktop)
- Minimal reproduction case
- Expected vs. actual behavior
- Console logs if applicable

---

**📚 Start exploring with the [Navigation Overview](NavigationOverview.md) or jump to specific topics using the links above!**