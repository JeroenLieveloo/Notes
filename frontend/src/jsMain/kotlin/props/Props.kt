package props

import domain.model.Connection
import domain.model.Note
import kotlinx.datetime.Instant
import react.Props


external interface NoteListProps : Props {
    var notes: List<Note>
    var connections: List<Connection>
    var onRefresh: () -> Unit
}



external interface NoteProps : Props {
    var note: Note
    var onRefresh: () -> Unit
}


external interface TimestampProps : Props {
    var createdOn: Instant?
    var updatedOn: Instant?
}
