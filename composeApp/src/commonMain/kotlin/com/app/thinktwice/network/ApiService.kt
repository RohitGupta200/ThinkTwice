package com.app.thinktwice.network

import com.app.thinktwice.network.api.*
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

class ApiService(private val httpClient: HttpClient, private val baseUrl: String = ApiConfig.FULL_BASE_URL) {

    private val ktorfit = Ktorfit.Builder()
        .httpClient(httpClient)
        .baseUrl(baseUrl)
        .build()

    // API interfaces
    val authApi: AuthApi = ktorfit.create()
    val userApi: UserApi = ktorfit.create()
    val profileApi: ProfileApi = ktorfit.create()
    val analyticsApi: AnalyticsApi = ktorfit.create()
    val advancedAnalyticsApi: AdvancedAnalyticsApi = ktorfit.create()
    val currencyApi: CurrencyApi = ktorfit.create()
    val referralApi: ReferralApi = ktorfit.create()
    val blockedAppApi: BlockedAppApi = ktorfit.create()
    val unblockRequestApi: UnblockRequestApi = ktorfit.create()
    val spendingSessionApi: SpendingSessionApi = ktorfit.create()
    val avatarStatsApi: AvatarStatsApi = ktorfit.create()
    val notesApi: NotesApi = ktorfit.create()
    val settingsApi: SettingsApi = ktorfit.create()
    val syncApi: SyncApi = ktorfit.create()

    fun close() {
        httpClient.close()
    }
}

class ApiServiceFactory {
    fun createApiService(httpClientFactory: HttpClientFactory): ApiService {
        val httpClient = httpClientFactory.create()
        return ApiService(httpClient)
    }
}

// Extension functions for easier access
fun ApiService.auth() = authApi
fun ApiService.users() = userApi
fun ApiService.profile() = profileApi
fun ApiService.analytics() = analyticsApi
fun ApiService.advancedAnalytics() = advancedAnalyticsApi
fun ApiService.currency() = currencyApi
fun ApiService.referral() = referralApi
fun ApiService.blockedApps() = blockedAppApi
fun ApiService.unblockRequests() = unblockRequestApi
fun ApiService.spendingSessions() = spendingSessionApi
fun ApiService.avatarStats() = avatarStatsApi
fun ApiService.notes() = notesApi
fun ApiService.settings() = settingsApi
fun ApiService.sync() = syncApi