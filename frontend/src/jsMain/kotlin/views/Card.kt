package views

import js.objects.jso
import props.NoteProps
import react.FC
import react.dom.html.ReactHTML.div
import web.cssom.ClassName
import web.cssom.Position
import web.cssom.px

val Card = FC<NoteProps> { props ->
    val note = props.note

    div {
        id = note.id.toString()
        key = id
        className = ClassName(
            if (props.isSelected) "card selected"
            else "card"
        )

        style = jso {
            //place the note at its stored position
            position = Position.absolute
            left = note.positionX.px
            top = note.positionY.px
        }

        onMouseOver = { event ->
            connector.setEnd(note.id)
            event.stopPropagation()
        }

        onMouseDown = { event ->
            if(event.ctrlKey || event.shiftKey)
                props.setSelected(note.id, !props.isSelected, true)
            else
                props.setSelected(note.id, true, props.isSelected)
            props.dragStart(event)
            event.stopPropagation()

        }

        onMouseUp = { event ->
            props.dragEnd(event)
        }

        onDoubleClick = { event ->
            // to prevent the board from creating a card
            event.stopPropagation()
        }

        Header {
            this.note = note
            this.onRefresh = props.onRefresh
            this.setSelected = props.setSelected
        }
        TextArea {
            this.onRefresh = props.onRefresh
            this.note = note
        }
        ConnectArea {
            this.onRefresh = props.onRefresh
            this.note = note
        }
    }
}