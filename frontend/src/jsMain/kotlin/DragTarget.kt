import kotlinx.browser.document
import org.w3c.dom.HTMLElement

class DragTarget {
    var targetId: Int? = null
    var targetElement: HTMLElement? = null

    fun setTarget(id: Int) {
        targetId = id
        targetElement = document.getElementById (id.toString()) as HTMLElement
    }

    fun setTarget(id: String) = setTarget(id.toInt())

    fun clearTarget() {
        targetId = null
        targetElement = null
    }

    fun hasTarget(): Boolean {
        return targetId != null
    }
}