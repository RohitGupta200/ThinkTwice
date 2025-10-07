package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.applocking.platform.AppInfo
import com.app.thinktwice.applocking.platform.AppPlatformHelper
import com.app.thinktwice.onboarding.components.AppSelectionBottomSheet
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton
import com.app.thinktwice.onboarding.components.OnboardingTopBar
import com.app.thinktwice.onboarding.components.PermissionRequestDialog
import kotlinx.coroutines.launch

@Composable
expect fun StartMonitoringService(selectedApps: Set<String>, installedApps: List<AppInfo>)

expect suspend fun StartMonitoringServiceSuspend(selectedApps: Set<String>, installedApps: List<AppInfo>)

@Composable
expect fun rememberSaveAppsFunction(): suspend (Set<String>, List<AppInfo>) -> Unit

@Composable
expect fun rememberLoadAppsFunction(): suspend () -> Set<String>

@Composable
fun AppSelectionScreen(
    platformHelper: AppPlatformHelper,
    selectedApps: Set<String>,
    onAppsChange: (Set<String>) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showAppSelectionSheet by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val saveApps = rememberSaveAppsFunction()
    val loadApps = rememberLoadAppsFunction()

    // Check permission on launch
    LaunchedEffect(Unit) {
        hasPermission = platformHelper.hasUsageStatsPermission()
    }

    // Load saved apps from database on launch
    LaunchedEffect(Unit) {
        val savedApps = loadApps()
        if (savedApps.isNotEmpty() && savedApps != selectedApps) {
            onAppsChange(savedApps)
        }
    }

    // Load installed apps if permission granted and we have selected apps
    LaunchedEffect(hasPermission, selectedApps) {
        if (hasPermission && selectedApps.isNotEmpty() && installedApps.isEmpty()) {
            isLoading = true
            installedApps = platformHelper.getInstalledApps()
            isLoading = false
        }
    }

    OnboardingBackground(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OnboardingTopBar(
                currentStep = 10,
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Now lets choose some apps",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose the apps you want to block to help you achieve your goals",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Large Avatar
                AvatarDisplay(
                    size = AvatarSize.Large,
                    state = AvatarState.Happy
                )

                Spacer(modifier = Modifier.height(48.dp))

                // App Selection Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF3A3D4A),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            scope.launch {
                                if (!hasPermission) {
                                    showPermissionDialog = true
                                } else {
                                    isLoading = true
                                    installedApps = platformHelper.getInstalledApps()
                                    isLoading = false
                                    showAppSelectionSheet = true
                                }
                            }
                        }
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Choose your apps",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // App Icons Row
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            if (isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color(0xFF1A1A1A)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        if (selectedApps.isEmpty()) {
                                            // Sample app icons when no apps selected
                                            AppIcon("🛒", Color(0xFFFF9900))
                                            AppIcon("S", Color(0xFF000000))
                                            AppIcon("E", Color(0xFFF56400))
                                            AppIcon("e", Color(0xFFE53238))
                                        } else {
                                            // Show first few selected apps
                                            selectedApps.take(4).forEach { packageName ->
                                                val app = installedApps.find { it.packageName == packageName }
                                                AppIcon(
                                                    text = app?.appName?.firstOrNull()?.uppercase() ?: "?",
                                                    backgroundColor = Color(0xFF3A3D4A)
                                                )
                                            }
                                            if (selectedApps.size > 4) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .background(
                                                            color = Color(0xFFE0E0E0),
                                                            shape = RoundedCornerShape(12.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "+${selectedApps.size - 4}",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF666666)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = "›",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A1A)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "You can modify these at any point",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 24.dp)
            ) {
                OnboardingContinueButton(
                    text = "Continue",
                    onClick = onContinueClick
                )
            }
        }
    }

    // Permission Request Dialog
    if (showPermissionDialog) {
        PermissionRequestDialog(
            onGrantPermission = {
                scope.launch {
                    platformHelper.requestUsageStatsPermission()
                    showPermissionDialog = false
                    // Re-check permission after a delay (user might grant it)
                    kotlinx.coroutines.delay(1000)
                    hasPermission = platformHelper.hasUsageStatsPermission()
                }
            },
            onDismiss = {
                showPermissionDialog = false
            }
        )
    }

    // App Selection Bottom Sheet
    if (showAppSelectionSheet) {
        AppSelectionBottomSheet(
            apps = installedApps,
            selectedApps = selectedApps,
            onAppsSelected = { newSelection ->
                onAppsChange(newSelection)
                // Save immediately to database
                scope.launch {
                    saveApps(newSelection, installedApps)
                }
            },
            onDismiss = {
                showAppSelectionSheet = false
            }
        )
    }
}

@Composable
private fun AppIcon(
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (backgroundColor == Color(0xFF000000)) Color.White else Color.White
        )
    }
}