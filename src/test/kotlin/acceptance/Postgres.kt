package acceptance

import guilda.Actors
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

@org.testcontainers.junit.jupiter.Testcontainers
class PostgresTest {
    @Container
    var sqlContainer: PostgreSQLContainer<*> = PostgreSQLContainer<Nothing>("postgres:12-alpine").apply {
        withDatabaseName("integration-tests-db")
        withUsername("sa")
        withPassword("sa")
    }


    @Test
    fun testSimplePutAndGet() = sqlTest(sqlContainer) {
        val id = Actors.insert {
            it[firstName] = "Brad"
            it[lastName] = "Pitt"
        } get Actors.id

        val result = Actors.select { Actors.id eq id }.single()

        assertEquals("Brad", result[Actors.firstName])
        assertEquals("Pitt", result[Actors.lastName])
    }
}

fun sqlTest(container: PostgreSQLContainer<*>, fn: () -> Unit) {
    Database.connect(
        "jdbc:postgresql://${container.getHost()}:${container.getFirstMappedPort()}/${container.getDatabaseName()}?sslmode=disable",
        driver = "org.postgresql.Driver",
        user = container.getUsername(),
        password = container.getPassword()
    )

    //Test body and rollback
    transaction {
        SchemaUtils.drop(Actors)
        SchemaUtils.create(Actors)
        fn()
        rollback()
    }
    container.stop()
}
