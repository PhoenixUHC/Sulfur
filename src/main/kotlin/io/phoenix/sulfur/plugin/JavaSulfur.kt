package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.Sulfur
import org.bukkit.plugin.java.JavaPlugin
import redis.clients.jedis.JedisPooled
import java.util.*

class JavaSulfur : JavaPlugin(), Sulfur {
    private lateinit var redis: JedisPooled

    override fun registerGame(host: UUID, plugin: JavaPlugin, server: String): Game {
        val id = UUID.randomUUID()

        redis.hset(id.toString(), hashMapOf(
            "host" to host.toString(),
            "plugin" to plugin.name,
            "server" to server,
        ))
        redis.sadd("games", id.toString())

        return Game(id,  redis)
    }

    override fun findGame(id: UUID): Game? {
        val game = Game(id, redis)
        return if (game.exists()) game else null
    }

    override fun onEnable() {
        redis = JedisPooled("127.0.0.1", 6379)

        logger.info("Enabled Sulfur")
    }
}
