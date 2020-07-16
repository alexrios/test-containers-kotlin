package acceptance

import guilda.CacheClient
import io.lettuce.core.RedisClient
import io.lettuce.core.api.sync.RedisCommands
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container

@org.testcontainers.junit.jupiter.Testcontainers
class RedisTest {
    @Container
    var redisContainer: GenericContainer<*> = GenericContainer<Nothing>("redis:5.0.3-alpine")
        .withExposedPorts(6379)

    @Test
    fun testSimplePutAndGet() = postgresTest(redisContainer) { commands ->
        val cacheClient = CacheClient(commands)
        cacheClient.put("test", "example")
        val retrieved: String? = cacheClient.get("test")
        assertEquals("example", retrieved)
    }
}

fun postgresTest(redis: GenericContainer<*>, fn: (connection: RedisCommands<String, String>) -> Unit) {
    //Pre
    val address = redis.getHost()
    val port = redis.getFirstMappedPort()

    val redisClient = RedisClient.create("redis://$address:$port/0")
    val connection = redisClient.connect()
    val redisCommands = connection.sync()

    //Test Body
    fn(redisCommands)

    //Pos
    connection.close()
    redisClient.shutdown()
    redis.stop()
}
