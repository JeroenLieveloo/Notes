package views

import api.deleteNote
import props.NoteProps

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

val Header = FC<NoteProps> { props ->
    val note = props.note

    div {
        className = ClassName("spaced-menu grabbable")

        Timestamp {
            createdOn = note.createdOn
            updatedOn = note.updatedOn
        }

        button {
            className = ClassName("circle small secondary-hover")
            onClick = {
                MainScope().launch {
                    deleteNote(note.id)
                    props.onRefresh()   // 🔥 reload UI
                }
            }
        }

        onMouseOver = { event ->
            lineDrawer.setEnd(note.id)
            event.stopPropagation()
        }

        onMouseDown = { event ->
            props.onSelect(event, note.id.toString())
            event.stopPropagation()
        }

//        onClick = { event ->
//            event.stopPropagation()
//        }

    }
}