package views

import api.updateNote
import props.NoteProps
import domain.model.NoteRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.textarea

val TextArea = FC<NoteProps> { props ->
    val note = props.note
    textarea {
        id = "TextArea-${note.id}"
        defaultValue = note.text

        onBlur = { event ->
            val noteRequest = NoteRequest(
                note.id,
                event.target.value,
                note.positionX,
                note.positionY,
            )
            MainScope().launch {
                updateNote(noteRequest)
                props.onRefresh()   // 🔥 reload UI
            }
        }
    }
}