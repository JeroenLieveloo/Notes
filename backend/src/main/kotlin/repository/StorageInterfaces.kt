package repository

import domain.model.NoteRequest

interface NoteStorage {
    suspend fun createNote(note: NoteRequest): Int
    suspend fun updateNote(note: NoteRequest): Int
    suspend fun deleteNote(noteId: Int)
    suspend fun getAllNotes(): List<NoteEntity>
    suspend fun getNoteById(noteId: Int): NoteEntity?
    fun reset()
}

interface ConnectionStorage {
    suspend fun createConnection(connection: ConnectionEntity): Int
    suspend fun updateConnection(connection: ConnectionEntity): Int
    suspend fun deleteConnection(connectionId: Int)
    suspend fun getAllConnections(): List<ConnectionEntity>
    fun reset()
}
