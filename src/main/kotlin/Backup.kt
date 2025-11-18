import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.html.*
import java.sql.DriverManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory


// A simple data class for your notes
data class Note(val id: Int, val content: String, val timestamp: Long)

fun main() {
    val logger: Logger = LoggerFactory.getLogger("Main")

    // Connect to a local SQLite database file (creates it if missing)
    val connection = DriverManager.getConnection("jdbc:sqlite:notes.db")

    // Create a table if it doesn’t exist
    connection.createStatement().use { stmt ->
        stmt.execute(
            "CREATE TABLE IF NOT EXISTS notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "content TEXT, " +
                    "timestamp INTEGER)"
        )
    }

    // Start a Ktor server on port 8080
    embeddedServer(Netty, port = 8080) {
        routing {

            // Render the main page
            get("/") {
                logger.info("GET / request received")

                val notes = mutableListOf<Note>()
                connection.createStatement().use { stmt ->
                    val rs = stmt.executeQuery("SELECT id, content, timestamp FROM notes ORDER BY id DESC")
                    while (rs.next()) {
                        notes += Note(
                            rs.getInt("id"),
                            rs.getString("content"),
                            rs.getLong("timestamp")
                        )
                    }
                }

                call.respondHtml {
                    head { title { +"Kotlin Notes App" } }
                    body {
                        h1 { +"My Notes" }

                        // Form to add a new note
                        form(action = "/add", method = FormMethod.post) {
                            textInput(name = "content") {
                                placeholder = "Write something..."
                            }
                            submitInput { value = "Add" }
                        }

                        hr {}
                        ul {
                            notes.forEach { note ->
                                li { +"${note.content} (at ${note.timestamp})" }
                            }
                        }
                    }
                }
            }

            // Handle form submissions
            post("/add") {
                val params = call.receiveParameters()
                val content = params["content"]?.trim().orEmpty()
                logger.debug("Adding note: $content")
                if (content.isNotBlank()) {
                    connection.prepareStatement(
                        "INSERT INTO notes (content, timestamp) VALUES (?, ?)"
                    ).use { stmt ->
                        stmt.setString(1, content)
                        stmt.setLong(2, System.currentTimeMillis())
                        stmt.executeUpdate()
                    }
                    logger.debug("Note added.")
                } else {
                    logger.debug("Not adding note because it was empty.")
                }
                call.respondRedirect("/") // Refresh page
            }
        }
    }.start(wait = true)

    logger.info("Server is running at http://localhost:8080")
}
