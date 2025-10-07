package com.app.thinktwice.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.thinktwice.navigation.NavigationScope

/**
 * Home screen with navigation options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScope.HomeScreenContent() {
    val navigationOptions = remember {
        listOf(
            NavigationOption("Notes", "View and manage your notes", NotesScreen),
            NavigationOption("Settings", "App preferences and configuration", SettingsScreen),
            NavigationOption("Profile", "User profile and account", ProfileScreen),
            NavigationOption("About", "About this application", AboutScreen),
            NavigationOption(
                "Sample Note Detail",
                "View a sample note detail",
                NoteDetailScreen(NoteDetailParams(noteId = 1, title = "Sample Note"))
            ),
            NavigationOption(
                "Edit New Note",
                "Create a new note",
                EditNoteScreen(EditNoteParams())
            ),
            NavigationOption(
                "User Profile",
                "View user profile",
                UserProfileScreen(UserProfileParams(userId = 1, username = "sampleuser"))
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ThinkTwice Home",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Welcome to ThinkTwice",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Navigate through the app using the options below. This demonstrates the stack-based navigation system.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    "Navigation Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(navigationOptions) { option ->
                NavigationCard(option = option) {
                    navigateTo(option.screen)
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
                        Text(
                            "Navigation Stack Info",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Stack depth: ${navigator.stackDepth}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Can navigate back: ${if (canNavigateBack) "Yes" else "No"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Current screen: ${currentScreen.screenId}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationCard(
    option: NavigationOption,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                option.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                option.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class NavigationOption(
    val title: String,
    val description: String,
    val screen: com.app.thinktwice.navigation.Screen
)