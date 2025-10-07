package com.app.thinktwice.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.applocking.models.RestrictedApp
import com.app.thinktwice.applocking.platform.AppInfo

/**
 * Dashboard screen for managing restricted apps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestrictedAppsScreen(
    restrictedApps: List<RestrictedApp>,
    isMonitoring: Boolean,
    hasPermissions: Boolean,
    onAddAppsClick: () -> Unit,
    onToggleMonitoring: () -> Unit,
    onToggleAppRestriction: (Long, Boolean) -> Unit,
    onRemoveApp: (Long) -> Unit,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Top bar
        TopAppBar(
            title = {
                Text(
                    text = "App Restrictions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // Permission warning
        if (!hasPermissions) {
            PermissionWarningCard(onRequestPermissions)
        }

        // Monitoring status card
        MonitoringStatusCard(
            isMonitoring = isMonitoring,
            restrictedAppsCount = restrictedApps.count { it.isEnabled },
            onToggleMonitoring = onToggleMonitoring
        )

        // Restricted apps list
        if (restrictedApps.isEmpty()) {
            EmptyStateView(onAddAppsClick)
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(restrictedApps) { app ->
                    RestrictedAppCard(
                        app = app,
                        onToggle = { onToggleAppRestriction(app.id, !app.isEnabled) },
                        onRemove = { onRemoveApp(app.id) }
                    )
                }
            }
        }

        // Add apps button (FAB style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                onClick = onAddAppsClick,
                containerColor = Color(0xFF3A3D4A),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add apps",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun PermissionWarningCard(onRequestPermissions: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3CD)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFF856404),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Permissions Required",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF856404)
                )
                Text(
                    text = "Grant permissions to enable monitoring",
                    fontSize = 14.sp,
                    color = Color(0xFF856404)
                )
            }

            Button(
                onClick = onRequestPermissions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF856404)
                )
            ) {
                Text("Grant", color = Color.White)
            }
        }
    }
}

@Composable
fun MonitoringStatusCard(
    isMonitoring: Boolean,
    restrictedAppsCount: Int,
    onToggleMonitoring: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMonitoring) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isMonitoring) "Monitoring Active" else "Monitoring Disabled",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMonitoring) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Text(
                    text = "$restrictedAppsCount apps restricted",
                    fontSize = 14.sp,
                    color = if (isMonitoring) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }

            Switch(
                checked = isMonitoring,
                onCheckedChange = { onToggleMonitoring() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4CAF50),
                    checkedTrackColor = Color(0xFF81C784)
                )
            )
        }
    }
}

@Composable
fun RestrictedAppCard(
    app: RestrictedApp,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3A3D4A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // App name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = if (app.isEnabled) "Enabled" else "Disabled",
                    fontSize = 14.sp,
                    color = if (app.isEnabled) Color(0xFF4CAF50) else Color(0xFF666666)
                )
            }

            // Toggle switch
            Switch(
                checked = app.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4CAF50),
                    checkedTrackColor = Color(0xFF81C784)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Delete button
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove app",
                    tint = Color(0xFFE53238)
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove App") },
            text = { Text("Remove ${app.appName} from restricted apps?") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove()
                    showDeleteDialog = false
                }) {
                    Text("Remove", color = Color(0xFFE53238))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyStateView(onAddAppsClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Restricted Apps",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Add apps to restrict and help yourself stay focused",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddAppsClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A3D4A)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Apps",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
