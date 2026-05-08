package views

import api.reset
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import props.MenuProps
import react.FC
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import web.cssom.ClassName

val Menu : FC<MenuProps> = FC { props ->
    div {
        className = ClassName("spaced-menu top-menu")
        button {
            className = ClassName("reset-button")
            +"Reset database"
            onClick = {
                MainScope().launch {
                    reset()
                    props.onRefresh()
                }
            }
        }

        h1 { +"My Notes" }

        DeleteZone {
            this.onDelete = props.onDelete
        }
    }
}