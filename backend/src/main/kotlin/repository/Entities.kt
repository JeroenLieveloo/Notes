package repository

data class NoteEntity (
    val id: Int? = null,
    val text: String = "",
    val positionX: Int = 0,
    val positionY: Int = 0,
    val createdOn: Long? = null,
    val updatedOn: Long? = null,
)


data class ConnectionEntity (
    val startId: Int,
    val endId: Int,
    val typeId: Int = 1
)

data class ConnectionTypeEntity (
    val id: Int? = null,
    val label: String,
    val order: Int
)