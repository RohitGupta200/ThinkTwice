package com.app.thinktwice.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.IntOffset

/**
 * Navigation transition configurations
 */
object NavigationTransitions {

    /**
     * Default animation duration
     */
    const val DEFAULT_ANIMATION_DURATION = 300

    /**
     * Fast animation duration for quick transitions
     */
    const val FAST_ANIMATION_DURATION = 150

    /**
     * Slow animation duration for emphasized transitions
     */
    const val SLOW_ANIMATION_DURATION = 500

    /**
     * Default easing for transitions
     */
    val defaultEasing = FastOutSlowInEasing

    /**
     * Slide in from right transition (for push navigation)
     */
    fun slideInFromRight(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Slide out to left transition (for push navigation)
     */
    fun slideOutToLeft(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Slide in from left transition (for pop navigation)
     */
    fun slideInFromLeft(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Slide out to right transition (for pop navigation)
     */
    fun slideOutToRight(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Fade in transition
     */
    fun fadeIn(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = androidx.compose.animation.fadeIn(
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Fade out transition
     */
    fun fadeOut(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = androidx.compose.animation.fadeOut(
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Scale in transition (growing from center)
     */
    fun scaleIn(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing,
        initialScale: Float = 0.8f
    ) = androidx.compose.animation.scaleIn(
        initialScale = initialScale,
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Scale out transition (shrinking to center)
     */
    fun scaleOut(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing,
        targetScale: Float = 0.8f
    ) = androidx.compose.animation.scaleOut(
        targetScale = targetScale,
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Slide up transition (for modal/dialog screens)
     */
    fun slideUp(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(animationDuration, easing = easing)
    )

    /**
     * Slide down transition (for modal/dialog screens)
     */
    fun slideDown(
        animationDuration: Int = DEFAULT_ANIMATION_DURATION,
        easing: Easing = defaultEasing
    ) = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(animationDuration, easing = easing)
    )
}

/**
 * Predefined transition styles
 */
enum class NavigationTransition {
    /**
     * Horizontal slide transition (iOS-style)
     */
    Slide,

    /**
     * Fade transition
     */
    Fade,

    /**
     * Scale with fade transition (Material Design-style)
     */
    ScaleFade,

    /**
     * Modal slide up transition
     */
    Modal,

    /**
     * No animation
     */
    None
}

/**
 * Gets enter transition for navigation type
 */
fun NavigationTransition.getEnterTransition(isPopping: Boolean = false): EnterTransition {
    return when (this) {
        NavigationTransition.Slide -> {
            if (isPopping) {
                NavigationTransitions.slideInFromLeft()
            } else {
                NavigationTransitions.slideInFromRight()
            }
        }
        NavigationTransition.Fade -> {
            NavigationTransitions.fadeIn()
        }
        NavigationTransition.ScaleFade -> {
            NavigationTransitions.scaleIn() + NavigationTransitions.fadeIn()
        }
        NavigationTransition.Modal -> {
            NavigationTransitions.slideUp()
        }
        NavigationTransition.None -> {
            EnterTransition.None
        }
    }
}

/**
 * Gets exit transition for navigation type
 */
fun NavigationTransition.getExitTransition(isPopping: Boolean = false): ExitTransition {
    return when (this) {
        NavigationTransition.Slide -> {
            if (isPopping) {
                NavigationTransitions.slideOutToRight()
            } else {
                NavigationTransitions.slideOutToLeft()
            }
        }
        NavigationTransition.Fade -> {
            NavigationTransitions.fadeOut()
        }
        NavigationTransition.ScaleFade -> {
            NavigationTransitions.scaleOut() + NavigationTransitions.fadeOut()
        }
        NavigationTransition.Modal -> {
            NavigationTransitions.slideDown()
        }
        NavigationTransition.None -> {
            ExitTransition.None
        }
    }
}