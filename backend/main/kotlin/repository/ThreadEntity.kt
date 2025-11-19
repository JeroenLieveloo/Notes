package repository

data class ThreadEntity (
    val note: String,
    val id: Int? = null,
    val parentId: Int? = null,
    val createdOn: Long? = null,
    val updatedOn: Long? = null,
)
