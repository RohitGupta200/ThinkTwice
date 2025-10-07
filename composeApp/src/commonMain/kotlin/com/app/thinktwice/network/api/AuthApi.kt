package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.*
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST

interface AuthApi {

    @POST("api/v1/oauth2")
    suspend fun oauth2SignIn(@Body request: OAuth2SignInRequest): OAuth2SignInResponse

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String,
        @Body request: LogoutRequest
    )
}