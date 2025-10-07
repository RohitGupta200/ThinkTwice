package com.app.thinktwice.network.repository

import com.app.thinktwice.network.ApiExceptionMapper
import com.app.thinktwice.network.BasicApiService
import com.app.thinktwice.network.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class NetworkNotesRepository(private val apiService: BasicApiService) {

    suspend fun createNote(request: CreateNoteRequest): Result<NoteDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createNote(request)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to create note"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun getNoteById(id: Long): Result<NoteDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNoteById(id)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Note not found"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun getNotesByUserId(
        userId: Long,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResponse<NoteDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getNotesByUserId(userId, page, pageSize)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to get notes"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun updateNote(id: Long, request: UpdateNoteRequest): Result<NoteDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateNote(id, request)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to update note"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun deleteNote(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteNote(id)

                if (response.success) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.message ?: "Failed to delete note"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun searchNotes(request: SearchNotesRequest): Result<PaginatedResponse<NoteDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchNotes(request)

                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Search failed"))
                }
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun getImportantNotes(page: Int = 1, pageSize: Int = 20): Result<PaginatedResponse<NoteDto>> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Get important notes not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun markAsImportant(id: Long): Result<NoteDto> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Mark as important not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun unmarkAsImportant(id: Long): Result<NoteDto> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Unmark as important not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }

    suspend fun filterNotes(request: NotesFilterRequest): Result<PaginatedResponse<NoteDto>> {
        return withContext(Dispatchers.IO) {
            try {
                // Not implemented in BasicApiService
                Result.failure(Exception("Filter notes not implemented"))
            } catch (e: Exception) {
                Result.failure(ApiExceptionMapper.mapException(e))
            }
        }
    }
}