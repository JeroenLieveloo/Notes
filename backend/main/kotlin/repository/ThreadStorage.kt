package repository

interface ThreadStorage {
    suspend fun createThread(thread: ThreadEntity)
    suspend fun updateThread(thread: ThreadEntity)
    suspend fun deleteThread(threadId: Int)
    suspend fun getAllThreads(): List<ThreadEntity>
    fun reset()
}