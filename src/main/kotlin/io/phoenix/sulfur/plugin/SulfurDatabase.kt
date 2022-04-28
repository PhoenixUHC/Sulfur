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

    override fun registerGame(host: UUID, plugin: SulfurPlugin, server: String?): Game {
        val game = Game(UUID.randomUUID(), redis)

        val hash = hashMapOf(
            "host" to host.toString(),
            "plugin" to plugin.name,
            "running" to "0",
        )
        if (server != null) hash["server"] = server

        redis.hset("games:${game.id}", hash)
        redis.sadd("games", game.id.toString())

        plugin.onRegisterGame(game)

        return game
    }

    override fun findGame(id: UUID): Game? {
        val game = Game(id, redis)
        return if (game.exists()) game else null
    }

    override fun startGame(game: Game) {
        if(game.running()) throw IllegalStateException("Game ${game.id} already running")

        redis.hset("games:${game.id}", "running", "1")

        game.plugin().onStartGame(game)
    }

    override fun stopGame(game: Game) {
        if (!game.running()) throw IllegalStateException("Game ${game.id} is not currently running")

        redis.hset("games:${game.id}", "running", "0")

        game.plugin().onStopGame(game)
    }

    override fun findPlayer(id: UUID): Game.Player? {
        val player = Game.Player(id, redis)
        return if (player.exists()) player else null
    }
}
