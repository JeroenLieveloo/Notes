package views

import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

val cursor = Cursor()

class Cursor {
    val element = (document.createElement("div") as HTMLElement).apply {
        style.left = "0px"
        style.top = "0px"
        id = "cursor-anchor"
        document.body!!.appendChild(this)
    }

    init {
        document.addEventListener("mousemove", updateCursor())
    }

    private fun updateCursor() : ((Event) -> Unit) = { event ->
        event as MouseEvent
        cursor.setPosition(event.pageX, event.pageY)
    }

    private fun String.numberOfPixels(): Int {
        return if (this.contains("px"))
            this.split("px")[0].toInt()
        else throw NumberFormatException("Can't parse number of pixels as it does not pixels")
    }

    fun getPositionX(): Int {
        return element.style.left.numberOfPixels()
    }

    fun getPositionY(): Int {
        return element.style.top.numberOfPixels()
    }

    fun setPosition(pageX: Double, pageY: Double) {
        element.style.left = "${pageX}px"
        element.style.top = "${pageY}px"
    }


}


