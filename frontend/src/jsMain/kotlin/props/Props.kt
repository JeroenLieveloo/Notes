package props

import domain.model.Connection
import domain.model.Note
import kotlinx.datetime.Instant
import react.Props
import react.dom.events.MouseEvent


external interface BoardProps : Props {
    var noteEntities: List<Note>
    var connections: List<Connection>
    var onRefresh: () -> Unit
    var moveNotes: (Set<String>, Double, Double) -> Unit
    var saveNotePositions: () -> Unit
    var selectNote: (String, Boolean) -> Unit


}


external interface CardHandle {
    fun setPosition(x: Int, y: Int)
}

external interface NoteProps : Props {
    var note: Note
    var onRefresh: () -> Unit
    var isSelected: Boolean
    var onSelect: (MouseEvent<*, *>, String) -> Unit
}


external interface TimestampProps : Props {
    var createdOn: Instant?
    var updatedOn: Instant?
}

external interface RefreshableProps : Props {
    var onRefresh: () -> Unit
}

external interface MenuProps : Props {
    var onRefresh: () -> Unit
    var onDelete: () -> Unit
}
