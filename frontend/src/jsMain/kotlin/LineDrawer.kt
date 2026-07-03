import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import domain.model.Connection
import org.w3c.dom.Element

const val cardOffsetX = 500/2
const val cardOffsetY = 200/2

open class LineDrawer {
    var lines: ArrayList<Line> = arrayListOf()
    var onRefresh: (() -> Unit)? = null

    fun setRefresh(onRefresh: () -> Unit){
        this.onRefresh = onRefresh
    }

    fun resetLeaderLines(connections: List<Connection>) {
        lines.forEach { it.remove() }
        lines = arrayListOf()
        drawLeaderLines(connections)
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

    protected fun getById(id : Int) : Element {
        return document.getElementById(id.toString()) ?: throw NoSuchElementException("Element with id $id not found.")
    }

    protected fun getByIdAsHTML(id : Int) : HTMLElement {
        return getById(id) as HTMLElement
    }
}