package com.app.thinktwice.database.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.thinktwice.database.Note
import com.app.thinktwice.database.ThinkTwiceDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import com.app.thinktwice.database.utils.TimeProvider

class NoteDao(private val database: ThinkTwiceDatabase) {

    suspend fun insert(userId: Long, title: String, content: String, category: String? = null, isImportant: Boolean = false): Long {
        val currentTime = TimeProvider.currentTimeMillis()
        database.noteQueries.insertNote(userId, title, content, category, if (isImportant) 1 else 0, currentTime, currentTime)
        return database.noteQueries.lastInsertRowId().executeAsOne()
    }

    suspend fun getById(id: Long): Note? {
        return database.noteQueries.getNoteById(id).executeAsOneOrNull()
    }

    suspend fun getByUserId(userId: Long): List<Note> {
        return database.noteQueries.getNotesByUserId(userId).executeAsList()
    }

    fun getByUserIdFlow(userId: Long): Flow<List<Note>> {
        return database.noteQueries.getNotesByUserId(userId).asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun getByCategory(category: String): List<Note> {
        return database.noteQueries.getNotesByCategory(category).executeAsList()
    }

    suspend fun getImportant(): List<Note> {
        return database.noteQueries.getImportantNotes().executeAsList()
    }

    fun getImportantFlow(): Flow<List<Note>> {
        return database.noteQueries.getImportantNotes().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun search(query: String): List<Note> {
        return database.noteQueries.searchNotes(query, query).executeAsList()
    }

    suspend fun getAll(): List<Note> {
        return database.noteQueries.getAllNotes().executeAsList()
    }

    fun getAllFlow(): Flow<List<Note>> {
        return database.noteQueries.getAllNotes().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun update(id: Long, title: String, content: String, category: String? = null, isImportant: Boolean = false) {
        val currentTime = TimeProvider.currentTimeMillis()
        database.noteQueries.updateNote(title, content, category, if (isImportant) 1 else 0, currentTime, id)
    }

    suspend fun delete(id: Long) {
        database.noteQueries.deleteNote(id)
    }

    suspend fun deleteByUserId(userId: Long) {
        database.noteQueries.deleteNotesByUserId(userId)
    }

    suspend fun count(): Long {
        return database.noteQueries.getNoteCount().executeAsOne()
    }

    suspend fun countByUser(userId: Long): Long {
        return database.noteQueries.getNoteCountByUser(userId).executeAsOne()
    }

    suspend fun deleteAll() {
        database.noteQueries.deleteAllNotes()
    }
}