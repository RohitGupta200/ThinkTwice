package com.app.thinktwice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.app.thinktwice.screens.*
import com.app.thinktwice.dashboard.DashboardScreen

/**
 * Main navigation router that handles screen composition based on the current screen
 */
@Composable
fun AppNavigationRouter(
    modifier: Modifier = Modifier,
    initialScreen: Screen = HomeScreen,
    transitionType: NavigationTransition = NavigationTransition.Slide
) {
    NavigationHost(
        initialScreen = initialScreen,
        modifier = modifier,
        transitionType = transitionType
    ) { screen ->
        when (screen) {
            // Main App Screens
            is HomeScreen -> HomeScreenContent()
            is NotesScreen -> NotesScreenContent()
            is SettingsScreen -> SettingsScreenContent()
            is ProfileScreen -> ProfileScreenContent()
            is AboutScreen -> AboutScreenContent()

            // Dashboard Screens
            is DashboardHomeScreen -> DashboardScreen()

            // Parameterized Screens
            is NoteDetailScreen -> NoteDetailScreenContent(screen.parameters)
            is EditNoteScreen -> EditNoteScreenContent(screen.parameters)
            is UserProfileScreen -> UserProfileScreenContent(screen.parameters)
            is WebViewScreen -> WebViewScreenContent(screen.parameters)

            // Auth & Onboarding Screens
            is SplashScreen -> SplashScreenContent()
            is AuthLoginScreen -> AuthLoginScreenContent()
            is FreshInstallScreen -> FreshInstallScreenContent()
            is OnboardingBasicInfoScreen -> OnboardingBasicInfoScreenContent()

            // Legacy Login/Register (keeping for compatibility)
            is LoginScreen -> LoginScreenContent()
            is RegisterScreen -> RegisterScreenContent()

            // All other onboarding screens are handled by the OnboardingFlow
            is OnboardingUserBehaviorsScreen,
            is OnboardingStatsScreen,
            is OnboardingSavingGoalsScreen,
            is OnboardingSavingGoalsDetailsScreen,
            is OnboardingSavingsBreakdownScreen,
            is OnboardingCharacterIntroScreen,
            is OnboardingPaydayDetailsScreen,
            is OnboardingNotificationsScreen,
            is OnboardingAppSelectionScreen,
            is OnboardingSelectionOverviewScreen,
            is OnboardingPaymentScreen,
            is OnboardingPaymentConfirmationScreen,
            is OnboardingDiscountScreen,
            is OnboardingSuccessScreen -> {
                // These are handled by the OnboardingFlow, redirect to BasicInfo
                OnboardingBasicInfoScreenContent()
            }

            else -> UnknownScreenContent(screen)
        }
    }
}

/**
 * Login screen placeholder
 */
@Composable
private fun NavigationScope.LoginScreenContent() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Card {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text(
                    "Login Screen",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                androidx.compose.material3.Button(
                    onClick = { clearAndNavigateTo(HomeScreen) }
                ) {
                    androidx.compose.material3.Text("Login & Go to Home")
                }
            }
        }
    }
}

/**
 * Register screen placeholder
 */
@Composable
private fun NavigationScope.RegisterScreenContent() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Card {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text(
                    "Register Screen",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                androidx.compose.material3.Button(
                    onClick = { navigateBack() }
                ) {
                    androidx.compose.material3.Text("Go Back")
                }
            }
        }
    }
}

/**
 * Fallback for unknown screen types
 */
@Composable
private fun NavigationScope.UnknownScreenContent(screen: Screen) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Card {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text(
                    "Unknown Screen",
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                androidx.compose.material3.Text(
                    "Screen: ${screen::class.simpleName}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
                androidx.compose.material3.Button(
                    onClick = { navigateBack() }
                ) {
                    androidx.compose.material3.Text("Go Back")
                }
            }
        }
    }
}