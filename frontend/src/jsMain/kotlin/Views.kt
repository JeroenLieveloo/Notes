import api.*
import props.*
import domain.model.NoteRequest
import js.objects.jso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import kotlinx.datetime.*
import web.cssom.ClassName
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.textarea
import react.useEffect
import web.cssom.Position
import web.cssom.px

var lineDrawer = LineDrawer()

private val scope = MainScope()

var offsetX = 0
var offsetY = 0

val NoteList: FC<NoteListProps> = FC { props ->

    lineDrawer.setRefresh(props.onRefresh)
    div {
        id = "canvas"

        useEffect(props.notes, props.connections) {
            lineDrawer.resetLeaderLines(props.connections)
        }

        onMouseOver = {
            lineDrawer.setEnd(null)
        }

        onDoubleClick = {
            scope.launch {
                createNote(
                    it.clientX.toInt(),
                    it.clientY.toInt(),
                )
                props.onRefresh()
            }
        }

        button {
            className = ClassName("reset-button")
            + "Reset database"
            onClick = {
                scope.launch {
                    reset()
                    props.onRefresh()
                }
            }
        }
        h1 { +"My Notes" }

        props.notes.forEach { note ->
            NoteCard {
                this.note = note
                this.onRefresh = props.onRefresh
            }
        }
    }
}

val NoteCard = FC<NoteProps> { props ->
    val note = props.note
    div {
        id = note.id.toString()
        className = ClassName("card")

        style = jso {
            //place the note at its stored position
            position = Position.absolute
            left = note.positionX.px
            top = note.positionY.px
        }

        onDoubleClick = { it.stopPropagation() }
        onMouseOver = { it.stopPropagation() }

        div { // HEADER
            className = ClassName("spaced-menu")

            draggable = true

            onDragStart = {
                offsetX = note.positionX - it.clientX.toInt()
                offsetY = note.positionY - it.clientY.toInt()
            }

            onDragEnd = {
                val noteRequest = NoteRequest(
                    note.id,
                    note.text,
                    it.clientX.toInt() + offsetX,
                    it.clientY.toInt() + offsetY,
                )
                scope.launch {
                    updateNote(noteRequest)
                    props.onRefresh()   // 🔥 reload UI
                }
            }


            timestamp {
                createdOn = note.createdOn
                updatedOn = note.updatedOn
            }

            button {
                className = ClassName("circle small secondary-hover")
                onClick = {
                    scope.launch {
                        deleteNote(note.id)
                        props.onRefresh()   // 🔥 reload UI
                    }
                }
            }
        }
        textarea {
            defaultValue = note.text

            onBlur = { event ->
                val noteRequest = NoteRequest(
                    note.id,
                    event.target.value,
                    note.positionX,
                    note.positionY,
                )
                scope.launch {
                    updateNote(noteRequest)
                    props.onRefresh()   // 🔥 reload UI
                }
            }
        }

        div { // Draggable edge
            className = ClassName("connect-area primary")

            onMouseOver = {
                lineDrawer.setEnd(note.id)
            }

            onMouseDown = {
                lineDrawer.startCursorLine(note.id)
            }

            onDoubleClick = {
                scope.launch {
                    createAndConnectNote(
                        note.id,
                        note.positionX,
                        note.positionY + 250
                    )
                    props.onRefresh()   // 🔥 reload UI2
                }
                it.stopPropagation()
            }
        }
    }
}

val timestamp = FC<TimestampProps> { props ->
    val createdOn = props.createdOn
    val updatedOn = props.updatedOn

    fun Int.format(digits: Int = 2): String {
        val str = this.toString()
        if (str.length >= digits) return str
        return "0".repeat(digits - str.length) + str
    }

    fun Instant.format() =
        this.toLocalDateTime(TimeZone.currentSystemDefault())
            .run {"${year}-${month.number.format()}-${dayOfMonth.format()} ${hour.format()}:${time.minute.format()}"}

    if (createdOn != null){
        div{
            className = ClassName("timestamp")
            + "Created on: ${createdOn.format()}"
            if (updatedOn != null){
                + ", Updated on: ${updatedOn.format()}"
            }
        }
    }
}
