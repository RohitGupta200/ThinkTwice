package com.app.thinktwice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.thinktwice.navigation.NavigationScope
// TODO: Fix imports when auth and onboarding modules are properly configured
// import com.app.thinktwice.auth.screens.SplashScreen
// import com.app.thinktwice.auth.screens.LoginScreen
// import com.app.thinktwice.onboarding.navigation.OnboardingFlow
// import com.app.thinktwice.onboarding.viewmodels.OnboardingViewModel

/**
 * Note Detail Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.NoteDetailScreenContent(params: NoteDetailParams) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        params.title.ifEmpty { "Note #${params.noteId}" },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            navigateTo(
                                EditNoteScreen(
                                    EditNoteParams(
                                        noteId = params.noteId,
                                        title = params.title,
                                        content = "Sample content for note ${params.noteId}"
                                    )
                                )
                            )
                        }
                    ) {
                        Text("Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        params.title.ifEmpty { "Untitled Note" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This is the content of note #${params.noteId}. In a real app, this would be loaded from the database.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        "Navigation Stack Info",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Stack depth: ${navigator.stackDepth}", style = MaterialTheme.typography.bodySmall)
                    Text("Note ID: ${params.noteId}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/**
 * Edit Note Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.EditNoteScreenContent(params: EditNoteParams) {
    var title by remember { mutableStateOf(params.title) }
    var content by remember { mutableStateOf(params.content) }
    val isEditing = params.noteId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Note" else "New Note",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // In a real app, save the note here
                            navigateBack()
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 10
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    "Navigation tip: Use the back button or check mark to return to the previous screen",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Settings Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.SettingsScreenContent() {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚙️")
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dark Mode", fontWeight = FontWeight.SemiBold)
                            Text("Toggle dark theme", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💭")
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Notifications", fontWeight = FontWeight.SemiBold)
                            Text("Enable push notifications", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = notifications,
                            onCheckedChange = { notifications = it }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Stack Navigation Demo", fontWeight = FontWeight.SemiBold)
                        Text("Current stack depth: ${navigator.stackDepth}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { navigateBackToRoot() },
                            enabled = navigator.stackDepth > 1
                        ) {
                            Text("Go to Root")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Profile Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.ProfileScreenContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("👤", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("John Doe", style = MaterialTheme.typography.headlineMedium)
                    Text("john.doe@example.com", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    navigateTo(
                        UserProfileScreen(
                            UserProfileParams(userId = 123, username = "johndoe")
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Detailed Profile")
            }
        }
    }
}

/**
 * About Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.AboutScreenContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ThinkTwice",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "A demonstration app showcasing stack-based navigation in Kotlin Multiplatform Compose.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = {
                    navigateTo(
                        WebViewScreen(
                            WebViewParams(
                                url = "https://developer.android.com/compose",
                                title = "Compose Documentation"
                            )
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Documentation")
            }
        }
    }
}

/**
 * User Profile Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.UserProfileScreenContent(params: UserProfileParams) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        params.username.ifEmpty { "User #${params.userId}" },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("User ID: ${params.userId}", style = MaterialTheme.typography.titleMedium)
                    Text("Username: ${params.username}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This is a detailed user profile screen with parameters passed from navigation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * WebView Screen (placeholder)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.WebViewScreenContent(params: WebViewParams) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        params.title.ifEmpty { "Web View" },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navigateBack() }) {
                        Text("← Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "WebView Placeholder",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "URL: ${params.url}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "In a real app, this would load the web content.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Splash Screen Content
 */
@Composable
fun NavigationScope.SplashScreenContent() {
    com.app.thinktwice.onboarding.screens.SplashScreen(
        onNavigateToLogin = {
            navigateTo(AuthLoginScreen)
        }
    )
}

/**
 * Auth Login Screen Content
 */
@Composable
fun NavigationScope.AuthLoginScreenContent() {
    val isIOS = false // TODO: Detect platform properly

    com.app.thinktwice.onboarding.screens.AuthLoginScreen(
        onGoogleSignIn = {
            // TODO: Implement Google OAuth
            // For now, navigate to fresh install for new users
            navigateTo(FreshInstallScreen)
        },
        onAppleSignIn = {
            // TODO: Implement Apple OAuth
            // For now, navigate to fresh install for new users
            navigateTo(FreshInstallScreen)
        },
        showAppleSignIn = isIOS
    )
}

/**
 * Fresh Install Screen Content
 */
@Composable
fun NavigationScope.FreshInstallScreenContent() {
    com.app.thinktwice.onboarding.screens.FreshInstallScreen(
        onGetStarted = {
            navigateTo(OnboardingBasicInfoScreen)
        }
    )
}

/**
 * Onboarding Screen Content
 */
@Composable
fun NavigationScope.OnboardingBasicInfoScreenContent() {
    val viewModel = remember { com.app.thinktwice.onboarding.viewmodel.OnboardingViewModel() }

    com.app.thinktwice.onboarding.OnboardingFlow(
        viewModel = viewModel,
        onOnboardingComplete = {
            navigateTo(DashboardHomeScreen)
        },
        onBackToAuth = {
            navigateBack()
        }
    )
}