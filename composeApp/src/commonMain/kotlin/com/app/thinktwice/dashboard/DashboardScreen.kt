package com.app.thinktwice.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.app.thinktwice.dashboard.components.*
import com.app.thinktwice.dashboard.screens.*

/**
 * Main dashboard container with bottom navigation
 * Contains all 4 dashboard tabs: Home, Stats, Settings, Profile
 */
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.HOME) }

    Column(modifier = modifier.fillMaxSize()) {
        // Main content area
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                DashboardTab.HOME -> DashboardHomeContent()
                DashboardTab.STATS -> DashboardStatsContent()
                DashboardTab.SETTINGS -> DashboardSettingsContent()
                DashboardTab.PROFILE -> DashboardProfileContent()
            }
        }

        // Bottom navigation
        DashboardBottomNavigation(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
    }
}