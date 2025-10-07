package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.CompleteOnboardingRequest
import com.app.thinktwice.network.dto.CompleteOnboardingResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.PUT

interface ProfileApi {

    @PUT("profile/onboarding")
    suspend fun completeOnboarding(
        @Header("Authorization") authorization: String,
        @Body request: CompleteOnboardingRequest
    ): CompleteOnboardingResponse
}
