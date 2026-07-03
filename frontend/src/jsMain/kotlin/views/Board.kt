package views

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import LineDrawer
import api.createNote
import api.deleteNote
import props.BoardProps
import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.useEffect
import react.useRef
import react.useState
import web.html.HTMLDivElement

var connector = Connector(cursor)

val Board: FC<BoardProps> = FC { props ->

    connector.setRefresh(props.onRefresh)
    val (selectedIds, setSelectedIds) = useState<Set<String>>(emptySet())


    useEffect(props.noteEntities, props.connections) {
        connector.resetLeaderLines(props.connections)
    }

    val dragPrevPosition = useRef<Pair<Double, Double>>(null)

    val selectNote = fun(id: Int, setSelected: Boolean, multi: Boolean) {
        val sid = id.toString()
        println("Selecting $id: $multi")
        setSelectedIds { prev ->
            if (multi) {
                if (prev.contains(sid)) {
                    if(setSelected) prev else prev - sid
                } else {
                    if(setSelected) prev + sid else prev
                }
            } else {
                setOf(sid)
            }
        }
    }

    fun moveSelection(dx: Double, dy: Double) {
        props.noteEntities.filter { note -> note.isSelected }.forEach { note ->
            note.positionY + dx
            note.positionX + dy
        }
    }

    val dragStart = fun (e: MouseEvent<*, *>) {
        dragPrevPosition.current = e.clientX to e.clientY
    }

    val drag = fun(e: MouseEvent<*, *>) {
        val (prevX, prevY) = dragPrevPosition.current?: return
        val dx = e.clientX - prevX
        val dy = e.clientY - prevY
        dragPrevPosition.current = e.clientX to e.clientY
        props.moveNotes(selectedIds, false, dx, dy)

//        moveSelection(move.first.toDouble(), move.second.toDouble())
//        props.updateNotes( props.noteEntities.filter { it.isSelected }, true)
    }

    val dragEnd = fun (e: MouseEvent<*, *>) {
        dragPrevPosition.current = null
        props.moveNotes(selectedIds, true, 0.0, 0.0)
    }

    val onKeyPressed = fun (event: KeyboardEvent<HTMLDivElement>){
        val stepSize = if(event.ctrlKey){100} else {10}
        var move = when (event.key) {
            "ArrowDown" -> 0 to stepSize
            "ArrowUp" -> 0 to -stepSize
            "ArrowLeft" -> (-stepSize) to 0
            "ArrowRight" -> stepSize to 0
            else -> 0 to 0
        }
        println("Moving $move")
        props.moveNotes(selectedIds, true, move.first.toDouble(), move.second.toDouble())
    }

    div{
        Menu {
            this.onDelete = {
                console.log("Deleting ${selectedIds.count()} selected notes")
                MainScope().launch {
                    selectedIds.forEach { id ->
                        deleteNote(id.toInt())
                    }
                    props.onRefresh()
                }
                setSelectedIds(setOf())
            }
        }
    }

    div {
        id = "board"
        onMouseMove = { drag(it) }
        onMouseDown = {
            println("Resetting selection")
            setSelectedIds(setOf())
        }
        onKeyDown  = { onKeyPressed(it) }
        props.noteEntities.forEach { note ->
            Card {
                key = note.id.toString()
                this.note = note
                this.onRefresh = props.onRefresh
                this.isSelected = selectedIds.contains(key)
                this.setSelected = selectNote
                this.dragStart = { dragStart(it) }
                this.dragEnd = { dragEnd(it) }
            }
        }

        onMouseOver = {
            connector.setEnd(null)
        }

        onDoubleClick = {
            MainScope().launch {
                createNote(
                    it.pageX.toInt() - 250,
                    it.pageY.toInt() - 100,
                )
                props.onRefresh()
            }
        }
    }
}
