package com.app.thinktwice.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Horizontal savings progress bar component
 * Shows current savings progress towards goal with green/gray sections
 */
@Composable
fun SavingsProgressBar(
    currentAmount: Double,
    targetAmount: Double,
    daysRemaining: Int,
    goalDescription: String,
    modifier: Modifier = Modifier,
    progressColor: Color = Color(0xFF4CAF50), // Green
    backgroundColor: Color = Color(0xFFE0E0E0) // Light gray
) {
    val progress = (currentAmount / targetAmount).coerceIn(0.0, 1.0).toFloat()

    Column(modifier = modifier) {
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Amount display
        Text(
            text = "$${currentAmount.toInt()}/$${targetAmount.toInt()}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Goal description with days remaining
        Text(
            text = "At this rate, your $goalDescription is just $daysRemaining days away!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

/**
 * Compact savings progress component for smaller spaces
 */
@Composable
fun CompactSavingsProgress(
    currentAmount: Double,
    targetAmount: Double,
    modifier: Modifier = Modifier,
    progressColor: Color = Color(0xFF4CAF50),
    backgroundColor: Color = Color(0xFFE0E0E0)
) {
    val progress = (currentAmount / targetAmount).coerceIn(0.0, 1.0).toFloat()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(progressColor)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$${currentAmount.toInt()}/$${targetAmount.toInt()}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}