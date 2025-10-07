package com.app.thinktwice.network

import com.app.thinktwice.network.auth.AuthTokenManager
import io.ktor.client.HttpClient

expect class HttpClientFactory(tokenManager: AuthTokenManager?) {
    fun create(): HttpClient
}