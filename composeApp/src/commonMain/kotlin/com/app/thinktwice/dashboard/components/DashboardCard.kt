package com.app.thinktwice.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Reusable dashboard card component with consistent styling
 * Used throughout the dashboard for sections like frequently visited, stats, etc.
 */
@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    elevation: Int = 2,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(
            elevation = elevation.dp,
            shape = RoundedCornerShape(12.dp),
            ambientColor = Color.Black.copy(alpha = 0.1f),
            spotColor = Color.Black.copy(alpha = 0.1f)
        )
        .clip(RoundedCornerShape(12.dp))
        .background(backgroundColor)
        .let { baseModifier ->
            if (onClick != null) {
                baseModifier.clickable { onClick() }
            } else {
                baseModifier
            }
        }

    Column(
        modifier = cardModifier.padding(padding),
        content = content
    )
}

/**
 * Section card for grouping related content
 * Used for sections like "Frequently Visited", "Your goals", etc.
 */
@Composable
fun DashboardSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFF9F4E8), // Light beige color from designs
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    DashboardCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        padding = PaddingValues(0.dp)
    ) {
        // Title row with optional action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = titleColor
            )
            action?.invoke()
        }

        // Content with consistent padding
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Metric card for displaying statistics
 * Used in stats screen for metrics like "Avg. Daily Saving"
 */
@Composable
fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    labelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    DashboardCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        padding = PaddingValues(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = valueColor
        )
    }
}