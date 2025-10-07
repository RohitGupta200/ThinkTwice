package com.app.thinktwice.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import thinktwice.composeapp.generated.resources.Res
import thinktwice.composeapp.generated.resources.avatar

enum class AvatarSize(val dp: Dp) {
    Small(60.dp),
    Medium(100.dp),
    Large(150.dp),
    ExtraLarge(200.dp)
}

enum class AvatarState {
    Happy,
    Neutral,
    Excited,
    Wealthy
}

@Composable
fun AvatarDisplay(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    state: AvatarState = AvatarState.Happy,
    contentDescription: String = "Mini Kunal Avatar"
) {
    val isInPreview = LocalInspectionMode.current

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isInPreview) {
            // Preview placeholder
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(OnboardingColors.GradientTopLeft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👧",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
            }
        } else {
            Image(
                painter = painterResource(Res.drawable.avatar),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(size.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun AvatarWithBackground(
    modifier: Modifier = Modifier,
    size: AvatarSize = AvatarSize.Medium,
    state: AvatarState = AvatarState.Happy,
    showFullBody: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (showFullBody) {
            // For full body avatar display (used in splash and character intro)
            AvatarDisplay(
                size = size,
                state = state,
                modifier = Modifier
            )
        } else {
            // For head/bust display
            AvatarDisplay(
                size = size,
                state = state
            )
        }
    }
}