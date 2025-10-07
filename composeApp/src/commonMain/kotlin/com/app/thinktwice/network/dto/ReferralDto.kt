package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateReferralCodeRequest(
    val reward_type: String,
    val reward_value: Double? = null,
    val reward_description: String,
    val max_uses: Int,
    val expires_in_days: Int? = null
)

@Serializable
data class ReferralCodeResponse(
    val id: Int,
    val code: String,
    val is_active: Boolean,
    val max_uses: Int,
    val current_uses: Int,
    val reward_type: String,
    val reward_value: Double? = null,
    val reward_description: String,
    val created_at: String,
    val expires_at: String? = null
)

@Serializable
data class ValidateReferralCodeRequest(
    val referral_code: String
)

@Serializable
data class ValidateReferralCodeResponse(
    val is_valid: Boolean,
    val code: String,
    val referrer_name: String,
    val reward_type: String,
    val reward_description: String,
    val remaining_uses: Int,
    val expires_at: String? = null
)

@Serializable
data class UseReferralCodeRequest(
    val referral_code: String
)

@Serializable
data class UseReferralCodeResponse(
    val id: Int,
    val referral_code: String,
    val referrer_name: String,
    val referred_user_name: String,
    val reward_type: String,
    val reward_status: String,
    val reward_value: Double? = null,
    val created_at: String
)

@Serializable
data class ReferralStatsResponse(
    val total_referrals_sent: Int,
    val total_referrals_received: Int,
    val total_rewards_earned: Int,
    val total_rewards_redeemed: Int,
    val active_referral_codes: Int,
    val pending_rewards: Int
)

@Serializable
data class RedeemRewardRequest(
    val referral_id: Int
)

@Serializable
data class RedeemRewardResponse(
    val id: Int,
    val reward_type: String,
    val reward_value: Double? = null,
    val reward_description: String,
    val redemption_code: String,
    val redemption_instructions: String,
    val status: String,
    val created_at: String,
    val expires_at: String? = null
)
