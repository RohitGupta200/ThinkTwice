package com.app.thinktwice.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.thinktwice.onboarding.components.AvatarDisplay
import com.app.thinktwice.onboarding.components.AvatarSize
import com.app.thinktwice.onboarding.components.AvatarState
import com.app.thinktwice.onboarding.components.OnboardingBackground
import com.app.thinktwice.onboarding.components.OnboardingContinueButton

@Composable
fun FreshInstallScreen(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    OnboardingBackground(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                AvatarDisplay(
                    size = AvatarSize.ExtraLarge,
                    state = AvatarState.Happy,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Stop buying.\nStart saving.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                OnboardingContinueButton(
                    text = "Get Started",
                    onClick = onGetStarted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "By signing up, you are agreeing to our",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = {
                        // TODO: Open terms and privacy policy
                        uriHandler.openUri("https://example.com/terms-privacy")
                    }
                ) {
                    Text(
                        text = "terms of service and privacy policy",
                        fontSize = 12.sp,
                        color = Color.Black,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}