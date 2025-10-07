package com.app.thinktwice.screens

import com.app.thinktwice.navigation.Screen
import com.app.thinktwice.navigation.RootScreen
import com.app.thinktwice.navigation.SimpleScreen
import com.app.thinktwice.navigation.ParameterizedScreen
import kotlinx.serialization.Serializable

/**
 * App-specific screen definitions
 */

@Serializable
data object HomeScreen : SimpleScreen(), RootScreen

@Serializable
data object NotesScreen : SimpleScreen()

@Serializable
data object SettingsScreen : SimpleScreen()

@Serializable
data object ProfileScreen : SimpleScreen()

@Serializable
data class NoteDetailScreen(
    override val parameters: NoteDetailParams
) : ParameterizedScreen<NoteDetailParams>()

@Serializable
data class NoteDetailParams(
    val noteId: Long,
    val title: String = ""
)

@Serializable
data class UserProfileScreen(
    override val parameters: UserProfileParams
) : ParameterizedScreen<UserProfileParams>()

@Serializable
data class UserProfileParams(
    val userId: Long,
    val username: String = ""
)

@Serializable
data object LoginScreen : SimpleScreen(), RootScreen

@Serializable
data object RegisterScreen : SimpleScreen()

@Serializable
data class EditNoteScreen(
    override val parameters: EditNoteParams
) : ParameterizedScreen<EditNoteParams>()

@Serializable
data class EditNoteParams(
    val noteId: Long? = null, // null for new note
    val title: String = "",
    val content: String = ""
)

@Serializable
data object AboutScreen : SimpleScreen()

@Serializable
data class WebViewScreen(
    override val parameters: WebViewParams
) : ParameterizedScreen<WebViewParams>()

@Serializable
data class WebViewParams(
    val url: String,
    val title: String = ""
)

// Onboarding and Auth Screens
@Serializable
data object SplashScreen : SimpleScreen(), RootScreen

@Serializable
data object AuthLoginScreen : SimpleScreen(), RootScreen

@Serializable
data object FreshInstallScreen : SimpleScreen(), RootScreen

// Onboarding Flow Screens (15 screens)
@Serializable
data object OnboardingBasicInfoScreen : SimpleScreen()

@Serializable
data object OnboardingUserBehaviorsScreen : SimpleScreen()

@Serializable
data object OnboardingStatsScreen : SimpleScreen()

@Serializable
data object OnboardingSavingGoalsScreen : SimpleScreen()

@Serializable
data object OnboardingSavingGoalsDetailsScreen : SimpleScreen()

@Serializable
data object OnboardingSavingsBreakdownScreen : SimpleScreen()

@Serializable
data object OnboardingCharacterIntroScreen : SimpleScreen()

@Serializable
data object OnboardingPaydayDetailsScreen : SimpleScreen()

@Serializable
data object OnboardingNotificationsScreen : SimpleScreen()

@Serializable
data object OnboardingAppSelectionScreen : SimpleScreen()

@Serializable
data object OnboardingSelectionOverviewScreen : SimpleScreen()

@Serializable
data object OnboardingPaymentScreen : SimpleScreen()

@Serializable
data object OnboardingPaymentConfirmationScreen : SimpleScreen()

@Serializable
data object OnboardingDiscountScreen : SimpleScreen()

@Serializable
data object OnboardingSuccessScreen : SimpleScreen()

// Dashboard Screens
@Serializable
data object DashboardHomeScreen : SimpleScreen(), RootScreen

@Serializable
data object DashboardStatsScreen : SimpleScreen()

@Serializable
data object DashboardSettingsScreen : SimpleScreen()

@Serializable
data object DashboardProfileScreen : SimpleScreen()