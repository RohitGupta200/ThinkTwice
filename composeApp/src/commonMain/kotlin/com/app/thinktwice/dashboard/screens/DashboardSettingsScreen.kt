package com.app.thinktwice.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import com.app.thinktwice.dashboard.components.*

/**
 * Dashboard Settings screen content
 * Shows blocked apps, support options, legal information, and feedback
 */
@Composable
fun DashboardSettingsContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Header
        item {
            SettingsHeader()
        }

        // Blocked Apps section
        item {
            BlockedAppsSection()
        }

        // Support section
        item {
            SupportSection()
        }

        // Legal section
        item {
            LegalSection()
        }

        // Feedback section
        item {
            FeedbackSection()
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* TODO: Navigate back */ }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }

        Text(
            text = "Rules and Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BlockedAppsSection(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF454554) // Dark background from design
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
                    text = "Blocked Apps",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // App icons row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Placeholder app icons - TODO: Replace with actual app icons
                    repeat(4) { index ->
                        val colors = listOf(
                            Color(0xFFFF9800), // Orange (Amazon)
                            Color(0xFF000000), // Black (Shein)
                            Color(0xFFFF5722), // Red (Etsy)
                            Color(0xFF2196F3)  // Blue (eBay)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors[index])
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View blocked apps",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun SupportSection(
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Support",
        modifier = modifier
    ) {
        SettingsItem(
            title = "View FAQs",
            onClick = { /* TODO: Navigate to FAQs */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItem(
            title = "Contact Us",
            onClick = { /* TODO: Navigate to contact */ }
        )
    }
}

@Composable
private fun LegalSection(
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Legal",
        modifier = modifier
    ) {
        SettingsItem(
            title = "Terms and conditions",
            onClick = { /* TODO: Navigate to terms */ }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SettingsItem(
            title = "Privacy Policy",
            onClick = { /* TODO: Navigate to privacy */ }
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate to $title"
            )
        }
    }
}

@Composable
private fun FeedbackSection(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF8E1) // Light yellow background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Star rating
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) {
                    Text(
                        text = "⭐",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You're among the 10,000 others that have\ncollectively saved over $87,000.00 for their trips!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Let us know about your experience!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Open feedback */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF454554)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tell us how you feel!",
                    color = Color.White
                )
            }
        }
    }
}