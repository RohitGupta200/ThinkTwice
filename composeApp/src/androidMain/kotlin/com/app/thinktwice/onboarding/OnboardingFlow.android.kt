package com.app.thinktwice.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.app.thinktwice.applocking.platform.AppPlatformHelper

@Composable
actual fun rememberAppPlatformHelper(): AppPlatformHelper {
    val context = LocalContext.current
    return remember { AppPlatformHelper(context) }
}
