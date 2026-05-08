import api.*
import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import domain.model.Connection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import views.Cursor

const val cardOffsetX = 500/2
const val cardOffsetY = 200/2

class LineDrawer (val cursor: Cursor) {
    var lines: ArrayList<Line> = arrayListOf()
    var cursorLine: Line? = null
    var startId: Int? = null
    var onRefresh: (() -> Unit)? = null


    init {
        document.addEventListener("mousemove", updateCursorLine())
    }

    fun setRefresh(onRefresh: () -> Unit){
        this.onRefresh = onRefresh
    }

    fun startCursorLine(originId: Int){
        startId = originId
    }

    private fun createCursorLine() {
        cursorLine = Line(
            getByIdAsHTML(startId!!),
            cursor.element,
        )
        document.addEventListener("mouseup", onMouseUp())
    }

    private fun updateCursorLine() : ((Event) -> Unit) = {
        cursorLine?.position()
    }

    fun setEnd(endId: Int?){
        if (startId == null) return
//        if (endId == startId) return
        if(cursorLine == null) createCursorLine()
        console.log("connecting to $endId")
        if (endId == null) {
            cursorLine?.setEnd(cursor.element)
        } else {
            cursorLine?.setEnd(getByIdAsHTML(endId))
        }
    }

    fun removeCursorLine(){
//        println("removing cursor line")
        cursorLine?.remove()
        cursorLine = null
        startId = null
    }


    fun resetLeaderLines(connections: List<Connection>) {
        lines.forEach { it.remove() }
        lines = arrayListOf()
        drawLeaderLines(connections)
    }

    private fun onMouseUp(): ((Event) -> Unit) = {
        try{
            connect()
        } catch(e: Exception){
            println(e)
        }
        removeCursorLine()
    }
    fun connect(){
        cursorLine?: throw NullPointerException("not a valid connection: cursorLine is null")

        val startId = cursorLine!!.getStartElement().id.toInt()
        val endElement = cursorLine!!.getEndElement()

        MainScope().launch {
            val endId  = if (endElement == cursor.element) {
                createNote(
                    cursor.getPositionX() - cardOffsetX,
                    cursor.getPositionY() - cardOffsetY
                )
            } else {
                endElement.id.toInt()
            }

            if(startId == endId) throw IllegalArgumentException("start and end of connection are the same")
            println("Connecting $startId to $endId.")

            saveConnection(Connection(startId, endId, 1))
            onRefresh?.invoke()
        }
    }

    fun drawLeaderLines(connections: List<Connection>){
//        println("Drawing $connections")
        connections.forEach { connection ->
            try {
                lines.add(
                    Line(
                        getByIdAsHTML(connection.startId),
                        getByIdAsHTML(connection.endId)
                    )
                )
            } catch (e: Exception){
                println(e)
            }
        }
    }

    fun deleteConnections(noteId: Int){
        lines.filter {
            it.isConnectedTo(getByIdAsHTML(noteId))
        }.forEach { it.remove() }
    }

    private fun getById(id : Int) : Element {
        return document.getElementById(id.toString()) ?: throw NoSuchElementException("Element with id $id not found.")
    }

    private fun getByIdAsHTML(id : Int) : HTMLElement {
        return getById(id) as HTMLElement
    }
}