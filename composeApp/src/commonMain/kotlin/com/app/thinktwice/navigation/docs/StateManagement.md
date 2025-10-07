# State Management and Lifecycle Documentation

## Overview

The ThinkTwice Navigation System provides sophisticated state management that integrates seamlessly with Compose's state system. This document covers how navigation state is managed, preserved, and restored across the application lifecycle.

## Table of Contents

1. [Navigation State Architecture](#navigation-state-architecture)
2. [Screen State Preservation](#screen-state-preservation)
3. [Lifecycle Integration](#lifecycle-integration)
4. [State Flows and Reactive Updates](#state-flows-and-reactive-updates)
5. [Memory Management](#memory-management)
6. [Configuration Changes](#configuration-changes)
7. [Process Death and Restoration](#process-death-and-restoration)
8. [Best Practices](#best-practices)

## Navigation State Architecture

### NavigationState Class

The `NavigationState` class is the core state container for the navigation system:

```kotlin
@Stable
class NavigationState internal constructor(
    initialScreen: Screen
) {
    // Internal state flows
    private val _backStack = MutableStateFlow(listOf(initialScreen))
    private val _currentScreen = MutableStateFlow(initialScreen)
    private val _isNavigating = MutableStateFlow(false)

    // Public reactive state
    val backStack: StateFlow<List<Screen>> = _backStack.asStateFlow()
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()

    // Compose state integration
    val currentScreenState: State<Screen> @Composable get() = currentScreen.collectAsState()
    val backStackState: State<List<Screen>> @Composable get() = backStack.collectAsState()
    val isNavigatingState: State<Boolean> @Composable get() = isNavigating.collectAsState()

    // Computed properties
    val canPopBack: Boolean get() = _backStack.value.size > 1
}
```

### State Updates

All navigation operations update the state atomically:

```kotlin
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
```

### State Consistency

The navigation system ensures state consistency through:
- **Atomic Updates**: All related state changes happen together
- **Validation**: Invalid operations (like popping from empty stack) are prevented
- **Synchronization**: State updates are serialized to prevent race conditions

## Screen State Preservation

### Automatic State Preservation

Each screen automatically preserves its Compose state based on its unique key:

```kotlin
// Screen key generation
interface Screen {
    val key: String get() = screenId
}

// Parameterized screens include parameter hash
abstract class ParameterizedScreen<T : Any> : Screen {
    override val key: String
        get() = "${screenId}_${parameters.hashCode()}"
}
```

### Examples of State Preservation

```kotlin
// Simple screens share state by type
@Serializable
data object SettingsScreen : SimpleScreen()

val screen1 = SettingsScreen
val screen2 = SettingsScreen
// screen1.key == screen2.key (shared state)

// Parameterized screens have unique state per parameter combination
@Serializable
data class UserScreen(override val parameters: UserParams) : ParameterizedScreen<UserParams>()

val user1Screen = UserScreen(UserParams(1, "Alice"))
val user2Screen = UserScreen(UserParams(2, "Bob"))
val user1ScreenAgain = UserScreen(UserParams(1, "Alice"))

// user1Screen.key == user1ScreenAgain.key (shared state)
// user1Screen.key != user2Screen.key (separate state)
```

### Manual State Control

For custom state preservation:

```kotlin
@Serializable
data class CustomScreen(
    override val parameters: CustomParams
) : ParameterizedScreen<CustomParams>() {

    override val key: String
        get() = "custom_${parameters.primaryKey}_v${parameters.version}"
}

// This allows you to control when state is preserved vs. recreated
```

### State Preservation in Practice

```kotlin
@Composable
fun NavigationScope.FormScreen() {
    // This state is automatically preserved when navigating away and back
    var formData by remember { mutableStateOf(FormData()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Basic Info") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Advanced") }
            )
        }

        when (selectedTab) {
            0 -> BasicInfoForm(
                data = formData,
                onDataChange = { formData = it }
            )
            1 -> AdvancedForm(
                data = formData,
                onDataChange = { formData = it }
            )
        }

        Button(onClick = { navigateTo(PreviewScreen) }) {
            Text("Preview") // State preserved when returning
        }
    }
}
```

## Lifecycle Integration

### Compose Lifecycle Integration

The navigation system integrates with Compose lifecycle:

```kotlin
@Composable
fun NavigationScope.ScreenWithLifecycle() {
    // Called when screen appears
    LaunchedEffect(Unit) {
        onScreenAppear()
    }

    // Called when screen disappears
    DisposableEffect(Unit) {
        onDispose {
            onScreenDisappear()
        }
    }

    // Called when navigation state changes
    LaunchedEffect(currentScreen) {
        handleScreenChange(currentScreen)
    }
}
```

### Screen Lifecycle Events

```kotlin
@Composable
fun NavigationScope.LifecycleAwareScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> handleCreate()
                    Lifecycle.Event.ON_START -> handleStart()
                    Lifecycle.Event.ON_RESUME -> handleResume()
                    Lifecycle.Event.ON_PAUSE -> handlePause()
                    Lifecycle.Event.ON_STOP -> handleStop()
                    Lifecycle.Event.ON_DESTROY -> handleDestroy()
                }
            }
        })
    }
}
```

### ViewModel Integration

Proper ViewModel scoping with navigation:

```kotlin
@Composable
fun NavigationScope.ScreenWithViewModel() {
    // ViewModel is scoped to this screen's lifecycle
    val viewModel = viewModel<MyScreenViewModel>()

    // Observe ViewModel state
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation based on ViewModel events
    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is NavigateToDetail -> navigateTo(DetailScreen(event.params))
                is NavigateBack -> navigateBack()
            }
        }
    }

    ScreenContent(uiState, viewModel::handleAction)
}

class MyScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun handleAction(action: UiAction) {
        when (action) {
            is LoadDetails -> _navigationEvents.tryEmit(NavigateToDetail(action.id))
            is GoBack -> _navigationEvents.tryEmit(NavigateBack)
        }
    }
}
```

## State Flows and Reactive Updates

### Observing Navigation State

```kotlin
@Composable
fun NavigationStateObserver() {
    val navigationState = rememberNavigationState(HomeScreen)

    // Observe current screen changes
    val currentScreen by navigationState.currentScreenState
    LaunchedEffect(currentScreen) {
        analytics.trackScreenView(currentScreen.screenId)
    }

    // Observe back stack changes
    val backStack by navigationState.backStackState
    LaunchedEffect(backStack) {
        logger.debug("Stack depth: ${backStack.size}")
        updateNavigationUI(backStack)
    }

    // Observe navigation in progress
    val isNavigating by navigationState.isNavigatingState
    if (isNavigating) {
        LoadingOverlay()
    }
}
```

### Custom State Derivation

```kotlin
@Composable
fun NavigationScope.CustomStateDerivation() {
    // Derive custom state from navigation state
    val canShowBackButton by remember {
        derivedStateOf {
            canNavigateBack && currentScreen !is RootScreen
        }
    }

    val breadcrumbs by remember {
        derivedStateOf {
            navigator.backStack.map { it.screenId }
        }
    }

    // Use derived state in UI
    if (canShowBackButton) {
        BackButton(onClick = { navigateBack() })
    }

    BreadcrumbBar(breadcrumbs)
}
```

### State Synchronization

```kotlin
// Synchronize navigation state with external systems
@Composable
fun StateSynchronization() {
    val navigationState = rememberNavigationState(HomeScreen)
    val navigator = rememberNavigator(navigationState)

    // Sync with external state management
    val externalState by externalStateManager.state.collectAsState()

    LaunchedEffect(externalState.currentView) {
        val targetScreen = mapExternalViewToScreen(externalState.currentView)
        if (navigator.currentScreen != targetScreen) {
            navigator.push(targetScreen)
        }
    }

    // Sync navigation changes back to external system
    LaunchedEffect(navigationState.currentScreen) {
        val externalView = mapScreenToExternalView(navigationState.currentScreen.value)
        externalStateManager.updateCurrentView(externalView)
    }
}
```

## Memory Management

### Automatic Memory Management

The navigation system automatically manages memory for:
- **Navigation Stack**: Old screens are eligible for garbage collection when removed
- **State Preservation**: Only preserves state for screens in the current stack
- **Animation Frames**: Cleans up animation resources after transitions complete

### Manual Memory Management

For screens with heavy resources:

```kotlin
@Composable
fun NavigationScope.HeavyResourceScreen() {
    var heavyData by remember { mutableStateOf<HeavyData?>(null) }

    // Load resources when screen appears
    LaunchedEffect(Unit) {
        heavyData = loadHeavyData()
    }

    // Clean up resources when screen disappears
    DisposableEffect(Unit) {
        onDispose {
            heavyData?.cleanup()
            heavyData = null
        }
    }
}
```

### Stack Size Management

Monitor and manage stack size:

```kotlin
@Composable
fun NavigationScope.StackSizeManager() {
    LaunchedEffect(navigator.stackDepth) {
        if (navigator.stackDepth > MAX_STACK_SIZE) {
            // Pop to a reasonable checkpoint
            navigator.popToRoot()
            showMessage("Navigation stack reset for memory optimization")
        }
    }
}

const val MAX_STACK_SIZE = 10
```

### Memory Leak Prevention

```kotlin
@Composable
fun NavigationScope.LeakSafeScreen() {
    val coroutineScope = rememberCoroutineScope()

    // Use scoped resources
    val repository by remember {
        derivedStateOf {
            RepositoryFactory.create(coroutineScope)
        }
    }

    // Avoid storing navigation references in long-lived objects
    LaunchedEffect(Unit) {
        // ❌ Don't do this
        // GlobalState.navigator = navigator

        // ✅ Use navigation callbacks instead
        repository.setNavigationCallback { screen ->
            navigateTo(screen)
        }
    }
}
```

## Configuration Changes

### Automatic State Survival

Navigation state automatically survives configuration changes:

```kotlin
@Composable
fun App() {
    // Navigation state survives rotation, theme changes, etc.
    NavigationHost(initialScreen = HomeScreen) { screen ->
        when (screen) {
            is HomeScreen -> HomeContent()
            is SettingsScreen -> SettingsContent()
        }
    }
}
```

### Manual State Saving

For additional state beyond what's automatically preserved:

```kotlin
@Composable
fun NavigationScope.StatefulScreen() {
    // Automatically survives configuration changes
    var persistentState by rememberSaveable {
        mutableStateOf(PersistentState())
    }

    // For complex state that needs custom serialization
    var customState by rememberSaveable(
        saver = CustomStateSaver
    ) {
        mutableStateOf(CustomState())
    }
}

object CustomStateSaver : Saver<CustomState, Bundle> {
    override fun restore(value: Bundle): CustomState? {
        return CustomState.fromBundle(value)
    }

    override fun SaverScope.save(value: CustomState): Bundle? {
        return value.toBundle()
    }
}
```

### Orientation-Specific Navigation

```kotlin
@Composable
fun NavigationScope.OrientationAwareContent() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(isLandscape) {
        if (isLandscape && currentScreen is DetailScreen) {
            // Maybe navigate to a landscape-optimized version
            replaceWith(DetailLandscapeScreen(currentScreen.parameters))
        }
    }
}
```

## Process Death and Restoration

### State Persistence

The navigation system can persist and restore state across process death:

```kotlin
// Enable state persistence (platform-specific implementation)
@Composable
fun PersistentNavigationHost() {
    val savedStateHandle = rememberSaveable { Bundle() }

    val initialScreen = restoreNavigationState(savedStateHandle) ?: HomeScreen
    val navigationState = rememberNavigationState(initialScreen)

    // Save navigation state
    LaunchedEffect(navigationState.backStack) {
        saveNavigationState(savedStateHandle, navigationState.backStack.value)
    }

    NavigationHost(
        navigationState = navigationState,
        navigator = rememberNavigator(navigationState)
    ) { screen ->
        // Screen content
    }
}

private fun saveNavigationState(bundle: Bundle, backStack: List<Screen>) {
    val serializedStack = Json.encodeToString(backStack)
    bundle.putString("navigation_stack", serializedStack)
}

private fun restoreNavigationState(bundle: Bundle): Screen? {
    val serializedStack = bundle.getString("navigation_stack") ?: return null
    return try {
        val backStack = Json.decodeFromString<List<Screen>>(serializedStack)
        backStack.lastOrNull()
    } catch (e: Exception) {
        null
    }
}
```

### Deep Link Restoration

Handle app restoration from deep links:

```kotlin
@Composable
fun DeepLinkRestoration() {
    val intent = LocalContext.current.intent
    val deepLinkScreen = remember(intent) {
        parseDeepLink(intent) ?: HomeScreen
    }

    NavigationHost(initialScreen = deepLinkScreen) { screen ->
        // Screen content
    }
}
```

## Best Practices

### 1. State Management

✅ **Do:**
```kotlin
// Use appropriate state preservation
@Composable
fun NavigationScope.FormScreen() {
    // Survives navigation and configuration changes
    var formData by rememberSaveable { mutableStateOf(FormData()) }

    // For ViewModel state
    val viewModel = viewModel<FormViewModel>()
    val uiState by viewModel.uiState.collectAsState()
}

// Clean up resources properly
@Composable
fun NavigationScope.ResourceScreen() {
    DisposableEffect(Unit) {
        val resource = acquireResource()
        onDispose {
            resource.release()
        }
    }
}
```

❌ **Don't:**
```kotlin
// Don't use non-persistent state for important data
@Composable
fun BadFormScreen() {
    var formData by remember { mutableStateOf(FormData()) }
    // Lost on process death
}

// Don't leak resources
@Composable
fun BadResourceScreen() {
    val resource = remember { acquireResource() }
    // Never released
}
```

### 2. Memory Management

✅ **Do:**
```kotlin
// Monitor stack size
@Composable
fun NavigationScope.WellManagedScreen() {
    LaunchedEffect(navigator.stackDepth) {
        if (navigator.stackDepth > 8) {
            navigator.popToRoot()
        }
    }
}

// Use lazy loading for heavy content
@Composable
fun NavigationScope.HeavyScreen() {
    var data by remember { mutableStateOf<Data?>(null) }

    LaunchedEffect(Unit) {
        data = loadDataSuspending()
    }

    data?.let { HeavyContent(it) } ?: LoadingIndicator()
}
```

❌ **Don't:**
```kotlin
// Don't ignore memory pressure
@Composable
fun BadScreen() {
    val heavyData = remember { loadAllDataImmediately() }
    // Causes memory issues
}

// Don't create unbounded stacks
fun badNavigationLogic() {
    repeat(100) {
        navigator.push(DetailScreen(DetailParams(it)))
    }
    // Creates massive stack
}
```

### 3. Lifecycle Integration

✅ **Do:**
```kotlin
// Proper lifecycle handling
@Composable
fun NavigationScope.LifecycleAwareScreen() {
    LaunchedEffect(Unit) {
        startPeriodicTask()
    }

    DisposableEffect(Unit) {
        onDispose {
            stopPeriodicTask()
        }
    }
}

// Use appropriate scoping
@Composable
fun NavigationScope.ViewModelScreen() {
    val viewModel = viewModel<ScreenViewModel>()
    // ViewModel automatically cleaned up when screen is removed
}
```

❌ **Don't:**
```kotlin
// Don't ignore lifecycle events
@Composable
fun BadScreen() {
    // Starts task but never stops it
    LaunchedEffect(Unit) {
        startPeriodicTask()
    }
}

// Don't use wrong scoping
@Composable
fun BadScope() {
    val sharedViewModel = activityViewModel<SharedViewModel>()
    // May outlive the screen inappropriately
}
```

### 4. Performance Optimization

✅ **Do:**
```kotlin
// Use keys for expensive screens
@Serializable
data class ExpensiveScreen(
    override val parameters: ExpensiveParams
) : ParameterizedScreen<ExpensiveParams>() {

    override val key: String
        get() = "expensive_${parameters.uniqueId}_${parameters.version}"
}

// Optimize state updates
@Composable
fun NavigationScope.OptimizedScreen() {
    val expensiveValue by remember {
        derivedStateOf {
            computeExpensiveValue(someInputState)
        }
    }
}
```

❌ **Don't:**
```kotlin
// Don't recompute expensive operations
@Composable
fun BadScreen() {
    val result = expensiveComputation() // Recomputed every composition
}

// Don't create unnecessary keys
@Serializable
data class BadScreen(...) : ParameterizedScreen<...>() {
    override val key: String
        get() = UUID.randomUUID().toString() // Never reuses state
}
```

This comprehensive state management documentation covers all aspects of how the navigation system handles state. Finally, let me create the migration and integration guide.