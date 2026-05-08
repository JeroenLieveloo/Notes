package views

import api.deleteNote
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import props.MenuProps
import props.RefreshableProps
import react.FC
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

val DeleteZone : FC<MenuProps> = FC { props ->
    div {
        className = ClassName("drop-zone large")

        onDragOver = {
            it.preventDefault()
        }

        onDragEnter = {
            it.preventDefault()
        }

        onDrop = { event ->
            event.preventDefault()
            MainScope().launch {
                props.onDelete()
            }
        }
    }
}