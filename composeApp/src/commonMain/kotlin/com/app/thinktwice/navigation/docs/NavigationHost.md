# NavigationHost Documentation

## Overview

`NavigationHost` is the primary composable for hosting and displaying screens in the ThinkTwice Navigation System. It manages screen transitions, animations, and provides the navigation context to child composables.

## Function Signatures

### Primary NavigationHost

```kotlin
@Composable
fun NavigationHost(
    initialScreen: Screen,
    modifier: Modifier = Modifier,
    transitionType: NavigationTransition = NavigationTransition.Slide,
    content: @Composable NavigationScope.(Screen) -> Unit
)
```

### Advanced NavigationHost

```kotlin
@Composable
fun NavigationHost(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    transitionType: NavigationTransition = NavigationTransition.Slide,
    content: @Composable NavigationScope.(Screen) -> Unit
)
```

## Parameters

### Primary NavigationHost Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `initialScreen` | `Screen` | ✅ | - | The first screen to display |
| `modifier` | `Modifier` | ❌ | `Modifier` | Modifier for the navigation container |
| `transitionType` | `NavigationTransition` | ❌ | `Slide` | Animation type for screen transitions |
| `content` | `@Composable NavigationScope.(Screen) -> Unit` | ✅ | - | Screen content composer |

### Advanced Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `navigationState` | `NavigationState` | ✅ | Pre-created navigation state |
| `navigator` | `Navigator` | ✅ | Pre-created navigator instance |

## Basic Usage

### Simple Setup

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
                is AboutScreen -> AboutContent()
            }
        }
    }
}
```

### With Custom Modifier

```kotlin
@Composable
fun NavigationExample() {
    NavigationHost(
        initialScreen = HomeScreen,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        transitionType = NavigationTransition.Fade
    ) { screen ->
        // Screen content
        ScreenContent(screen)
    }
}
```

## NavigationScope

The `content` lambda receives a `NavigationScope` instance that provides navigation methods and state access.

### NavigationScope Properties

```kotlin
@Stable
class NavigationScope(val navigator: Navigator) {
    val currentScreen: Screen
    val canNavigateBack: Boolean
}
```

### NavigationScope Methods

```kotlin
fun navigateTo(screen: Screen)
fun navigateBack(): Boolean
fun navigateBackTo(screen: Screen): Boolean
fun navigateBackToRoot(): Boolean
fun replaceWith(screen: Screen)
fun clearAndNavigateTo(screen: Screen)
```

### Using NavigationScope

```kotlin
NavigationHost(
    initialScreen = HomeScreen
) { screen ->
    when (screen) {
        is HomeScreen -> {
            HomeContent(
                onNavigateToSettings = { navigateTo(SettingsScreen) },
                onNavigateToProfile = { navigateTo(ProfileScreen) }
            )
        }
        is SettingsScreen -> {
            SettingsContent(
                onBackClick = { navigateBack() },
                onNavigateToAbout = { navigateTo(AboutScreen) }
            )
        }
        is ProfileScreen -> {
            ProfileContent(
                canGoBack = canNavigateBack,
                onBackClick = { navigateBack() }
            )
        }
    }
}
```

## Screen Content Organization

### Recommended Pattern

```kotlin
@Composable
fun NavigationScope.HomeContent() {
    // Direct access to navigation methods
    Column {
        Button(onClick = { navigateTo(SettingsScreen) }) {
            Text("Settings")
        }

        Button(onClick = { navigateTo(AboutScreen) }) {
            Text("About")
        }
    }
}

