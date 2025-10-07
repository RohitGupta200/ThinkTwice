package com.app.thinktwice.applocking.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.applocking.models.ResponseType
import kotlinx.coroutines.launch

/**
 * Follow-up activity shown after user closes a restricted app
 * Asks: "Did you complete the intended action?"
 */
class FollowupActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_SESSION_START = "session_start"
        const val EXTRA_SESSION_END = "session_end"
    }

    private var packageName: String? = null
    private var sessionStart: Long = 0
    private var sessionEnd: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        sessionStart = intent.getLongExtra(EXTRA_SESSION_START, 0)
        sessionEnd = intent.getLongExtra(EXTRA_SESSION_END, System.currentTimeMillis())

        setContent {
            FollowupScreen(
                packageName = packageName ?: "Unknown App",
                sessionDurationSeconds = ((sessionEnd - sessionStart) / 1000).toInt(),
                onResponseSelected = { response ->
                    handleResponse(response)
                }
            )
        }
    }

    private fun handleResponse(response: ResponseType) {
        // TODO: Call coordinator to record response
        finish()
    }
}

@Composable
fun FollowupScreen(
    packageName: String,
    sessionDurationSeconds: Int,
    onResponseSelected: (ResponseType) -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Quick Check",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question
            Text(
                text = "Did you complete the intended action?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = packageName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Session: ${formatDuration(sessionDurationSeconds)}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Response buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResponseButton(
                    text = "Yes",
                    responseType = ResponseType.YES,
                    backgroundColor = Color(0xFF4CAF50),
                    onClick = {
                        scope.launch {
                            onResponseSelected(ResponseType.YES)
                        }
                    }
                )

                ResponseButton(
                    text = "No",
                    responseType = ResponseType.NO,
                    backgroundColor = Color(0xFFE53238),
                    onClick = {
                        scope.launch {
                            onResponseSelected(ResponseType.NO)
                        }
                    }
                )

                ResponseButton(
                    text = "Skip",
                    responseType = ResponseType.SKIP,
                    backgroundColor = Color(0xFF666666),
                    onClick = {
                        scope.launch {
                            onResponseSelected(ResponseType.SKIP)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Helper text
            Text(
                text = "Your response helps improve your app usage insights",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ResponseButton(
    text: String,
    responseType: ResponseType,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return when {
        minutes == 0 -> "${remainingSeconds}s"
        remainingSeconds == 0 -> "${minutes}m"
        else -> "${minutes}m ${remainingSeconds}s"
    }
}
