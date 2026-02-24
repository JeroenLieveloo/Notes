import repository.Repository

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
//import io.ktor.server.html.*
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import domain.model.*
import domain.service.NoteManager
import domain.service.ConnectionManager
import io.ktor.server.request.receive
import kotlinx.io.files.Path
import java.io.File

val logger: Logger = LoggerFactory.getLogger("Main")

fun main() {
    try {
        embeddedServer(Netty, port = 8080) {
            module()
        }.start(wait = true)
    } catch (e: Exception) {
        println("Exception: $e.")
    }
}

fun Application.module() {
    val noteManager = NoteManager(Repository())
    val connectionManager = ConnectionManager(Repository())
    corsConfiguration()

    install(ContentNegotiation) {
        json()
    }

    routing {

        //staticResources("/static", "static", "index.html")
        val buildLocation = File("Tasks/frontend/build/dist/js/developmentExecutable").absoluteFile
        staticFiles("/", buildLocation) {
            default("index.html")
        }

        get("/notes") {
            call.respond(noteManager.getNotes())
        }

        get("/connections") {
            call.respond(connectionManager.getConnections())
        }

        post("/save") {
            val request = call.receive<NoteRequest>()
            val id = noteManager.createOrUpdateNote(request)
            logger.info("Saved note: ${request.id}")
            call.respond(id)
        }

        post("/connect"){
            val request = call.receive<Connection>()
            connectionManager.createOrUpdateConnection(request)
            logger.info("Saved connection between: ${request.startId} and ${request.endId}.")
            call.respond(HttpStatusCode.OK)
        }

        delete("/disconnect") {
            val connectionId = call.queryParameters["id"]?.toIntOrNull()?: throw IllegalArgumentException("query parameter missing")
            connectionManager.deleteConnection(connectionId)
            logger.info("Deleting connection: $connectionId")
            call.respond(HttpStatusCode.OK)
        }

        delete("/delete") {
            val noteId = call.queryParameters["id"]?.toIntOrNull()?: throw IllegalArgumentException("query parameter missing")
            noteManager.deleteNote(noteId)
            logger.info("Deleting note: $noteId")
            call.respond(HttpStatusCode.OK)
        }

        post("/reset") {
            logger.debug("Resetting database")
            noteManager.reset()
            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun Application.corsConfiguration() {
    install(CORS) {
        allowHost("localhost:3000", schemes = listOf("http"))
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
    }
}