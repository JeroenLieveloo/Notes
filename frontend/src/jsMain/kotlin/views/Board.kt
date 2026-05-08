package views

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import LineDrawer
import api.createNote
import props.BoardProps
import react.dom.events.MouseEvent
import react.useEffect
import react.useRef
import react.useState


var lineDrawer = LineDrawer(cursor)

val Board: FC<BoardProps> = FC { props ->

    lineDrawer.setRefresh(props.onRefresh)
    val (selectedIds, setSelectedIds) = useState<Set<String>>(emptySet())


    useEffect(props.noteEntities, props.connections) {
        lineDrawer.resetLeaderLines(props.connections)
    }

    val dragStart = useRef<Pair< Double, Double>>(null)

    fun selectNote(e: MouseEvent<*, *>, id: String, multi: Boolean) {
        println("Selecting $id: $multi")
        dragStart.current = e.clientX to e.clientY
        setSelectedIds { prev ->
            if (multi) {
                if (prev.contains(id)) prev else prev + id
            } else {
                setOf(id)
            }
        }
    }

    val onDragging = fun(e: MouseEvent<*, *>) {
        val (startX, startY) = dragStart.current?: return

        val dx = e.clientX - startX
        val dy = e.clientY - startY

        props.moveNotes(selectedIds, dx, dy)
        dragStart.current = e.clientX to e.clientY
    }

    val onDragEnd = fun (e: MouseEvent<*, *>) {
        println("Drag ending from $dragStart.")

        dragStart.current = null
        props.saveNotePositions()
    }

    div {
        id = "board"
        onMouseMove = { onDragging(it) }
        onMouseUp = { onDragEnd(it) }


        props.noteEntities.forEach { note ->

            Card {
                key = note.id.toString()
                this.note = note
                this.onRefresh = props.onRefresh
                this.isSelected = selectedIds.contains(key)
                this.onSelect = { e, unit ->
                    selectNote(e, note.id.toString(), e.ctrlKey || e.shiftKey)
                }
            }
        }


        onMouseOver = {
            lineDrawer.setEnd(null)
        }

        onClick = {
            it.stopPropagation()
            setSelectedIds(setOf())
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

        onDrag = {
            it.preventDefault()
            it.stopPropagation()
        }
    }
}
