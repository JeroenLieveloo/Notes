package views

import Line
import LineDrawer
import api.createNote
import api.saveConnection
import cardOffsetX
import cardOffsetY
import domain.model.Connection
import kotlinx.browser.document
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

class Connector (val cursor: Cursor): LineDrawer() {

    init {
        val lineDrawer = LineDrawer()
        document.addEventListener("mousemove", updateCursorLine())
        document.addEventListener("mouseup", onMouseUp())
    }

    var cursorLine: Line? = null
    var startId: Int? = null
    var endId: Int? = null


    fun startCursorLine(originId: Int){
        startId = originId
        // only create a line when it exits the current card to prevent clicking the connect area from creating a card
    }

    private fun createCursorLine() {
        cursorLine = Line(
            getByIdAsHTML(startId!!),
            cursor.element,
        )
    }

    private fun updateCursorLine() : ((Event) -> Unit) = {
        cursorLine?.position()
    }

    fun setEnd(endId: Int?){
        if (startId == null) return
        if (endId == startId) {
            removeEnd()
            return
        }
        if(cursorLine == null) createCursorLine()
        console.log("connecting to $endId")
        if (endId == null) {
            cursorLine?.setEnd(cursor.element)
        } else {
            cursorLine?.setEnd(getByIdAsHTML(endId))
        }
    }

    fun removeCursorLine(){
        removeEnd()
        startId = null
    }

    fun removeEnd(){
        cursorLine?.remove()
        cursorLine = null
    }

    private fun onMouseUp(): ((Event) -> Unit) = {
        if(cursorLine != null) {
            try{
                connect()
            } catch(e: Exception){
                println(e)
            }
            removeCursorLine()
        }
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
}