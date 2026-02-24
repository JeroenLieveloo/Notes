import api.*
import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import domain.model.Connection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element

const val cardOffsetX = 500/2
const val cardOffsetY = 200/2

fun createCursorAnchor(): HTMLElement =
    (document.createElement("div") as HTMLElement).apply {
        style.left = "0px"
        style.top = "0px"
        id = "cursor-anchor"
        document.body!!.appendChild(this)
    }

class LineDrawer {
    var lines: ArrayList<Line> = arrayListOf()
    var cursorLine: Line? = null
    var startId: Int? = null
    var onRefresh: (() -> Unit)? = null

    fun String.numberOfPixels(): Int {
        return if (this.contains("px"))
            this.split("px")[0].toInt()
        else throw NumberFormatException("Can't parse number of pixels as it does not pixels")
    }


    val cursorElement = createCursorAnchor()

    init {
        document.addEventListener("mousemove", updateCursorLine(cursorElement))
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
            cursorElement
        )
        document.addEventListener("mouseup", connect())
    }

    private fun updateCursorLine(cursorElement: HTMLElement) : ((Event) -> Unit) = { event ->
        event as MouseEvent
        cursorElement.style.left = "${event.clientX}px"
        cursorElement.style.top = "${event.clientY}px"
        cursorLine?.position()
    }

    fun setEnd(endId: Int?){
        if (startId == null) return
//        if (endId == startId) return
        if(cursorLine == null) createCursorLine()
        if (endId == null) {
            cursorLine?.setEnd(cursorElement)
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

    fun connect()  : ((Event) -> Unit) =  {
        try {
            cursorLine?: throw NullPointerException("not a valid connection: cursorLine is null")

            val startId = cursorLine!!.getStartElement().id.toInt()
            val endElement = cursorLine!!.getEndElement()

            MainScope().launch {
                val endId  = if (endElement == cursorElement) {
                    createNote(
                    cursorElement.style.left.numberOfPixels() - cardOffsetX,
                    cursorElement.style.top.numberOfPixels() - cardOffsetY
                    )
                } else {
                    endElement.id.toInt()
                }

                if(startId == endId) throw IllegalArgumentException("start and end of connection are the same")
                println("Connecting $startId to $endId.")

                saveConnection(Connection(startId, endId, 1))
                onRefresh?.invoke()
            }
        } catch (e: Exception) {
//            println(e)
        }
        finally{
            removeCursorLine()
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