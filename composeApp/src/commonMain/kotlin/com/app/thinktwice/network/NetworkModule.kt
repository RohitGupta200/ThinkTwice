package com.app.thinktwice.network

import com.app.thinktwice.database.DatabaseCleaner
import com.app.thinktwice.database.ThinkTwiceDatabase
import com.app.thinktwice.database.dao.NoteDao
import com.app.thinktwice.database.dao.SettingsDao
import com.app.thinktwice.database.dao.UserDao
import com.app.thinktwice.network.auth.AuthTokenManager
import com.app.thinktwice.network.repository.*

/**
 * Dependency injection module for network layer
 * Provides instances of all network-related components
 */
class NetworkModule(
    private val database: ThinkTwiceDatabase
) {

    // DAOs
    private val settingsDao: SettingsDao by lazy {
        SettingsDao(database)
    }

    private val userDao: UserDao by lazy {
        UserDao(database)
    }

    private val noteDao: NoteDao by lazy {
        NoteDao(database)
    }

    // Database Cleaner
    private val databaseCleaner: DatabaseCleaner by lazy {
        DatabaseCleaner(userDao, noteDao, settingsDao)
    }

    // Token Manager
    val authTokenManager: AuthTokenManager by lazy {
        AuthTokenManager(settingsDao)
    }

    // HTTP Client
    private val httpClientFactory: HttpClientFactory by lazy {
        HttpClientFactory(authTokenManager)
    }

    // API Service
    val apiService: ApiService by lazy {
        val httpClient = httpClientFactory.create()
        ApiService(httpClient)
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, authTokenManager, databaseCleaner)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(apiService, authTokenManager)
    }

    val analyticsRepository: AnalyticsRepository by lazy {
        AnalyticsRepository(apiService, authTokenManager)
    }

    val currencyRepository: CurrencyRepository by lazy {
        CurrencyRepository(apiService)
    }

    val referralRepository: ReferralRepository by lazy {
        ReferralRepository(apiService, authTokenManager)
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(apiService, authTokenManager)
    }

    val blockedAppRepository: BlockedAppRepository by lazy {
        BlockedAppRepository(apiService, authTokenManager)
    }

    val unblockRequestRepository: UnblockRequestRepository by lazy {
        UnblockRequestRepository(apiService, authTokenManager)
    }

    val spendingSessionRepository: SpendingSessionRepository by lazy {
        SpendingSessionRepository(apiService, authTokenManager)
    }

    val avatarStatsRepository: AvatarStatsRepository by lazy {
        AvatarStatsRepository(apiService, authTokenManager)
    }

    val advancedAnalyticsRepository: AdvancedAnalyticsRepository by lazy {
        AdvancedAnalyticsRepository(apiService, authTokenManager)
    }

    /**
     * Cleanup resources
     */
    fun close() {
        apiService.close()
    }
}
