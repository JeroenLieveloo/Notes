import kotlinx.html.*
import domain.model.Thread
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import kotlin.toString


val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    .withZone(ZoneId.systemDefault())

fun HTML.threadsPage(threads: List<Thread>) {

    head {
        title { +"My Threads" }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
    }
    body {
        h1 { +"My Threads" }
        renderThreadList(threads)

        form {
            method = FormMethod.post
            action = "/reset"
            submitInput { value = "Reset database" }
        }

        script(src = "/static/app.js") {}
    }
}

fun FlowContent.renderThreadList(threads: List<Thread>, parentId: Int? = null) {

    ul {
        threads.forEach { thread ->
            li {
                // Render thread info
                note(thread, parentId)

                // Recursively render subthreads, if any
                if (thread.subThreads.isNotEmpty()) {
                    renderThreadList(thread.subThreads, thread.id)
                } else  {
                    //card(Thread(), thread.id)
                }
            }
        }
        if (parentId == null){
            li {
                // Create new thread card
                form (classes = "card centered"){
                    method = FormMethod.post
                    action = "/save"

                    submitInput(classes = "primary circle large centered"){
                        value = "+"
                    }
                }
            }
        }
    }
}

fun FlowContent.timestamp(createdOn: Instant?, updatedOn: Instant?){
    if (createdOn != null){
        span(classes = "timestamp") {
            + "Created on: ${formatter.format(createdOn)}"
            if (updatedOn != null){
                + ", Updated on: ${formatter.format(updatedOn)}"
            }
        }
    }
}

fun FlowContent.note(thread: Thread, parentId: Int?) {

    div(classes = "card") {
        div(classes = "spaced-menu") {
            timestamp(thread.createdOn, thread.updatedOn)
            //+ "Id: ${thread.id.toString()}, ParentId: ${parentId.toString()}"
            if(thread.id != null) {
                form{
                    method = FormMethod.post
                    action = "/delete"

                    hiddenInput {
                        name = "id"
                        value = thread.id.toString()
                    }
                    submitInput(classes = "circle small secondary-hover") { value = "" }
                }
            }
        }
        textArea {
            name = "note"
            onFocus = "window.saveThread(${thread.id}, this.value, ${parentId})"
            onBlur = "window.saveThread(${thread.id}, this.value, ${parentId})"
            + thread.note
        }
    }
    form {
        method = FormMethod.post
        action = "/save"

        hiddenInput {
            name = "parentId"
            value = thread.id.toString()
        }
        submitInput(classes = "hidden-action primary"){
            value = "+"
        }
    }
}