package domain.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class Note(
    val id: Int,
    var text: String = "",
    val positionX: Int,
    val positionY: Int,
    val createdOn: Instant? = null,
    val updatedOn: Instant? = null,
)

@Serializable
data class NoteRequest(
    val id: Int? = null,
    val text: String,
    val positionX: Int,
    val positionY: Int
)
//
//@Serializable
//data class NoteResponse(
//    val id: Int,
//    val text: String,
//    val positionX: Int,
//    val positionY: Int
//)
//
