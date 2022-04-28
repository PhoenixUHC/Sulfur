package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Metadata
import redis.clients.jedis.JedisPooled

class SulfurMetadata(
    private val redis: JedisPooled,
    private val root: String,
) : Metadata {
    override operator fun set(key: String, value: String) { redis.hset(root, key, value) }
    override operator fun get(key: String): String? = redis.hget(root, key)

    /** Removes the value associated with the given key from the metadata hash */
    override fun remove(key: String) { redis.hdel(root, key) }

    /** Clears the metadata hash from the database */
    override fun clear() { redis.del(root) }
}
