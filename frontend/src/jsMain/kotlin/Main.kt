import kotlinx.browser.document
import web.dom.Element as WebElement
import react.dom.client.createRoot
import react.FC
import react.Props
import react.create

val App = FC<Props> { +"Hello from Kotlin/JS!" }

fun main() {
    val container = document.getElementById("root")
        ?: error("Missing <div id=\"root\"> in index.html")

    // cast org.w3c.dom.Element -> web.dom.Element expected by the wrapper
    createRoot(container.unsafeCast<WebElement>()).render(App.create())
}
