# Screen Interface Documentation

## Overview

The `Screen` interface is the foundation of the navigation system. All navigation destinations must implement this interface to be part of the navigation stack.

## Base Screen Interface

```kotlin
@Serializable
sealed interface Screen {
    val screenId: String get() = this::class.simpleName ?: "Unknown"
    val key: String get() = screenId
}
```

### Properties

| Property | Type | Description | Default |
|----------|------|-------------|---------|
| `screenId` | `String` | Unique identifier for screen type | Class simple name |
| `key` | `String` | Key for state preservation | Same as `screenId` |

### Key Concepts

- **Serializable**: All screens must be annotated with `@Serializable`
- **Sealed Interface**: Ensures type safety and exhaustive when expressions
- **Unique Keys**: Each screen instance has a unique key for state management

## Screen Types

### 1. SimpleScreen

For screens without parameters:

```kotlin
@Serializable
abstract class SimpleScreen : Screen
```

**Example:**
```kotlin
@Serializable
data object HomeScreen : SimpleScreen()

@Serializable
data object SettingsScreen : SimpleScreen()

@Serializable
data object AboutScreen : SimpleScreen()
```

**Usage:**
```kotlin
// Navigation
navigateTo(HomeScreen)
navigateTo(SettingsScreen)

// In NavigationHost
when (screen) {
    is HomeScreen -> HomeContent()
    is SettingsScreen -> SettingsContent()
    is AboutScreen -> AboutContent()
}
```

### 2. ParameterizedScreen<T>

For screens that require parameters:

```kotlin
@Serializable
abstract class ParameterizedScreen<T : Any> : Screen {
    abstract val parameters: T

    override val key: String
        get() = "${screenId}_${parameters.hashCode()}"
}
```

**Example:**
```kotlin
@Serializable
data class UserDetailScreen(
    override val parameters: UserDetailParams
) : ParameterizedScreen<UserDetailParams>()

@Serializable
data class UserDetailParams(
    val userId: Long,
    val username: String,
    val email: String? = null
)
```

**Usage:**
```kotlin
// Create screen with parameters
val userScreen = UserDetailScreen(
    UserDetailParams(
        userId = 123,
        username = "john_doe",
        email = "john@example.com"
    )
)

// Navigate
navigateTo(userScreen)

// In NavigationHost
when (screen) {
    is UserDetailScreen -> UserDetailContent(screen.parameters)
}

// In composable
@Composable
fun UserDetailContent(params: UserDetailParams) {
    Text("User: ${params.username}")
    Text("ID: ${params.userId}")
    params.email?.let { Text("Email: $it") }
}
```

### 3. Marker Interfaces

#### RootScreen

Screens that should clear the navigation stack when navigated to:

```kotlin
interface RootScreen
```

**Use Cases:**
- Login screens
- Onboarding flows
- Home screens after authentication
- App reset points

**Example:**
```kotlin
@Serializable
data object LoginScreen : SimpleScreen(), RootScreen

@Serializable
data object OnboardingScreen : SimpleScreen(), RootScreen

@Serializable
data object MainHomeScreen : SimpleScreen(), RootScreen
```

**Behavior:**
When navigating to a `RootScreen`, the navigation stack is cleared and the screen becomes the only item in the stack.

```kotlin
// Current stack: [Home, Settings, Profile]
navigateTo(LoginScreen) // LoginScreen implements RootScreen
// New stack: [LoginScreen]
```

#### BackPressHandler

Screens that need custom back press handling:

```kotlin
interface BackPressHandler {
    fun onBackPressed(): Boolean
}
```

**Use Cases:**
- Forms with unsaved changes
- Screens with custom back behavior
- Confirmation dialogs
- Multi-step flows

**Example:**
```kotlin
@Serializable
data class EditFormScreen(
    override val parameters: EditFormParams
) : ParameterizedScreen<EditFormParams>(), BackPressHandler {

    override fun onBackPressed(): Boolean {
        // Check if form has unsaved changes
        return if (hasUnsavedChanges()) {
            // Show confirmation dialog
            showUnsavedChangesDialog()
            true // Handle the back press
        } else {
            false // Let navigation system handle it
        }
    }
}
```

## Parameter Design

### Best Practices

✅ **Do:**
```kotlin
// Simple, serializable parameters
@Serializable
data class NoteParams(
    val noteId: Long,
    val title: String = "",
    val category: String? = null
)

// Use primitive types and basic collections
@Serializable
data class SearchParams(
    val query: String,
    val filters: List<String> = emptyList(),
    val sortBy: String = "date"
)
```

❌ **Don't:**
```kotlin
// Complex objects (won't serialize properly)
data class BadParams(
    val viewModel: MyViewModel, // ❌ Not serializable
    val context: Context, // ❌ Android-specific
    val callback: () -> Unit // ❌ Functions not serializable
)

// Mutable collections
data class MutableParams(
    val items: MutableList<String> // ❌ Use List instead
)
```

### Supported Parameter Types

