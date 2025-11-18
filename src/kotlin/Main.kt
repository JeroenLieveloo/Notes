import repository.Repository
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.html.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

import domain.model.*
import domain.service.ThreadManager
import io.ktor.server.application.Application


val logger: Logger =    LoggerFactory.getLogger("Main")

fun main() {
    try {
        embeddedServer(Netty, port = 8080) {
            module()
        }.start(wait = true)
    } catch (e: Exception){
        println("Exception: $e.")
    }
}

fun Application.module(){
    val threadManager = ThreadManager(Repository())

    routing {
        staticFiles("/static", File("src/main/resources/static"))

        get("/") {
            try{
                val threads = threadManager.getThreads()
                call.respondHtml {
                    threadsPage(threads)
                }
            } catch (e: Exception){
                logger.debug(e.message)
            }
        }

        post("/save"){
            val params = call.receiveParameters()
            val id = params["id"]?.toIntOrNull()
            val note = params["note"]?.trim().orEmpty()
            val parentId = params["parentId"]?.toIntOrNull()
            val thread = Thread(
                id,
                note
            )
            threadManager.createOrUpdateThread(thread, parentId)
            logger.info("Saved thread: $note")
            call.respondRedirect ("/")
        }

        post("/delete"){
            val params = call.receiveParameters()
            val threadId = params["id"]?.toIntOrNull()
            if (threadId != null){
                threadManager.deleteThread(threadId)
                logger.info("Deleting thread: $threadId")
                call.respondRedirect ("/")
            }
        }

        post("/reset") {
            logger.debug("Resetting database")
            threadManager.reset()
            call.respondRedirect("/")
        }
    }
}