package repository

import domain.model.NoteRequest
import logger
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.use

class Repository(dbPath: String = "Tasks/backend/src/main/kotlin/data/notes.db") : NoteStorage, ConnectionStorage {
    private val databaseConnection: Connection = DriverManager.getConnection("jdbc:sqlite:${dbPath}")

    init {
//        reset()
        databaseConnection.createStatement().execute("PRAGMA foreign_keys = ON;")
        databaseConnection.createStatement().use { stmt ->
            val rs = stmt.executeQuery("PRAGMA table_info(connections)")
            val columns = mutableSetOf<String>()
            while (rs.next()) {
                columns += rs.getString("name")
            }
            logger.info("Found following columns: ${columns}.")

            require("startId" in columns) {
                "connections table missing startId column: $columns"
            }
        }
    }



    override fun reset() {
        logger.info("Resetting repository...")
        databaseConnection.createStatement().use { stmt ->
            stmt.execute("DROP TABLE IF EXISTS connections;")
            stmt.execute("DROP TABLE IF EXISTS notes;")
            stmt.execute("DROP TABLE IF EXISTS connectionTypes;")
            stmt.execute("DROP TABLE IF EXISTS statuses;")
            stmt.execute("DROP TABLE IF EXISTS tags;")
        }
        createDatabase()
    }

    fun createDatabase() {
        try {
            databaseConnection.createStatement().use {
                stmt -> stmt.execute(
            """
                    -- connections
                    CREATE TABLE IF NOT EXISTS connections (
                        startId INTEGER NOT NULL,
                        endId INTEGER NOT NULL,
                        typeId INTEGER,

                        PRIMARY KEY (startId, endId),

                        FOREIGN KEY (startId)
                            REFERENCES notes(id)
                            ON DELETE CASCADE,
                    
                        FOREIGN KEY (endId)
                            REFERENCES notes(id)
                            ON DELETE CASCADE
                    );
                    """.trimIndent()
                )
            }

            databaseConnection.createStatement().use {
                stmt -> stmt.execute(
                """
                    -- notes
                    CREATE TABLE IF NOT EXISTS notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        note TEXT,
                        positionX INTEGER,
                        positionY INTEGER,
                        createdOn INTEGER,
                        updatedOn INTEGER
                    );
                    
                    
                    """.trimIndent()
                )
            }
        } catch (e: Exception) {
            val newException: Exception = Exception("Error occurred while initializing the repository: ${e.message}")
            throw newException
        }
    }



    override suspend fun createConnection(connection: ConnectionEntity) : Int {
        databaseConnection.prepareStatement(
            """
            INSERT INTO connections (startId, endId, typeId)
            VALUES (?, ?, ?)
            ON CONFLICT(startId, endId)
            DO UPDATE SET typeId = excluded.typeId;
            """.trimIndent(),
            java.sql.Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setInt(1, connection.startId)
            statement.setInt(2, connection.endId)
            statement.setInt(3, 1)
            statement.executeUpdate()
            statement.generatedKeys.use { keys ->
                if (keys.next()) {
                    return keys.getInt(1)
                } else {
                    throw IllegalStateException("Creating connection failed, no ID returned.")
                }
            }
        }
    }

    override suspend fun updateConnection(connection: ConnectionEntity): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteConnection(connectionId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllConnections(): List<ConnectionEntity> {
        val connections = mutableListOf<ConnectionEntity>()
        val query =
            """
            SELECT *
            FROM connections
            ORDER BY startId DESC
            """.trimIndent()

        databaseConnection.prepareStatement(query).use { statement ->
            val rs = statement.executeQuery()
            while (rs.next()) {
                connections.add(
                    ConnectionEntity(
                        rs.getInt("startId"),
                        rs.getInt("endId"),
                        rs.getInt("typeId")
                    )
                )
            }
        }
        return connections
    }


    override suspend fun createNote(note: NoteRequest) : Int {
        databaseConnection.prepareStatement(
            """
            INSERT INTO notes (note, createdOn, positionX, positionY)
            VALUES (?, ?, ?, ?)
            """.trimIndent(),
            java.sql.Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, note.text)
            statement.setLong(2,System.currentTimeMillis())
            statement.setInt(3, note.positionX)
            statement.setInt(4, note.positionY)
            statement.executeUpdate()
            statement.generatedKeys.use { keys ->
                if (keys.next()) {
                    return keys.getInt(1)
                } else {
                    throw IllegalStateException("Creating note failed, no ID returned.")
                }
            }
        }
    }

    override suspend fun updateNote(note: NoteRequest) : Int {
        databaseConnection.prepareStatement(
            """
            UPDATE notes 
            SET note = ?, updatedOn = ?, positionX = ?, positionY = ?
            WHERE id = ?
            """.trimIndent(),
            java.sql.Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, note.text)
            statement.setLong(2,System.currentTimeMillis())
            statement.setInt(3, note.positionX)
            statement.setInt(4, note.positionY)
            statement.setInt(5, note.id ?: 0)
            statement.executeUpdate()
            return note.id?: throw IllegalStateException("Updated note, but no ID was given.")
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        databaseConnection.prepareStatement(
            """
            DELETE FROM notes
            WHERE id = ?
            """.trimIndent()
        ).use { statement ->
            statement.setInt(1, noteId)
            statement.executeUpdate()
        }
    }

    override suspend fun getAllNotes(): List<NoteEntity> {
        val notes = mutableListOf<NoteEntity>()
        val query =
            """
            SELECT *
            FROM notes
            ORDER BY id ASC
            """.trimIndent()

        databaseConnection.prepareStatement(query).use { statement ->
            val rs = statement.executeQuery()
            while (rs.next()) {
                notes.add(getNoteFromResult(rs))
            }
        }
        return notes
    }

    override suspend fun getNoteById(noteId: Int): NoteEntity? {
        val note = mutableListOf<NoteEntity>()
        val query =
            """
            SELECT *
            FROM notes
            WHERE note.id = ?
            """.trimIndent()

        databaseConnection.prepareStatement(query).use { statement ->
            statement.setInt(1, noteId)

            val rs = statement.executeQuery()
            return if (rs.next()) {
                getNoteFromResult(rs)
            } else {
                null
            }
        }
    }

    private fun getNoteFromResult(rs: ResultSet): NoteEntity{
        return NoteEntity(
            rs.getInt("id"),
            rs.getString("note"),
            rs.getInt("positionX"),
            rs.getInt("positionY"),
            rs.getLong("createdOn").let { if (it == 0.toLong()) null else it },
            rs.getLong("updatedOn").let { if (it == 0.toLong()) null else it }
        )
    }
}