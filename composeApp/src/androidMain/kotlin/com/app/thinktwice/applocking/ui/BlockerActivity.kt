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
import com.app.thinktwice.applocking.models.SnoozeDuration
import kotlinx.coroutines.launch

/**
 * Fullscreen blocker activity shown when user opens a restricted app
 *
 * Features:
 * - Fullscreen overlay (uses FLAG_ACTIVITY_NEW_TASK)
 * - Snooze duration selection
 * - Exit to home button
 * - Displays app name being blocked
 */
class BlockerActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
    }

    private var packageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

        android.util.Log.d("BlockerActivity", "onCreate() - Blocking package: $packageName")

        // Make activity fullscreen and prevent user from dismissing
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

        setContent {
            BlockerScreen(
                packageName = packageName ?: "Unknown App",
                onSnoozeSelected = { duration ->
                    handleSnooze(duration)
                },
                onExitToHome = {
                    exitToHome()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        android.util.Log.d("BlockerActivity", "onResume() - Activity is visible")
    }

    override fun onPause() {
        super.onPause()
        android.util.Log.d("BlockerActivity", "onPause() - Activity is being hidden")
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        android.util.Log.d("BlockerActivity", "onNewIntent() - Activity relaunched")
        setIntent(intent)
    }

    private fun handleSnooze(duration: SnoozeDuration) {
        // TODO: Call coordinator to create snooze
        // For now, just close the blocker
        finish()
    }

    private fun exitToHome() {
        // Take user to home screen
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_HOME)
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        // Disable back button to prevent bypassing blocker
        // User must either snooze or exit to home
    }
}

@Composable
fun BlockerScreen(
    packageName: String,
    onSnoozeSelected: (SnoozeDuration) -> Unit,
    onExitToHome: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedDuration by remember { mutableStateOf<SnoozeDuration?>(null) }

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
                text = "Focus Mode Active",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message
            Text(
                text = "You're trying to open a restricted app",
                fontSize = 18.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Package/App name
            Text(
                text = packageName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Snooze options
            Text(
                text = "Snooze for:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Snooze duration buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SnoozeDuration.entries.forEach { duration ->
                    SnoozeButton(
                        duration = duration,
                        isSelected = selectedDuration == duration,
                        onClick = {
                            selectedDuration = duration
                            scope.launch {
                                onSnoozeSelected(duration)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Divider with text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color(0xFFCCCCCC))
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Divider(modifier = Modifier.weight(1f), color = Color(0xFFCCCCCC))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Exit to home button
            Button(
                onClick = onExitToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53238)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Exit to Home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info text
            Text(
                text = "After snooze expires, the blocker will reappear if this app is still open.",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SnoozeButton(
    duration: SnoozeDuration,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF4CAF50) else Color(0xFF3A3D4A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = duration.label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
