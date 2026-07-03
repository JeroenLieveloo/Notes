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

        onMouseDown = { event->
            // If the multiselect keys are pressed, then allow the card to handle the click event,
            // if they are not pressed we need to stop propagation because the text field should allow for clicking and double-clicking to select text.
            if (event.ctrlKey || event.shiftKey) {
                event.preventDefault()
            } else {
                event.stopPropagation()
            }
        }

        onBlur = { event ->
            val noteRequest = NoteRequest(
                note.id,
                event.target.value,
                note.positionX,
                note.positionY,
            )
            MainScope().launch {
                updateNote(noteRequest)
                props.onRefresh()
            }
        }
    }
}