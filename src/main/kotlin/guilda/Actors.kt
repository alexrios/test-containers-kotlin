package guilda

import org.jetbrains.exposed.sql.Table

object Actors : Table("actors") {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 256)
    val lastName = varchar("last_name", 256)

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}
