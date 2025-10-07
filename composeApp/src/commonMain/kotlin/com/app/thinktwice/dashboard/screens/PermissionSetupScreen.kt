package com.app.thinktwice.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Permission setup screen for app locking feature
 * Guides user through granting required permissions
 */
@Composable
fun PermissionSetupScreen(
    permissions: List<PermissionInfo>,
    onRequestPermission: (PermissionType) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allGranted = permissions.all { it.isGranted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF3A3D4A),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Setup Permissions",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Grant the following permissions to enable app monitoring",
                fontSize = 16.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        // Permission list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(permissions) { permission ->
                PermissionCard(
                    permission = permission,
                    onRequestClick = { onRequestPermission(permission.type) }
                )
            }
        }

        // Continue button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = onContinue,
                enabled = allGranted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A3D4A),
                    disabledContainerColor = Color(0xFFCCCCCC)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (allGranted) "Continue" else "Grant All Permissions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    permission: PermissionInfo,
    onRequestClick: () -> Unit
) {
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
            // Permission icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (permission.isGranted) Color(0xFFE8F5E9) else Color(0xFFFFF3CD),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = permission.icon,
                    contentDescription = null,
                    tint = if (permission.isGranted) Color(0xFF4CAF50) else Color(0xFF856404),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Permission info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permission.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = permission.description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
                if (permission.isRequired) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Required",
                        fontSize = 12.sp,
                        color = Color(0xFFE53238),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Status / Action
            if (permission.isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Granted",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Button(
                    onClick = onRequestClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3A3D4A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Grant", fontSize = 14.sp)
                }
            }
        }
    }
}

/**
 * Data class for permission info
 */
data class PermissionInfo(
    val type: PermissionType,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isRequired: Boolean,
    val isGranted: Boolean
)

/**
 * Enum for permission types
 */
enum class PermissionType {
    USAGE_STATS,      // Android: Usage Access
    NOTIFICATIONS,    // Android: Notification Permission
    ACCESSIBILITY,    // Android: Accessibility (optional)
    OVERLAY,          // Android: Draw over other apps (optional)
    SCREEN_TIME       // iOS: Screen Time authorization
}
