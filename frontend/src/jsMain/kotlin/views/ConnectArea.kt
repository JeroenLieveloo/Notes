package views

import api.createAndConnectNote
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import props.NoteProps
import react.FC
import react.dom.html.ReactHTML.div
import web.cssom.ClassName


val ConnectArea = FC<NoteProps> { props ->
    val note = props.note
    div {
        draggable = false
        className = ClassName("connect-area")


        onDragStart = { event ->
            event.preventDefault()
            event.stopPropagation()
        }

        onDrag = { event ->
            event.stopPropagation()
        }

        onDragEnd = { event ->
            event.stopPropagation()
        }

        onMouseDown = { event ->
            lineDrawer.startCursorLine(note.id)
            event.stopPropagation()
        }

        onDoubleClick = { event ->
            MainScope().launch {
                createAndConnectNote(
                    note.id,
                    note.positionX,
                    note.positionY + 250
                )
                props.onRefresh()   // 🔥 reload UI2
            }
            event.stopPropagation()
        }

        div {
            className = ClassName("glow")
        }
    }
}