package domain.service

import domain.model.Note
import domain.model.NoteRequest
import repository.NoteEntity
import repository.NoteStorage

import kotlinx.datetime.Instant

class NoteManager(private val storage: NoteStorage) {

    private fun convertNoteEntityToNote(noteEntity: NoteEntity): Note {
        return Note(
            noteEntity.id!!,
            noteEntity.text,
            noteEntity.positionX,
            noteEntity.positionY,
            if(noteEntity.createdOn != null) Instant.fromEpochMilliseconds(noteEntity.createdOn) else null,
            if(noteEntity.updatedOn != null) Instant.fromEpochMilliseconds(noteEntity.updatedOn) else null
        )
    }

    suspend fun getNotes(): List<Note> {
        return storage.getAllNotes().map { convertNoteEntityToNote(it) }
    }

    fun reset(){
        storage.reset()
    }

    private fun getConnectedNotes(noteEntityList: List<NoteEntity>): List<Note>{
        return noteEntityList
            .sortedBy { it.id }
            .map { convertNoteEntityToNote(it) }
    }

    suspend fun deleteNote(noteId: Int){
        storage.deleteNote(noteId)
    }

    suspend fun createOrUpdateNote(noteRequest: NoteRequest): Int{
        return if (noteRequest.id == null){
            storage.createNote(noteRequest)
        } else {
            storage.updateNote(noteRequest)
        }
    }
}