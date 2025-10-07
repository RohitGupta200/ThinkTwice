package com.app.thinktwice

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.app.thinktwice.navigation.AppNavigationRouter
import com.app.thinktwice.navigation.NavigationTransition
import com.app.thinktwice.screens.SplashScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigationRouter(
            modifier = Modifier.fillMaxSize(),
            initialScreen = SplashScreen, // Start with splash screen for onboarding
            transitionType = NavigationTransition.Slide
        )
    }
}