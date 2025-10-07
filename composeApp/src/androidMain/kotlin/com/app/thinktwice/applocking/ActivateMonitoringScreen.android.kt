package com.app.thinktwice.applocking

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.applocking.platform.AppInfo
import kotlinx.coroutines.launch

@Composable
actual fun ActivateMonitoringButton(
    selectedApps: Set<String>,
    installedApps: List<AppInfo>,
    onActivated: () -> Unit
) {
    val startService = com.app.thinktwice.onboarding.screens.rememberStartServiceFunction()
    val scope = rememberCoroutineScope()
    var isActivating by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (!isActivating) {
                isActivating = true
                scope.launch {
                    startService(selectedApps, installedApps)
                    onActivated()
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = !isActivating
    ) {
        Text(
            text = if (isActivating) "Activating..." else "Activate Monitoring",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
