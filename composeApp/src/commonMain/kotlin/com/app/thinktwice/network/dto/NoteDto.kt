package com.app.thinktwice.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val category: String? = null,
    val isImportant: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateNoteRequest(
    val title: String,
    val content: String,
    val category: String? = null,
    val isImportant: Boolean = false
)

@Serializable
data class UpdateNoteRequest(
    val title: String? = null,
    val content: String? = null,
    val category: String? = null,
    val isImportant: Boolean? = null
)

@Serializable
data class SearchNotesRequest(
    val query: String,
    val category: String? = null,
    val isImportant: Boolean? = null,
    val pagination: PaginationRequest = PaginationRequest()
)

@Serializable
data class NotesFilterRequest(
    val category: String? = null,
    val isImportant: Boolean? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val pagination: PaginationRequest = PaginationRequest()
)