package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    var startId: Int,
    val endId: Int,
    val typeId: Int
)