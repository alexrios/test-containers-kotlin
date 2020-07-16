package guilda

import io.lettuce.core.api.sync.RedisCommands

class CacheClient(val redis: RedisCommands<String, String>) {

    fun put(key: String, value: String) {
        redis[key] = value
    }

    fun get(key: String): String? = redis[key]
}
