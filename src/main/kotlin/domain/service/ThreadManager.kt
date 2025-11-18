package domain.service

import domain.model.Tag
import domain.model.Thread
import repository.ThreadEntity
import repository.ThreadStorage
import java.time.Instant

class ThreadManager(private val storage: ThreadStorage) {

    suspend fun getThreads(): List<Thread> {
        return buildThreadTree(storage.getAllThreads())
    }

    fun reset(){
        storage.reset()
    }

    private fun buildThreadTree(threadEntityList: List<ThreadEntity>, parentId: Int? = null): List<Thread>{
        return threadEntityList
            .sortedBy { it.id }
            .filter { it.parentId == parentId }
            .map { threadEntity ->
                Thread(
                    threadEntity.id,
                    threadEntity.note,
                    if(threadEntity.createdOn != null) Instant.ofEpochMilli(threadEntity.createdOn) else null,
                    if(threadEntity.updatedOn != null) Instant.ofEpochMilli(threadEntity.updatedOn) else null,
                    emptyList<Tag>(),
                    buildThreadTree(threadEntityList, threadEntity.id)
                )
            }
    }

    suspend fun deleteThread(threadId: Int){
        storage.deleteThread(threadId)
    }

    suspend fun createOrUpdateThread(thread: Thread, parentId: Int?){
        val threadEntity = ThreadEntity(
            thread.note,
            thread.id,
            parentId,
            thread.createdOn.let { it?.toEpochMilli() },
            thread.updatedOn.let { it?.toEpochMilli() }
        )
        if (threadEntity.id == null){
            storage.createThread(threadEntity)
        } else {
            storage.updateThread(threadEntity)
        }
    }
}