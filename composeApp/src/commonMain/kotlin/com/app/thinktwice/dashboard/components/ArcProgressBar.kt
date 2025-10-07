package com.app.thinktwice.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * Arc progress bar component that displays progress as a colored arc
 * Used above the avatar in the dashboard design
 */
@Composable
fun ArcProgressBar(
    progress: Float, // 0.0 to 1.0
    modifier: Modifier = Modifier,
    strokeWidth: Float = 8f,
    size: Float = 200f,
    startAngle: Float = 140f, // Start angle for the arc
    sweepAngle: Float = 260f, // Total angle sweep
    progressColor: Color = getProgressColor(progress),
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.3f)
) {
    Canvas(
        modifier = modifier.size(size.dp)
    ) {
        val canvasSize = this.size
        val radius = (canvasSize.minDimension - strokeWidth) / 2
        val center = Offset(canvasSize.width / 2, canvasSize.height / 2)

        // Calculate the arc bounds
        val arcSize = Size(radius * 2, radius * 2)
        val topLeft = Offset(
            center.x - radius,
            center.y - radius
        )

        // Draw background arc
        drawArc(
            color = backgroundColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        // Draw progress arc
        val progressSweep = sweepAngle * progress
        if (progressSweep > 0) {
            drawArc(
                color = progressColor,
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

/**
 * Get color based on progress value
 * TODO: Implement different color schemes for different progress levels as requested
 */
private fun getProgressColor(progress: Float): Color {
    return when {
        progress >= 0.8f -> Color(0xFF4CAF50) // Green for high progress
        progress >= 0.5f -> Color(0xFF66BB6A) // Light green for medium progress
        progress >= 0.3f -> Color(0xFFFFA726) // Orange for low progress
        else -> Color(0xFFEF5350) // Red for very low progress
    }
}

/**
 * Wrapper component that centers the arc progress bar with content inside
 */
@Composable
fun ArcProgressBarWithContent(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        ArcProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxSize()
        )
        content()
    }
}