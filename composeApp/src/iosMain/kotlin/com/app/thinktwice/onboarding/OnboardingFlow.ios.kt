package com.app.thinktwice.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.app.thinktwice.applocking.platform.AppPlatformHelper

@Composable
actual fun rememberAppPlatformHelper(): AppPlatformHelper {
    return remember { AppPlatformHelper() }
}
