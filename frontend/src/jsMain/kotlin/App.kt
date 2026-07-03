import api.*
import domain.model.Connection
import domain.model.Note
import domain.model.NoteRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.MainScope

import react.useEffectOnce
import react.useState
import react.Props
import react.FC


import views.Board

val App = FC<Props> {

    val (noteEntities, setNoteEntities) = useState<List<Note>>(emptyList())
    val (connections, setConnections) = useState<List<Connection>>(emptyList())


    val updateNotes = fun(notesToUpdate: List<Note>, saveToDatabase: Boolean) {
        notesToUpdate.forEach { note ->
            setNoteEntities{ prev ->
                prev.map { currNote ->
                    if (notesToUpdate.contains(currNote)) note
                    else currNote
                }
            }
            if (saveToDatabase) {
                MainScope().launch {
                    val noteRequest = NoteRequest(
                        note.id,
                        note.text,
                        note.positionX,
                        note.positionY
                    )
                    updateNote(noteRequest)
                }
            }
        }
    }

    val moveNotes = fun(selectedIds: Set<String>, save: Boolean, dx: Double, dy: Double) {
        setNoteEntities { prev ->
            prev.map { note ->
                if (selectedIds.contains(note.id.toString())) {
                    note.copy(
                        positionX = note.positionX + dx.toInt(),
                        positionY = note.positionY + dy.toInt()
                    )
                } else note
            }
        }
        if (save) {
            MainScope().launch {
                noteEntities.filter { note ->
                    selectedIds.contains(note.id.toString())
                }.forEach { note ->
                    val noteRequest = NoteRequest(
                        note.id,
                        note.text,
                        note.positionX,
                        note.positionY
                    )
                    updateNote(noteRequest)
                }
            }
        }
    }

//    useEffectOnce {
//        js("""
//        if ('serviceWorker' in navigator) {
//            navigator.serviceWorker.register('/serviceworker.kt');
//        }
//    """)
//    }

    fun getData(){
        MainScope().launch {
            setNoteEntities(getNotes())
            setConnections(getConnections())
        }
    }
    // Fetch on load
    useEffectOnce {
        getData()
    }

    Board {
        this.noteEntities = noteEntities
        this.connections = connections
        this.onRefresh = { getData() }
        this.moveNotes = moveNotes
        this.updateNotes = updateNotes
        this.saveNotePosition = saveNotePosition
    }
}
