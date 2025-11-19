package repository

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Types

class Repository(dbPath: String = "backend/main/kotlin/data/threads.db") : ThreadStorage {
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:${dbPath}")

    init {
        createDatabase()
    }

    fun createDatabase() {
        try {
            connection.createStatement().use {
                    stmt -> stmt.execute(
                """
                    -- threads
                    CREATE TABLE IF NOT EXISTS threads (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        note TEXT,
                        createdOn INTEGER,
                        updatedOn INTEGER,
                        parentId INTEGER,
                        FOREIGN KEY (parentId) REFERENCES threads(id)
                    );
                    """.trimIndent()
            )
            }
        } catch (e: Exception) {
            val newException: Exception = Exception("Error occurred while initializing threadRepository: $e.message")
            throw newException
        }
    }

    override fun reset() {
        connection.createStatement().use { stmt ->
            stmt.execute(
                """
                    DROP TABLE IF EXISTS threads;
                    DROP TABLE IF EXISTS statuses;
                    DROP TABLE IF EXISTS tags;
                """.trimIndent()
            )
        }
        createDatabase()
    }

    override suspend fun createThread(thread: ThreadEntity) {
        connection.prepareStatement(
            """
                INSERT INTO threads (note, createdOn, parentId)
                VALUES (?, ?, ?)
                """.trimIndent()
        ).use { statement ->
            statement.setString(1, thread.note)
            statement.setLong(2,System.currentTimeMillis())
            if(thread.parentId != null) statement.setInt(3, thread.parentId)
            else statement.setNull(3, Types.NULL)
            statement.executeUpdate()
        }
    }

    override suspend fun updateThread(thread: ThreadEntity) {
        connection.prepareStatement(
            """
                UPDATE threads 
                SET note = ?, updatedOn = ?
                WHERE id = ?
                """.trimIndent()
        ).use { statement ->
            statement.setString(1, thread.note)
            statement.setLong(2,System.currentTimeMillis())
            statement.setInt(3, thread.id ?: 0)
            statement.executeUpdate()
        }
    }

    override suspend fun deleteThread(threadId: Int) {
        connection.prepareStatement(
            """
                DELETE FROM threads
                WHERE id = ?
                """.trimIndent()
        ).use { statement ->
            statement.setInt(1, threadId)
            statement.executeUpdate()
        }
    }

    override suspend fun getAllThreads(): List<ThreadEntity> {
        val threads = mutableListOf<ThreadEntity>()
        val query =
            """
                SELECT *
                FROM threads
                ORDER BY id DESC
            """.trimIndent()

        connection.prepareStatement(query).use { statement ->
            val rs = statement.executeQuery()
            while (rs.next()) {
                threads.add(
                    ThreadEntity(
                        rs.getString("note"),
                        rs.getInt("id"),
                        rs.getInt("parentId").let { if (it == 0) null else it },
                        rs.getLong("createdOn").let { if (it == 0.toLong()) null else it },
                        rs.getLong("updatedOn").let { if (it == 0.toLong()) null else it }
                    )
                )
            }
        }
        return threads
    }
}