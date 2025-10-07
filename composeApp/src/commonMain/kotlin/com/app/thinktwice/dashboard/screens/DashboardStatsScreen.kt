package com.app.thinktwice.dashboard.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.thinktwice.dashboard.components.*
import com.app.thinktwice.dashboard.models.VisitedApp

/**
 * Dashboard Stats screen content
 * Shows user statistics, character health chart, and performance metrics
 */
@Composable
fun DashboardStatsContent(
    modifier: Modifier = Modifier
) {
    // Mock data
    val dates = (17..29).toList()
    val selectedDate = 25
    val avgDailySaving = 150.00
    val avgDailySpending = 50.00
    val avgShoppingAttempts = 1.7
    val healthyStreak = 15

    val temptationsBeat = listOf(
        VisitedApp("Amazon", 5, true),
        VisitedApp("Shein", 3, false),
        VisitedApp("Etsy", 1, false)
    )

    val appsDrawnBack = listOf(
        VisitedApp("Amazon", 5, true),
        VisitedApp("Shein", 3, false),
        VisitedApp("Etsy", 1, false)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Header with back button
        item {
            StatsHeader()
        }

        // Date picker
        item {
            DatePickerRow(
                dates = dates,
                selectedDate = selectedDate,
                onDateSelected = { /* TODO */ }
            )
        }

        // Character health chart
        item {
            CharacterHealthSection()
        }

        // Performance metrics
        item {
            PerformanceMetricsSection(
                avgDailySaving = avgDailySaving,
                avgDailySpending = avgDailySpending,
                avgShoppingAttempts = avgShoppingAttempts,
                healthyStreak = healthyStreak
            )
        }

        // Savings progress
        item {
            SavingsProgressSection()
        }

        // Temptations and apps sections
        item {
            TemptationsBeatSection(apps = temptationsBeat)
        }

        item {
            AppsDrawnBackSection(apps = appsDrawnBack)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatsHeader(
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
            text = "Your Stats",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DatePickerRow(
    dates: List<Int>,
    selectedDate: Int,
    onDateSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            DateChip(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateChip(
    date: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> Color(0xFF4CAF50) // Green for selected
        date == 18 -> Color(0xFFFF5252) // Red for day 18
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> Color.White
        date == 18 -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CharacterHealthSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Character Health",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder for chart - TODO: Implement actual chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Color.LightGray.copy(alpha = 0.1f),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Character Health Chart\n(Line chart showing health over time)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun PerformanceMetricsSection(
    avgDailySaving: Double,
    avgDailySpending: Double,
    avgShoppingAttempts: Double,
    healthyStreak: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF454554) // Dark background from design
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Here's how you've been doing",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Grid of metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    label = "Avg. Daily Saving",
                    value = "$${avgDailySaving.toInt()}",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Avg. Daily Spending",
                    value = "$${avgDailySpending.toInt()}",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    label = "Avg. Shopping\nAttempts",
                    value = avgShoppingAttempts.toString(),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    label = "Healthy Streak",
                    value = "$healthyStreak days",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SavingsProgressSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SavingsProgressBar(
            currentAmount = 1000.00,
            targetAmount = 5000.00,
            daysRemaining = 27,
            goalDescription = "Bali trip"
        )
    }
}

@Composable
private fun TemptationsBeatSection(
    apps: List<VisitedApp>,
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Temptations You Beat",
        modifier = modifier,
        action = {
            IconButton(onClick = { /* TODO: Show info */ }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    ) {
        apps.forEach { app ->
            VisitedAppItem(app = app)
            if (app != apps.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "View more",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { /* TODO: View more */ }
        )
    }
}

@Composable
private fun AppsDrawnBackSection(
    apps: List<VisitedApp>,
    modifier: Modifier = Modifier
) {
    DashboardSectionCard(
        title = "Apps that drew you back",
        modifier = modifier,
        action = {
            IconButton(onClick = { /* TODO: Show info */ }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    ) {
        apps.forEach { app ->
            VisitedAppItem(app = app)
            if (app != apps.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "View more",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { /* TODO: View more */ }
        )
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