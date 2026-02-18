import js.objects.jso
import org.w3c.dom.HTMLElement

@JsName("LeaderLine")
external class LeaderLine(
    start: HTMLElement,
    end: HTMLElement,
    options: dynamic = definedExternally
) {
    fun position()
    fun remove()
}


class Line(private var startElement: HTMLElement, private var endElement: HTMLElement) {

    private var line: LeaderLine = createLine()

    fun position(){
        line.position()
    }

    fun remove() {
        line.remove()
    }

    fun isConnectedTo(noteElement: HTMLElement): Boolean {
        return noteElement == startElement || noteElement == endElement
    }

    fun setEnd(newEndElement: HTMLElement) {
        if (newEndElement == startElement) return
        endElement = newEndElement
        line.remove()
        line = createLine()
    }

    fun setStart(newStartElement: HTMLElement) {
        if (newStartElement == endElement) return
        startElement = newStartElement
        line.remove()
        line = createLine()
    }

    private fun createLine() : LeaderLine {
        val newLine = LeaderLine(startElement, endElement,
            jso {
                color = "grey"
            })
        return newLine
    }


    fun getStartElement(): HTMLElement = startElement
    fun getEndElement(): HTMLElement = endElement
}