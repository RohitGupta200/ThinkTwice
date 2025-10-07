package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiService
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.dto.*
import com.app.thinktwice.network.error.ErrorHandler

class ReferralRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) {

    /**
     * Create a new referral code
     */
    suspend fun createReferralCode(
        userId: Long,
        rewardType: String,
        rewardValue: Double? = null,
        rewardDescription: String,
        maxUses: Int,
        expiresInDays: Int? = null
    ): Result<ReferralCodeResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = CreateReferralCodeRequest(
                reward_type = rewardType,
                reward_value = rewardValue,
                reward_description = rewardDescription,
                max_uses = maxUses,
                expires_in_days = expiresInDays
            )
            apiService.referralApi.createReferralCode(token, request)
        }
    }

    /**
     * Validate a referral code
     */
    suspend fun validateReferralCode(
        referralCode: String
    ): Result<ValidateReferralCodeResponse> {
        return ErrorHandler.safeApiCall {
            val request = ValidateReferralCodeRequest(referral_code = referralCode)
            apiService.referralApi.validateReferralCode(request)
        }
    }

    /**
     * Use a referral code
     */
    suspend fun useReferralCode(
        referralCode: String
    ): Result<UseReferralCodeResponse> {
        return ErrorHandler.safeApiCall {
            val request = UseReferralCodeRequest(referral_code = referralCode)
            apiService.referralApi.useReferralCode(request)
        }
    }

    /**
     * Get referral statistics
     */
    suspend fun getReferralStats(userId: Long): Result<ReferralStatsResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            apiService.referralApi.getReferralStats(token)
        }
    }

    /**
     * Redeem a reward
     */
    suspend fun redeemReward(
        userId: Long,
        referralId: Int
    ): Result<RedeemRewardResponse> {
        return ErrorHandler.safeApiCall {
            val token = getAuthToken(userId)
            val request = RedeemRewardRequest(referral_id = referralId)
            apiService.referralApi.redeemReward(token, request)
        }
    }

    private suspend fun getAuthToken(userId: Long): String {
        val accessToken = tokenManager.getAccessToken(userId)
        return "Bearer $accessToken"
    }
}
