package com.app.thinktwice.applocking

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.thinktwice.applocking.platform.AppInfo

@Composable
actual fun ActivateMonitoringButton(
    selectedApps: Set<String>,
    installedApps: List<AppInfo>,
    onActivated: () -> Unit
) {
    Button(
        onClick = onActivated,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
    ) {
        Text("Activate Monitoring (iOS - TODO)")
    }
}