| Type | Supported | Notes |
|------|-----------|-------|
| Primitives | ✅ | `String`, `Int`, `Long`, `Boolean`, etc. |
| Data Classes | ✅ | Must be `@Serializable` |
| Lists | ✅ | `List<T>` where `T` is serializable |
| Maps | ✅ | `Map<K, V>` where `K`, `V` are serializable |
| Enums | ✅ | Must be `@Serializable` |
| Sealed Classes | ✅ | Must be `@Serializable` |
| Functions | ❌ | Not serializable |
| Android/iOS Objects | ❌ | Platform-specific objects |

### Parameter Validation

```kotlin
@Serializable
data class ValidatedParams(
    val userId: Long,
    val email: String
) {
    init {
        require(userId > 0) { "User ID must be positive" }
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(email.contains("@")) { "Email must be valid" }
    }
}
```

## Screen Lifecycle

### State Preservation

Screens automatically preserve their state based on their `key`:

```kotlin
// Same screen type, same parameters = same key = preserved state
val screen1 = UserDetailScreen(UserDetailParams(1, "john"))
val screen2 = UserDetailScreen(UserDetailParams(1, "john"))
// screen1.key == screen2.key (state preserved)

// Different parameters = different key = separate state
val screen3 = UserDetailScreen(UserDetailParams(2, "jane"))
// screen3.key != screen1.key (separate state)
```

### Custom Key Generation

For custom key generation:

```kotlin
@Serializable
data class CustomKeyScreen(
    override val parameters: CustomParams
) : ParameterizedScreen<CustomParams>() {

    override val key: String
        get() = "custom_${parameters.id}_${parameters.version}"
}
```

## Testing Screens

### Unit Testing

```kotlin
@Test
fun testScreenCreation() {
    val params = UserDetailParams(123, "testuser")
    val screen = UserDetailScreen(params)

    assertEquals("UserDetailScreen", screen.screenId)
    assertEquals(123, screen.parameters.userId)
    assertEquals("testuser", screen.parameters.username)
}

@Test
fun testScreenSerialization() {
    val original = UserDetailScreen(
        UserDetailParams(123, "testuser", "test@example.com")
    )

    val json = Json.encodeToString(original)
    val deserialized = Json.decodeFromString<UserDetailScreen>(json)

    assertEquals(original.parameters, deserialized.parameters)
}
```

### Navigation Testing

```kotlin
@Test
fun testRootScreenBehavior() {
    val navigator = TestNavigator()

    // Build up stack
    navigator.push(HomeScreen)
    navigator.push(SettingsScreen)
    assertEquals(2, navigator.stackDepth)

    // Navigate to root screen
    navigator.push(LoginScreen) // RootScreen
    assertEquals(1, navigator.stackDepth)
    assertEquals(LoginScreen, navigator.currentScreen)
}
```

## Common Patterns

### 1. List-Detail Pattern

```kotlin
@Serializable
data object ItemListScreen : SimpleScreen()

@Serializable
data class ItemDetailScreen(
    override val parameters: ItemDetailParams
) : ParameterizedScreen<ItemDetailParams>()

@Serializable
data class ItemDetailParams(
    val itemId: Long,
    val itemType: String
)

// Usage
@Composable
fun NavigationScope.ItemListContent() {
    LazyColumn {
        items(itemList) { item ->
            ItemRow(
                item = item,
                onClick = {
                    navigateTo(
                        ItemDetailScreen(
                            ItemDetailParams(item.id, item.type)
                        )
                    )
                }
            )
        }
    }
}
```

### 2. Multi-Step Flows

```kotlin
@Serializable
data object StepOneScreen : SimpleScreen()

@Serializable
data class StepTwoScreen(
    override val parameters: StepTwoParams
) : ParameterizedScreen<StepTwoParams>()

@Serializable
data class StepThreeScreen(
    override val parameters: StepThreeParams
) : ParameterizedScreen<StepThreeParams>()

// Flow navigation
fun NavigationScope.proceedToStepTwo(data: StepOneData) {
    navigateTo(StepTwoScreen(StepTwoParams(data)))
}

fun NavigationScope.completeFlow() {
    // Pop back to start or navigate to result
    navigateBackTo(StepOneScreen)
}
```

### 3. Modal Screens

```kotlin
@Serializable
data class ModalScreen(
    override val parameters: ModalParams
) : ParameterizedScreen<ModalParams>()

// Use Modal transition type
NavigationHost(
    transitionType = NavigationTransition.Modal
) { screen ->
    when (screen) {
        is ModalScreen -> ModalContent(screen.parameters)
    }
}
```

## Error Handling

### Parameter Validation Errors

```kotlin
@Serializable
data class SafeParams(
    val id: Long,
    val name: String
) {
    init {
        require(id > 0) { "ID must be positive, got: $id" }
        require(name.isNotBlank()) { "Name cannot be blank" }
    }
}

// Usage with error handling
try {
    val screen = DetailScreen(SafeParams(-1, ""))
    navigateTo(screen)
} catch (e: IllegalArgumentException) {
    // Handle validation error
    showError("Invalid parameters: ${e.message}")
}
```

### Serialization Errors

```kotlin
// Use safe serialization
fun createScreenSafely(json: String): Screen? {
    return try {
        Json.decodeFromString<Screen>(json)
    } catch (e: SerializationException) {
        Log.e("Navigation", "Failed to deserialize screen", e)
        null
    }
}
```

This documentation covers all aspects of the Screen interface system. The next sections will cover the Navigator API and other components in detail.