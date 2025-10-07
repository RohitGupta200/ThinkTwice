package com.app.thinktwice.applocking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
expect fun ActivateMonitoringButton(
    selectedApps: Set<String>,
    installedApps: List<AppInfo>,
    onActivated: () -> Unit
)

@Composable
fun ActivateMonitoringScreen(
    selectedApps: Set<String>,
    installedApps: List<AppInfo>,
    onActivated: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Activate App Monitoring",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You've selected ${selectedApps.size} apps to monitor.\nTap below to activate monitoring.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        ActivateMonitoringButton(
            selectedApps = selectedApps,
            installedApps = installedApps,
            onActivated = onActivated
        )
    }
}
