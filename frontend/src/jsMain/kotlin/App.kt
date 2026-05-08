import api.*
import domain.model.Connection
import domain.model.Note
import domain.model.NoteRequest
import kotlinx.coroutines.launch
import react.useEffectOnce
import react.useState

import kotlinx.coroutines.MainScope

import react.*
import react.dom.events.MouseEvent
import views.*

private val scope = MainScope()

val App = FC<Props> {

    val (noteEntities, setNoteEntities) = useState<List<Note>>(emptyList())
    val (connections, setConnections) = useState<List<Connection>>(emptyList())
    val (selectedIds, setSelectedIds) = useState<Set<String>>(emptySet())


    val toggleSelectNote = fun(id: String, multi: Boolean) {
        setSelectedIds { prev ->
            if (multi) {
                if (prev.contains(id)) prev else prev + id
            } else {
                setOf(id)
            }
        }
    }

    val moveNotes = fun(selectedIds: Set<String>, dx: Double, dy: Double) {
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
    }

    val saveNotePositions = fun() {
        noteEntities.forEach { note ->
            val noteRequest = NoteRequest(
                note.id,
                note.text,
                note.positionX,
                note.positionY
            )
            MainScope().launch {
                updateNote(noteRequest)
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
        scope.launch {
            setNoteEntities(getNotes())
            setConnections(getConnections())
        }
    }
    // Fetch on load
    useEffectOnce {
        getData()
    }

    Menu {
        this.onDelete = {
//            deleteSelectedNotes() todo?
            getData() }
    }

    Board {
        this.noteEntities = noteEntities
        this.connections = connections
        this.onRefresh = { getData() }
        this.moveNotes = moveNotes
        this.saveNotePositions = saveNotePositions
        this.selectNote = toggleSelectNote
    }
}