@Composable
fun NavigationScope.SettingsContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = { navigateBack() }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        // Settings content
    }
}
```

### Parameterized Screen Content

```kotlin
NavigationHost(
    initialScreen = HomeScreen
) { screen ->
    when (screen) {
        is UserDetailScreen -> {
            UserDetailContent(
                userId = screen.parameters.userId,
                username = screen.parameters.username,
                onEditClick = {
                    navigateTo(
                        EditUserScreen(
                            EditUserParams(
                                userId = screen.parameters.userId,
                                currentName = screen.parameters.username
                            )
                        )
                    )
                },
                onBackClick = { navigateBack() }
            )
        }
    }
}
```

## Advanced Navigation State Management

### Custom State Creation

```kotlin
@Composable
fun AdvancedNavigationExample() {
    val navigationState = rememberNavigationState(
        initialScreen = if (isUserLoggedIn()) HomeScreen else LoginScreen
    )
    val navigator = rememberNavigator(navigationState)

    // Access navigation state directly
    val currentScreen by navigationState.currentScreenState
    val backStack by navigationState.backStackState
    val isNavigating by navigationState.isNavigatingState

    NavigationHost(
        navigationState = navigationState,
        navigator = navigator
    ) { screen ->
        // Show loading overlay during navigation
        Box {
            ScreenContent(screen)

            if (isNavigating) {
                LoadingOverlay()
            }
        }
    }
}
```

### State Observation

```kotlin
@Composable
fun NavigationWithSideEffects() {
    val navigationState = rememberNavigationState(HomeScreen)
    val navigator = rememberNavigator(navigationState)

    // React to navigation changes
    LaunchedEffect(navigationState.currentScreen) {
        analytics.trackScreenView(navigationState.currentScreen.screenId)
    }

    LaunchedEffect(navigationState.backStack) {
        logger.debug("Navigation stack: ${navigationState.backStack.size} screens")
    }

    NavigationHost(
        navigationState = navigationState,
        navigator = navigator
    ) { screen ->
        ScreenContent(screen)
    }
}
```

## Transition Types

### Available Transitions

```kotlin
enum class NavigationTransition {
    Slide,      // Horizontal slide (iOS-style)
    Fade,       // Fade in/out
    ScaleFade,  // Scale + fade (Material Design)
    Modal,      // Vertical slide (for modals)
    None        // No animation
}
```

### Transition Examples

```kotlin
// iOS-style horizontal slide
NavigationHost(
    initialScreen = HomeScreen,
    transitionType = NavigationTransition.Slide
) { screen -> /* content */ }

// Material Design scale + fade
NavigationHost(
    initialScreen = HomeScreen,
    transitionType = NavigationTransition.ScaleFade
) { screen -> /* content */ }

// Modal presentation
NavigationHost(
    initialScreen = HomeScreen,
    transitionType = NavigationTransition.Modal
) { screen -> /* content */ }

// Instant transitions
NavigationHost(
    initialScreen = HomeScreen,
    transitionType = NavigationTransition.None
) { screen -> /* content */ }
```

## Performance Optimization

### Content Key Optimization

The NavigationHost automatically uses `screen.key` for content keys, optimizing recomposition:

```kotlin
// Screens with same key preserve their state
val screen1 = DetailScreen(DetailParams(1, "title"))
val screen2 = DetailScreen(DetailParams(1, "title"))
// screen1.key == screen2.key (state preserved)

