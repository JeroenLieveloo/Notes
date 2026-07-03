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
    var moveNotes: (Set<String>, Boolean, Double, Double) -> Unit
    var updateNotes: (List<Note>, Boolean) -> Unit
    var saveNotePosition: (Int) -> Unit
}

external interface NoteProps : Props {
    var note: Note
    var onRefresh: () -> Unit
    var isSelected: Boolean
    var setSelected: (Int, Boolean, Boolean) -> Unit
    var dragStart: (MouseEvent<*, *>) -> Unit
    var dragEnd: (MouseEvent<*, *>) -> Unit
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
