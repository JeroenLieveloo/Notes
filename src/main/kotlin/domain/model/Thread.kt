package domain.model

import kotlinx.html.currentTimeMillis
import java.time.Instant

data class Thread(
    val id: Int? = null,
    val note: String = "",
    val createdOn: Instant? = null,
    val updatedOn: Instant? = null,
    val tags: List<Tag> = emptyList(),
    val subThreads: List<Thread> = emptyList()
)