// Different keys create separate instances
val screen3 = DetailScreen(DetailParams(2, "other"))
// screen3.key != screen1.key (separate state)
```

### Large Content Optimization

For screens with expensive content:

```kotlin
NavigationHost(
    initialScreen = HomeScreen
) { screen ->
    when (screen) {
        is HeavyScreen -> {
            // Use LaunchedEffect to load data only once
            var data by remember { mutableStateOf<Data?>(null) }

            LaunchedEffect(screen.key) {
                data = loadHeavyData(screen.parameters)
            }

            data?.let { HeavyContent(it) }
                ?: LoadingScreen()
        }
    }
}
```

## Testing NavigationHost

### Unit Testing Screen Logic

```kotlin
@Test
fun testScreenNavigation() {
    composeTestRule.setContent {
        NavigationHost(
            initialScreen = HomeScreen
        ) { screen ->
            when (screen) {
                is HomeScreen -> {
                    Button(
                        onClick = { navigateTo(SettingsScreen) },
                        modifier = Modifier.testTag("settings-button")
                    ) {
                        Text("Settings")
                    }
                }
                is SettingsScreen -> {
                    Text(
                        "Settings Screen",
                        modifier = Modifier.testTag("settings-text")
                    )
                }
            }
        }
    }

    // Test navigation
    composeTestRule.onNodeWithTag("settings-button").performClick()
    composeTestRule.onNodeWithTag("settings-text").assertIsDisplayed()
}
```

### Testing with Custom State

```kotlin
@Test
fun testNavigationState() {
    lateinit var navigator: Navigator

    composeTestRule.setContent {
        val navigationState = rememberNavigationState(HomeScreen)
        navigator = rememberNavigator(navigationState)

        NavigationHost(
            navigationState = navigationState,
            navigator = navigator
        ) { screen ->
            Text("Current: ${screen.screenId}")
        }
    }

    // Test navigation operations
    navigator.push(SettingsScreen)
    composeTestRule.onNodeWithText("Current: SettingsScreen").assertIsDisplayed()
}
```

## Error Handling

### Screen Resolution Errors

```kotlin
NavigationHost(
    initialScreen = HomeScreen
) { screen ->
    try {
        when (screen) {
            is HomeScreen -> HomeContent()
            is SettingsScreen -> SettingsContent()
            else -> {
                ErrorScreen(
                    message = "Unknown screen: ${screen::class.simpleName}",
                    onBackClick = { navigateBack() }
                )
            }
        }
    } catch (e: Exception) {
        ErrorBoundary(
            error = e,
            onRetry = { /* retry logic */ },
            onBack = { navigateBack() }
        )
    }
}
```

### Navigation Error Recovery

```kotlin
@Composable
fun SafeNavigationHost() {
    NavigationHost(
        initialScreen = HomeScreen
    ) { screen ->
        var hasError by remember(screen) { mutableStateOf(false) }

        if (hasError) {
            ErrorRecoveryScreen(
                onRetry = { hasError = false },
                onGoHome = {
                    hasError = false
                    navigateBackToRoot()
                }
            )
        } else {
            try {
                ScreenContent(screen)
            } catch (e: Exception) {
                LaunchedEffect(e) {
                    hasError = true
                    logError(e)
                }
            }
        }
    }
}
```

## Integration with Other Systems

### With ViewModel

```kotlin
@Composable
fun NavigationWithViewModel() {
    NavigationHost(
        initialScreen = HomeScreen
    ) { screen ->
        when (screen) {
            is HomeScreen -> {
                val viewModel = viewModel<HomeViewModel>()
                HomeContent(viewModel)
            }
            is DetailScreen -> {
                val viewModel = viewModel<DetailViewModel> {
                    DetailViewModel(screen.parameters.itemId)
                }
                DetailContent(viewModel)
            }
        }
    }
}
```

### With Dependency Injection

```kotlin
@Composable
fun NavigationWithDI() {
    NavigationHost(
        initialScreen = HomeScreen
    ) { screen ->
        when (screen) {
            is UserScreen -> {
                val userRepository = get<UserRepository>()
                val viewModel = remember {
                    UserViewModel(userRepository, screen.parameters.userId)
                }
                UserContent(viewModel)
            }
        }
    }
}
```

## Best Practices

### Do's ✅

1. **Use NavigationScope extension functions** for clean screen composables
```kotlin
@Composable
fun NavigationScope.MyScreen() {
    // Direct access to navigation methods
    Button(onClick = { navigateTo(NextScreen) }) {
        Text("Next")
    }
}
```

2. **Handle back navigation properly**
```kotlin
@Composable
fun NavigationScope.ScreenWithBackButton() {
    if (canNavigateBack) {
        BackButton(onClick = { navigateBack() })
    }
}
```

3. **Use appropriate transition types**
```kotlin
// Standard navigation
NavigationHost(transitionType = NavigationTransition.Slide)

// Modal presentations
NavigationHost(transitionType = NavigationTransition.Modal)
```

### Don'ts ❌

1. **Don't navigate during composition**
```kotlin
// ❌ Bad
@Composable
fun BadScreen() {
    navigateTo(NextScreen) // Causes recomposition loops
}

// ✅ Good
@Composable
fun GoodScreen() {
    LaunchedEffect(someCondition) {
        if (someCondition) {
            navigateTo(NextScreen)
        }
    }
}
```

2. **Don't ignore navigation state**
```kotlin
// ❌ Bad
Button(onClick = { navigateBack() }) {
    Text("Back")
}

// ✅ Good
Button(
    onClick = { navigateBack() },
    enabled = canNavigateBack
) {
    Text("Back")
}
```

3. **Don't create multiple NavigationHosts**
```kotlin
// ❌ Bad - Multiple hosts cause conflicts
Column {
    NavigationHost(HomeScreen) { /* content */ }
    NavigationHost(OtherScreen) { /* content */ }
}

// ✅ Good - Single host with conditional logic
NavigationHost(initialScreen) { screen ->
    when (screen) {
        // Handle all screens in one place
    }
}
```

This documentation covers all aspects of NavigationHost usage. Next, I'll document the animation system in detail.