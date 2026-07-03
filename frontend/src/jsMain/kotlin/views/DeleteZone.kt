package views

import props.MenuProps
import react.FC
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.svg.ReactSVG.svg
import web.cssom.ClassName
import web.cssom.HtmlAttributes.Companion.src
//
//@JsNonModule
//@JsModule(".//icons/trashcan.svg")
//external val trashcanLogo: dynamic


val DeleteZone : FC<MenuProps> = FC { props ->
    div {
        img{
            src = "/icons/trashcan.svg"
        }

        className = ClassName("drop-zone large")

        onMouseUp = {
            props.onDelete()
        }
    }
}