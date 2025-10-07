package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.*
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST

interface ReferralApi {

    @POST("api/v1/referral/codes")
    suspend fun createReferralCode(
        @Header("Authorization") authorization: String,
        @Body request: CreateReferralCodeRequest
    ): ReferralCodeResponse

    @POST("api/v1/referral/validate")
    suspend fun validateReferralCode(
        @Body request: ValidateReferralCodeRequest
    ): ValidateReferralCodeResponse

    @POST("api/v1/referral/use")
    suspend fun useReferralCode(
        @Body request: UseReferralCodeRequest
    ): UseReferralCodeResponse

    @GET("api/v1/referral/stats")
    suspend fun getReferralStats(
        @Header("Authorization") authorization: String
    ): ReferralStatsResponse

    @POST("api/v1/referral/rewards/redeem")
    suspend fun redeemReward(
        @Header("Authorization") authorization: String,
        @Body request: RedeemRewardRequest
    ): RedeemRewardResponse
}
