package domain.service

import domain.model.Connection
import repository.ConnectionEntity
import repository.ConnectionStorage

class ConnectionManager(private val storage: ConnectionStorage) {

    suspend fun getConnections(): List<Connection> {
        return storage.getAllConnections().map { connectionEntity ->
            Connection(
                connectionEntity.startId,
                connectionEntity.endId,
                connectionEntity.typeId
            )
        }
    }
//
//    fun reset(){
//        storage.reset()
//    }
//
//    private fun getConnectedNotes(connectionEntityList: List<ConnectionEntity>): List<Connection>{
//        return connectionEntityList
//            .sortedBy { it.id }
//            .map { connection ->
//                Connection(
//                    connection.id,
//                    connection.startId,
//                    connection.endId,
//                    connection.typeId
//                )
//            }
//    }

    suspend fun deleteConnection(connectionId: Int){
        storage.deleteConnection(connectionId)
    }

    suspend fun createOrUpdateConnection(connection: Connection){
        val connectionEntity = ConnectionEntity(connection.startId, connection.endId, connection.typeId)
        storage.createConnection(connectionEntity)

    }
}