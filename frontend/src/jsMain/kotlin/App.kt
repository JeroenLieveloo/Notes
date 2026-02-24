import api.*
import domain.model.Connection
import domain.model.Note
import kotlinx.coroutines.launch
import react.useEffectOnce
import react.useState

import kotlinx.coroutines.MainScope

import react.*

private val scope = MainScope()

val App = FC<Props> {

    var notes by useState<List<Note>>(emptyList())
    var connections by useState<List<Connection>>(emptyList())
    // Fetch on load
    useEffectOnce {
        scope.launch {
            notes = getNotes()
            connections = getConnections()
        }
    }

    // Pass down the updater to children
    NoteList {
        this.notes = notes
        this.connections = connections
        this.onRefresh = {
            scope.launch {
                notes = getNotes()
                connections = getConnections()
            }
        }
    }
}
