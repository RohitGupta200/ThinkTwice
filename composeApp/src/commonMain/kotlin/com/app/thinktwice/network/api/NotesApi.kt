package com.app.thinktwice.network.api

import com.app.thinktwice.network.dto.*
import de.jensklingenberg.ktorfit.http.*

interface NotesApi {

    @GET("notes")
    suspend fun getAllNotes(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortDirection") sortDirection: String = "DESC"
    ): ApiResponse<PaginatedResponse<NoteDto>>

    @GET("users/{userId}/notes")
    suspend fun getNotesByUserId(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PaginatedResponse<NoteDto>>

    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): ApiResponse<NoteDto>

    @POST("notes")
    suspend fun createNote(@Body request: CreateNoteRequest): ApiResponse<NoteDto>

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body request: UpdateNoteRequest
    ): ApiResponse<NoteDto>

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): ApiResponse<Unit>

    @POST("notes/search")
    suspend fun searchNotes(@Body request: SearchNotesRequest): ApiResponse<PaginatedResponse<NoteDto>>

    @GET("notes/important")
    suspend fun getImportantNotes(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PaginatedResponse<NoteDto>>

    @POST("notes/filter")
    suspend fun filterNotes(@Body request: NotesFilterRequest): ApiResponse<PaginatedResponse<NoteDto>>

    @PUT("notes/{id}/important")
    suspend fun markAsImportant(@Path("id") id: Long): ApiResponse<NoteDto>

    @DELETE("notes/{id}/important")
    suspend fun unmarkAsImportant(@Path("id") id: Long): ApiResponse<NoteDto>
}