package com.app.thinktwice.database.repository

import com.app.thinktwice.database.Note
import com.app.thinktwice.database.dao.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun createNote(userId: Long, title: String, content: String, category: String? = null, isImportant: Boolean = false): Result<Long> {
        return try {
            val validationErrors = validateNote(title, content)
            if (validationErrors.isNotEmpty()) {
                Result.failure(Exception(validationErrors.joinToString(", ")))
            } else {
                val noteId = noteDao.insert(userId, title, content, category, isImportant)
                Result.success(noteId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNoteById(id: Long): Note? {
        return noteDao.getById(id)
    }

    suspend fun getNotesByUserId(userId: Long): List<Note> {
        return noteDao.getByUserId(userId)
    }

    fun getNotesByUserIdFlow(userId: Long): Flow<List<Note>> {
        return noteDao.getByUserIdFlow(userId)
    }

    suspend fun getNotesByCategory(category: String): List<Note> {
        return noteDao.getByCategory(category)
    }

    suspend fun getImportantNotes(): List<Note> {
        return noteDao.getImportant()
    }

    fun getImportantNotesFlow(): Flow<List<Note>> {
        return noteDao.getImportantFlow()
    }

    suspend fun searchNotes(query: String): List<Note> {
        return if (query.isBlank()) {
            emptyList()
        } else {
            noteDao.search(query)
        }
    }

    suspend fun getAllNotes(): List<Note> {
        return noteDao.getAll()
    }

    fun getAllNotesFlow(): Flow<List<Note>> {
        return noteDao.getAllFlow()
    }

    suspend fun updateNote(id: Long, title: String, content: String, category: String? = null, isImportant: Boolean = false): Result<Unit> {
        return try {
            val validationErrors = validateNote(title, content)
            if (validationErrors.isNotEmpty()) {
                Result.failure(Exception(validationErrors.joinToString(", ")))
            } else {
                noteDao.update(id, title, content, category, isImportant)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNote(id: Long): Result<Unit> {
        return try {
            noteDao.delete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotesByUserId(userId: Long): Result<Unit> {
        return try {
            noteDao.deleteByUserId(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNoteCount(): Long {
        return noteDao.count()
    }

    suspend fun getNoteCountByUser(userId: Long): Long {
        return noteDao.countByUser(userId)
    }

    suspend fun markNoteAsImportant(id: Long): Result<Unit> {
        return try {
            val note = noteDao.getById(id)
            if (note != null) {
                noteDao.update(id, note.title, note.content, note.category, true)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markNoteAsNotImportant(id: Long): Result<Unit> {
        return try {
            val note = noteDao.getById(id)
            if (note != null) {
                noteDao.update(id, note.title, note.content, note.category, false)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateNote(title: String, content: String): List<String> {
        val errors = mutableListOf<String>()

        if (title.isBlank()) {
            errors.add("Title cannot be empty")
        }

        if (content.isBlank()) {
            errors.add("Content cannot be empty")
        }

        if (title.length > 255) {
            errors.add("Title must be 255 characters or less")
        }

        return errors
    }
}