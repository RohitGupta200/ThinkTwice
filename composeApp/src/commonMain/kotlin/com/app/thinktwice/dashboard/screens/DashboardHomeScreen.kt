package com.app.thinktwice.dashboard.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.thinktwice.dashboard.components.*
import com.app.thinktwice.dashboard.models.VisitedApp
import org.jetbrains.compose.resources.painterResource
import thinktwice.composeapp.generated.resources.Res
import thinktwice.composeapp.generated.resources.avatar

/**
 * Dashboard Home screen content
 * Shows avatar, progress, savings goal, and frequently visited apps
 */
@Composable
fun DashboardHomeContent(
    modifier: Modifier = Modifier
) {
    // State for customization bottom sheet
    var showCustomizationSheet by remember { mutableStateOf(false) }

    // Mock data - will be replaced with real data
    val characterProgress = 0.2f // 20% progress (1000/5000)
    val currentSavings = 1000.00
    val targetSavings = 5000.00
    val daysRemaining = 27
    val characterName = "Mini Kunal"
    val characterMood = "feels great!"
    val characterDescription = "She's got plenty energy and is feeling fantastic"

    val frequentlyVisited = listOf(
        VisitedApp("Amazon", 5, isHighFrequency = true),
        VisitedApp("Shein", 3, isHighFrequency = false),
        VisitedApp("Etsy", 1, isHighFrequency = false)
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Avatar section with arc progress
            item {
                AvatarSection(
                    progress = characterProgress,
                    characterName = characterName,
                    characterMood = characterMood,
                    characterDescription = characterDescription,
                    onCustomizeClick = { showCustomizationSheet = true }
                )
            }

        // Savings goal section
        item {
            SavingsGoalSection(
                currentAmount = currentSavings,
                targetAmount = targetSavings,
                daysRemaining = daysRemaining,
                goalDescription = "Bali trip"
            )
        }

        // Frequently visited apps
        item {
            FrequentlyVisitedSection(
                apps = frequentlyVisited
            )
        }

        // Alert section
        item {
            AlertSection(
                message = "Whoa! Your Amazon visits are a bit too frequent today!"
            )
        }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Customization bottom sheet
        CustomizationBottomSheet(
            showBottomSheet = showCustomizationSheet,
            onDismiss = { showCustomizationSheet = false }
        )
    }
}

@Composable
private fun AvatarSection(
    progress: Float,
    characterName: String,
    characterMood: String,
    characterDescription: String,
    onCustomizeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Info icon (top right in design)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { /* TODO: Show info */ }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Avatar with arc progress
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            ArcProgressBar(progress = progress)

            // Avatar image
            Image(
                painter = painterResource(Res.drawable.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Customize button
        Button(
            onClick = onCustomizeClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9671)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Customize 👕")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Character status
        Text(
            text = "$characterName $characterMood",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = characterDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SavingsGoalSection(
    currentAmount: Double,
    targetAmount: Double,
    daysRemaining: Int,
    goalDescription: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SavingsProgressBar(
            currentAmount = currentAmount,
            targetAmount = targetAmount,
            daysRemaining = daysRemaining,
            goalDescription = goalDescription
        )
    }
}

@Composable
private fun FrequentlyVisitedSection(
    apps: List<VisitedApp>,
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Frequently Visited",
        modifier = modifier,
        action = {
            Text(
                text = "View more",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* TODO: View more */ }
            )
        }
    ) {
        apps.forEach { app ->
            VisitedAppItem(app = app)
            if (app != apps.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun VisitedAppItem(
    app: VisitedApp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodyMedium
        )

        // Visit badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (app.isHighFrequency) Color(0xFFFF5252) else Color(0xFF757575)
        ) {
            Text(
                text = "${app.visitCount} visits",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AlertSection(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFFFF3E0) // Light orange background
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(12.dp)
        )
    }
}