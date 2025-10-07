# Migration and Integration Guide

## Overview

This guide helps you integrate the ThinkTwice Navigation System into existing projects and migrate from other navigation solutions. It covers various scenarios and provides step-by-step migration strategies.

## Table of Contents

1. [Integration into Existing Projects](#integration-into-existing-projects)
2. [Migrating from Jetpack Navigation](#migrating-from-jetpack-navigation)
3. [Migrating from Other Navigation Libraries](#migrating-from-other-navigation-libraries)
4. [Gradual Migration Strategies](#gradual-migration-strategies)
5. [Compatibility Considerations](#compatibility-considerations)
6. [Testing Migration](#testing-migration)
7. [Common Migration Issues](#common-migration-issues)

## Integration into Existing Projects

### 1. Adding to New KMP Project

**Step 1: Dependencies**
No additional dependencies needed - the navigation system is self-contained within your project.

**Step 2: Basic Setup**
```kotlin
// App.kt
@Composable
fun App() {
    MaterialTheme {
        NavigationHost(
            initialScreen = HomeScreen,
            transitionType = NavigationTransition.Slide
        ) { screen ->
            when (screen) {
                is HomeScreen -> HomeContent()
                // Add your screens here
            }
        }
    }
}
```

**Step 3: Define Your Screens**
```kotlin
// Create a screens package
package com.yourapp.navigation.screens

@Serializable
data object HomeScreen : SimpleScreen(), RootScreen

@Serializable
data object SettingsScreen : SimpleScreen()

@Serializable
data class DetailScreen(
    override val parameters: DetailParams
) : ParameterizedScreen<DetailParams>()

@Serializable
data class DetailParams(val id: Long, val title: String)
```

### 2. Integration with Existing Compose App

**Step 1: Identify Current Navigation**
```kotlin
// Current app structure (example)
@Composable
fun ExistingApp() {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen { currentScreen = "settings" }
        "settings" -> SettingsScreen { currentScreen = "home" }
    }
}
```

**Step 2: Define Navigation Screens**
```kotlin
@Serializable
data object HomeScreen : SimpleScreen(), RootScreen

@Serializable
data object SettingsScreen : SimpleScreen()
```

**Step 3: Replace with NavigationHost**
```kotlin
@Composable
fun MigratedApp() {
    NavigationHost(
        initialScreen = HomeScreen
    ) { screen ->
        when (screen) {
            is HomeScreen -> HomeContent()
            is SettingsScreen -> SettingsContent()
        }
    }
}

@Composable
fun NavigationScope.HomeContent() {
    // Existing HomeScreen content, but now with navigation access
    ExistingHomeContent(
        onNavigateToSettings = { navigateTo(SettingsScreen) }
    )
}
```

**Step 4: Update Screen Composables**
```kotlin
// Before
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Button(onClick = { onNavigate("settings") }) {
        Text("Settings")
    }
}

// After
@Composable
fun NavigationScope.HomeContent() {
    Button(onClick = { navigateTo(SettingsScreen) }) {
        Text("Settings")
    }
}
```

### 3. Integration with Existing View System (Android)

For projects transitioning from View-based navigation to Compose with ThinkTwice Navigation:

**Step 1: Create Compose Activity**
```kotlin
class ComposeNavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NavigationHost(
                    initialScreen = getInitialScreen()
                ) { screen ->
                    when (screen) {
                        is HomeScreen -> HomeContent()
                        is LegacyScreen -> LegacyViewWrapper(screen.parameters)
                    }
                }
            }
        }
    }

    private fun getInitialScreen(): Screen {
        // Parse intent/deep link to determine initial screen
        return when (intent.getStringExtra("target")) {
            "settings" -> SettingsScreen
            else -> HomeScreen
        }
    }
}
```

**Step 2: Wrap Legacy Views**
```kotlin
@Serializable
data class LegacyScreen(
    override val parameters: LegacyParams
) : ParameterizedScreen<LegacyParams>()

@Serializable
data class LegacyParams(val viewType: String, val data: String)

@Composable
fun NavigationScope.LegacyViewWrapper(params: LegacyParams) {
    AndroidView(
        factory = { context ->
            when (params.viewType) {
                "settings" -> createLegacySettingsView(context)
                else -> createDefaultView(context)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
```

## Migrating from Jetpack Navigation

### Common Patterns Translation

**Navigation Graph → Screen Definitions**
```kotlin
// Jetpack Navigation (nav_graph.xml)
<navigation>
    <fragment android:name="HomeFragment" android:id="@+id/homeFragment" />
    <fragment android:name="DetailFragment" android:id="@+id/detailFragment" />
</navigation>

// ThinkTwice Navigation
@Serializable data object HomeScreen : SimpleScreen()
@Serializable data class DetailScreen(
    override val parameters: DetailParams
) : ParameterizedScreen<DetailParams>()
```

**NavController → Navigator**
```kotlin
// Jetpack Navigation
findNavController().navigate(R.id.action_home_to_detail)
findNavController().navigateUp()

// ThinkTwice Navigation
navigateTo(DetailScreen(DetailParams(id)))
navigateBack()
```

**Arguments → Parameters**
```kotlin
// Jetpack Navigation
val args: DetailFragmentArgs by navArgs()
val itemId = args.itemId

// ThinkTwice Navigation
@Composable
fun NavigationScope.DetailContent(params: DetailParams) {
    val itemId = params.itemId
}
```

### Step-by-Step Migration

**Step 1: Map Existing Destinations**
```kotlin
// Create mapping table
val destinationMapping = mapOf(
    R.id.homeFragment to HomeScreen,
    R.id.detailFragment to { args -> DetailScreen(DetailParams(args.itemId)) },
    R.id.settingsFragment to SettingsScreen
)
```

**Step 2: Convert Fragments to Composables**
```kotlin
// Old Fragment
class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(...): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DetailContent(args.itemId)
            }
        }
    }
}

// New Composable
@Composable
fun NavigationScope.DetailContent(params: DetailParams) {
    DetailUI(
        itemId = params.itemId,
        onNavigateBack = { navigateBack() }
    )
}
```

**Step 3: Update Navigation Calls**
```kotlin
// Replace throughout codebase
// Old
findNavController().navigate(
    DetailFragmentDirections.actionHomeToDetail(itemId)
)

// New
navigateTo(DetailScreen(DetailParams(itemId)))
```

### Handling Complex Navigation Graphs

**Deep Links**
```kotlin
// Jetpack Navigation deep links
<deepLink app:uri="myapp://item/{itemId}" />

// ThinkTwice Navigation deep link handling
fun handleDeepLink(uri: Uri): Screen {
    return when (uri.pathSegments.firstOrNull()) {
        "item" -> {
            val itemId = uri.pathSegments.getOrNull(1)?.toLongOrNull()
            if (itemId != null) {
                DetailScreen(DetailParams(itemId))
            } else {
                HomeScreen
            }
        }
        else -> HomeScreen
    }
}
```

**Nested Navigation**
```kotlin
// Jetpack Navigation nested graphs
<navigation android:id="@+id/user_graph">
    <fragment android:id="@+id/userListFragment" />
    <fragment android:id="@+id/userDetailFragment" />
</navigation>

// ThinkTwice Navigation equivalent
@Serializable sealed interface UserFlow : Screen
@Serializable data object UserListScreen : SimpleScreen(), UserFlow
@Serializable data class UserDetailScreen(...) : ParameterizedScreen<...>(), UserFlow
```

## Migrating from Other Navigation Libraries

### From Voyager

**Screen Definition**
```kotlin
// Voyager
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        HomeContent()
    }
}

// ThinkTwice Navigation
@Serializable
data object HomeScreen : SimpleScreen()

@Composable
fun NavigationScope.HomeContent() {
    // Content
}
```

**Navigation Operations**
```kotlin
// Voyager
val navigator = LocalNavigator.current
navigator.push(DetailScreen())
navigator.pop()

// ThinkTwice Navigation
navigateTo(DetailScreen())
navigateBack()
```

### From PreCompose Navigation

**Route Definition**
```kotlin
// PreCompose
NavHost(
    navigator = navigator,
    initialRoute = "/home"
) {
    scene("/home") { HomeScreen() }
    scene("/detail/{id}") { backStackEntry ->
        val id = backStackEntry.path<String>("id")
        DetailScreen(id)
    }
}

// ThinkTwice Navigation
NavigationHost(
    initialScreen = HomeScreen
) { screen ->
    when (screen) {
        is HomeScreen -> HomeContent()
        is DetailScreen -> DetailContent(screen.parameters)
    }
}
```

### From Decompose Navigation

**Component Definition**
```kotlin
// Decompose
sealed class Config : Parcelable {
    @Parcelize object Home : Config()
    @Parcelize data class Detail(val id: Long) : Config()
}

// ThinkTwice Navigation
@Serializable data object HomeScreen : SimpleScreen()
@Serializable data class DetailScreen(
    override val parameters: DetailParams
) : ParameterizedScreen<DetailParams>()
```

## Gradual Migration Strategies

### 1. Feature-by-Feature Migration

**Step 1: Start with New Features**
```kotlin
@Composable
fun HybridApp() {
    var useNewNavigation by remember { mutableStateOf(false) }

    if (useNewNavigation) {
        // New features use ThinkTwice Navigation
        NavigationHost(initialScreen = NewFeatureScreen) { screen ->
            when (screen) {
                is NewFeatureScreen -> NewFeatureContent()
                is LegacyBridgeScreen -> {
                    // Bridge to old navigation
                    useNewNavigation = false
                }
            }
        }
    } else {
        // Legacy navigation
        LegacyNavigationSystem(
            onNavigateToNewFeature = {
                useNewNavigation = true
            }
        )
    }
}
```

**Step 2: Create Bridge Screens**
```kotlin
@Serializable
data class LegacyBridgeScreen(
    override val parameters: LegacyBridgeParams
) : ParameterizedScreen<LegacyBridgeParams>()

@Serializable
data class LegacyBridgeParams(val targetLegacyScreen: String)

@Composable
fun NavigationScope.LegacyBridgeContent(params: LegacyBridgeParams) {
    LaunchedEffect(params.targetLegacyScreen) {
        // Navigate to legacy system
        legacyNavigationManager.navigateTo(params.targetLegacyScreen)
    }
}
```

### 2. Bottom-Up Migration

**Step 1: Migrate Leaf Screens First**
```kotlin
// Start with screens that don't navigate to other screens
@Composable
fun NavigationScope.MigratedDetailScreen(params: DetailParams) {
    // This screen only navigates back, easy to migrate
    DetailContent(
        data = params,
        onBack = { navigateBack() }
    )
}
```

**Step 2: Work Up the Hierarchy**
```kotlin
// Then migrate screens that navigate to already-migrated screens
@Composable
fun NavigationScope.MigratedListScreen() {
    ListContent(
        onItemClick = { item ->
            navigateTo(DetailScreen(DetailParams(item.id)))
        }
    )
}
```

### 3. Side-by-Side Migration

**Step 1: Duplicate Navigation Structure**
```kotlin
@Composable
fun App() {
    var useNewNavigation by rememberSaveable { mutableStateOf(false) }

    Column {
        Switch(
            checked = useNewNavigation,
            onCheckedChange = { useNewNavigation = it }
        )

        if (useNewNavigation) {
            NewNavigationSystem()
        } else {
            LegacyNavigationSystem()
        }
    }
}
```

**Step 2: Gradually Replace Screens**
```kotlin
@Composable
fun HybridNavigationHost() {
    NavigationHost(initialScreen = HomeScreen) { screen ->
        when (screen) {
            is HomeScreen -> if (isScreenMigrated("home")) {
                NewHomeContent()
            } else {
                LegacyHomeWrapper()
            }
            // Gradual replacement
        }
    }
}
```

## Compatibility Considerations

### Android Compatibility

**Minimum SDK Requirements**
- API 21+ (Android 5.0) - Same as Compose minimum
- Works with all Compose versions 1.0+

**Integration with Android Components**
```kotlin
// Activity integration
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NavigationHost(initialScreen = HomeScreen) { screen ->
                // Handle system back press
                BackHandler {
                    if (!navigator.onBackPressed()) {
                        finish()
                    }
                }

                ScreenContent(screen)
            }
        }
    }
}

// Fragment integration
class ComposeFragment : Fragment() {
    override fun onCreateView(...): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NavigationHost(initialScreen = FragmentScreen) { screen ->
                    ScreenContent(screen)
                }
            }
        }
    }
}
```

### iOS Compatibility

**SwiftUI Integration**
```swift
// iOS bridge (if needed)
struct NavigationHostWrapper: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return ComposeViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Update if needed
    }
}
```

### Desktop Compatibility

**Desktop-specific Considerations**
```kotlin
@Composable
fun DesktopApp() {
    NavigationHost(
        initialScreen = HomeScreen,
        // Desktop might prefer no animations or different ones
        transitionType = if (isDesktopPlatform()) {
            NavigationTransition.None
        } else {
            NavigationTransition.Slide
        }
    ) { screen ->
        ScreenContent(screen)
    }
}
```

## Testing Migration

### Unit Testing Migration

**Test Screen Definitions**
```kotlin
@Test
fun testScreenMigration() {
    // Test that old screens map to new screens correctly
    val oldRoute = "/detail/123"
    val newScreen = migrateRoute(oldRoute)

    assertTrue(newScreen is DetailScreen)
    assertEquals(123, (newScreen as DetailScreen).parameters.id)
}
```

**Test Navigation Behavior**
```kotlin
@Test
fun testMigratedNavigation() {
    val testNavigator = createTestNavigator()

    // Test that migrated navigation works the same
    testNavigator.push(HomeScreen)
    testNavigator.push(DetailScreen(DetailParams(1)))

    assertEquals(2, testNavigator.stackDepth)
    assertTrue(testNavigator.currentScreen is DetailScreen)
}
```

### Integration Testing

**Test Migration Bridges**
```kotlin
@Test
fun testLegacyBridge() {
    composeTestRule.setContent {
        NavigationHost(initialScreen = HomeScreen) { screen ->
            when (screen) {
                is LegacyBridgeScreen -> LegacyBridgeContent(screen.parameters)
            }
        }
    }

    // Test that bridge correctly triggers legacy navigation
    composeTestRule.onNodeWithTag("legacy-bridge").performClick()
    verify(legacyNavigationManager).navigateTo("legacy_screen")
}
```

### UI Testing Migration

**Automated Migration Testing**
```kotlin
@Test
fun testFullMigrationFlow() {
    // Test complete user flow through migrated navigation
    composeTestRule.setContent {
        MigratedApp()
    }

    // Navigate through app and verify behavior
    composeTestRule.onNodeWithText("Settings").performClick()
    composeTestRule.onNodeWithText("Settings Screen").assertIsDisplayed()

    composeTestRule.onNodeWithContentDescription("Back").performClick()
    composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()
}
```

## Common Migration Issues

### 1. Parameter Serialization Issues

**Problem:**
```kotlin
// This won't work - complex objects aren't serializable
@Serializable
data class BadParams(
    val complexObject: SomeComplexClass // ❌
)
```

**Solution:**
```kotlin
// Use simple, serializable parameters
@Serializable
data class GoodParams(
    val id: Long,
    val title: String,
    val metadata: String // Serialize complex objects to JSON strings if needed
)
```

### 2. State Management Conflicts

**Problem:**
```kotlin
// Mixing state management approaches
@Composable
fun ProblematicScreen() {
    val legacyState = LegacyStateManager.current
    var newState by remember { mutableStateOf("") }
    // Conflicts between old and new state
}
```

**Solution:**
```kotlin
// Create clear boundaries
@Composable
fun NavigationScope.MigratedScreen() {
    // Use only new state management within migrated screens
    val viewModel = viewModel<MigratedScreenViewModel>()
    val state by viewModel.uiState.collectAsState()
}
```

### 3. Animation Conflicts

**Problem:**
```kotlin
// Mixing animation systems causes glitches
@Composable
fun ConflictingAnimations() {
    AnimatedVisibility(visible = true) { // Compose animation
        LegacyAnimatedView() // Legacy animation system
    }
}
```

**Solution:**
```kotlin
// Use consistent animation approach
NavigationHost(
    transitionType = NavigationTransition.Slide // Consistent animations
) { screen ->
    when (screen) {
        is MigratedScreen -> MigratedContent() // Pure Compose
        is LegacyScreen -> StaticLegacyWrapper() // No competing animations
    }
}
```

### 4. Deep Link Migration

**Problem:**
```kotlin
// Deep links break during migration
val oldDeepLink = "myapp://item/123"
// New system doesn't recognize old format
```

**Solution:**
```kotlin
// Support both old and new deep link formats
fun handleDeepLink(uri: Uri): Screen {
    return when {
        uri.path?.startsWith("/legacy/") == true -> {
            migrateLegacyDeepLink(uri)
        }
        uri.path?.startsWith("/item/") == true -> {
            val id = uri.lastPathSegment?.toLongOrNull()
            if (id != null) DetailScreen(DetailParams(id)) else HomeScreen
        }
        else -> HomeScreen
    }
}

fun migrateLegacyDeepLink(uri: Uri): Screen {
    // Convert old deep link format to new screen
    return when (uri.getQueryParameter("type")) {
        "detail" -> {
            val id = uri.getQueryParameter("id")?.toLongOrNull()
            if (id != null) DetailScreen(DetailParams(id)) else HomeScreen
        }
        else -> HomeScreen
    }
}
```

### 5. Performance Issues During Migration

**Problem:**
```kotlin
// Running both navigation systems simultaneously
@Composable
fun HeavyMigration() {
    LegacyNavigationSystem() // Still running
    NavigationHost(...) { } // New system also running
    // Double memory usage, poor performance
}
```

**Solution:**
```kotlin
// Use exclusive migration approach
@Composable
fun EfficientMigration() {
    val useLegacy = shouldUseLegacyForScreen(currentScreen)

    if (useLegacy) {
        LegacyNavigationSystem()
    } else {
        NavigationHost(initialScreen = currentScreen) { screen ->
            ScreenContent(screen)
        }
    }
}
```

This comprehensive migration guide covers all common scenarios for integrating the ThinkTwice Navigation System into existing projects and migrating from other navigation solutions.