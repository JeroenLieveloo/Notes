package api
import domain.model.*

import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import kotlin.js.json

import kotlinx.serialization.encodeToString

//TODO this url only works on same device as host.
const val url = "http://localhost:8080"

suspend fun getNotes(): List<Note> {
    val response = window.fetch("$url/notes").await()
    val text = response.text().await()
    return Json.decodeFromString(text)
}

suspend fun createNote(x: Int = 0, y: Int = 0): Int {
    println("Creating note.")
    return saveNote(
        NoteRequest(
            null,
            "",
            x,
            y
        )
    )
}

suspend fun createAndConnectNote(connectedId: Int, x: Int = 0, y: Int = 0): Int {
    println("Creating and connecting note to $connectedId.")
    val createdId = saveNote(
        NoteRequest(
            null,
            "",
            x,
            y
        )
    )
    saveConnection(
        Connection(
            connectedId, createdId, 1
        )
    )
    return createdId
}
suspend fun updateNote(request: NoteRequest): Int {
    println("Updating note ${request.id}")
    return saveNote(request)
}



private suspend fun saveNote(note: NoteRequest): Int {
    val response = window.fetch("$url/save",
        RequestInit(
            method = "POST",
            headers = json(
                "Content-Type" to "application/json"
            ),
            body = Json.encodeToString(note)
        )
    ).await()
    return response.text().await().toInt()
}

suspend fun deleteNote(noteId: Int?) {
    console.log("Deleting note $noteId")
    window.fetch("$url/delete?id=$noteId",
        RequestInit(
            method = "DELETE"
        )
    ).await()
}

suspend fun getConnections(): List<Connection> {
    val response = window.fetch("$url/connections").await()
    val text = response.text().await()
    return Json.decodeFromString(text)
}

suspend fun saveConnection (request: Connection) {
    println("Saving connection ${request.startId} to ${request.endId}")
    window.fetch("$url/connect",
        RequestInit(
            method = "POST",
            headers = json(
                "Content-Type" to "application/json"
            ),
            body = Json.encodeToString(request)
        )
    ).await()
}

suspend fun deleteConnection(connectionId: Int?) {
    console.log("Deleting connection $connectionId")
    window.fetch("$url/disconnect?id=$connectionId",
        RequestInit(
            method = "DELETE"
        )
    ).await()
}

suspend fun reset(){
    println("Resetting connection.")
    window.fetch("$url/reset",
        RequestInit(
            method = "POST"
        )
    ).await()
}