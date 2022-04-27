package io.phoenix.sulfur.api

import redis.clients.jedis.JedisPooled

class Metadata(
    private val redis: JedisPooled,
    private val root: String,
) {
    operator fun set(key: String, value: String) = redis.hset(root, key, value)
    operator fun get(key: String): String = redis.hget(root, key)

    /** Clears the metadata hash from the database */
    fun clear() = redis.del(root)
}