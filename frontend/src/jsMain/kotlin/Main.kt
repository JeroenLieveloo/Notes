import react.create
import react.dom.client.createRoot
import web.dom.document

fun main() {
    val root = document.getElementById("root")
        ?: error("Missing <div id=\"root\"> in index.html")

    createRoot(root).render(App.create())
}
