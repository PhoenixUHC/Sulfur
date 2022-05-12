package io.phoenix.sulfur.plugin.structures

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.plugin.SulfurGame
import redis.clients.jedis.JedisPooled
import java.util.*

class SulfurWorld(
    override val name: String,

    private val redis: JedisPooled,
) : Game.World {
    override fun exists(): Boolean = redis.sismember("worlds", name)
    override fun game(): Game = SulfurGame(UUID.fromString(redis.hget("worlds:$name", "game")), redis)
    override fun delete() {
        redis.srem("worlds", name)
        redis.del("worlds:$name")
    }
}
