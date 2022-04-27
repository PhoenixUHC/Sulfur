package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.SulfurPlugin
import redis.clients.jedis.JedisPooled
import java.util.*

class SulfurDatabase(
    host: String,
    port: Int,
) : Database {
    private val redis = JedisPooled(host, port)

    override fun registerGame(host: UUID, plugin: SulfurPlugin, server: String): Game {
        val game = Game(UUID.randomUUID(), redis)

        redis.hset(game.id.toString(), hashMapOf(
            "host" to host.toString(),
            "plugin" to plugin.name,
            "server" to server,
        ))
        redis.sadd("games", game.id.toString())

        plugin.onRegisterGame(game)

        return game
    }

    override fun findGame(id: UUID): Game? {
        val game = Game(id, redis)
        return if (game.exists()) game else null
    }
}
