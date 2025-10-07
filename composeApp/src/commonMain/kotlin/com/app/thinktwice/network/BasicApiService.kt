package com.app.thinktwice.network

import com.app.thinktwice.network.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Basic API service using raw Ktor client (without KtorFit)
 * This demonstrates the network layer works without code generation
 */
class BasicApiService(
    private val httpClient: HttpClient,
    private val baseUrl: String = ApiConfig.FULL_BASE_URL
) {

    // Authentication - OAuth2
    suspend fun oauth2SignIn(provider: String, token: String, deviceId: String, userAgent: String, referralCode: String? = null): OAuth2SignInResponse {
        val request = OAuth2SignInRequest(provider, token, deviceId, userAgent, referralCode)
        return httpClient.post("$baseUrl/api/v1/oauth2") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        val request = RefreshTokenRequest(refreshToken)
        return httpClient.post("$baseUrl/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Users
    suspend fun getCurrentUserProfile(): ApiResponse<UserDto> {
        return httpClient.get("$baseUrl/users/profile").body()
    }

    suspend fun updateProfile(request: UpdateUserRequest): ApiResponse<UserDto> {
        return httpClient.put("$baseUrl/users/profile") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Notes
    suspend fun createNote(request: CreateNoteRequest): ApiResponse<NoteDto> {
        return httpClient.post("$baseUrl/notes") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getNoteById(id: Long): ApiResponse<NoteDto> {
        return httpClient.get("$baseUrl/notes/$id").body()
    }

    suspend fun getNotesByUserId(userId: Long, page: Int = 1, pageSize: Int = 20): ApiResponse<PaginatedResponse<NoteDto>> {
        return httpClient.get("$baseUrl/users/$userId/notes") {
            parameter("page", page)
            parameter("pageSize", pageSize)
        }.body()
    }

    suspend fun updateNote(id: Long, request: UpdateNoteRequest): ApiResponse<NoteDto> {
        return httpClient.put("$baseUrl/notes/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteNote(id: Long): ApiResponse<Unit> {
        return httpClient.delete("$baseUrl/notes/$id").body()
    }

    suspend fun searchNotes(request: SearchNotesRequest): ApiResponse<PaginatedResponse<NoteDto>> {
        return httpClient.post("$baseUrl/notes/search") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Settings
    suspend fun getUserSettings(userId: Long): ApiResponse<List<SettingDto>> {
        return httpClient.get("$baseUrl/users/$userId/settings").body()
    }

    suspend fun createSetting(userId: Long, request: CreateSettingRequest): ApiResponse<SettingDto> {
        return httpClient.post("$baseUrl/users/$userId/settings") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateSetting(userId: Long, key: String, request: UpdateSettingRequest): ApiResponse<SettingDto> {
        return httpClient.put("$baseUrl/users/$userId/settings/$key") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Sync
    suspend fun syncData(request: SyncDataRequest): ApiResponse<SyncDataResponse> {
        return httpClient.post("$baseUrl/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun close() {
        httpClient.close()
    }
}