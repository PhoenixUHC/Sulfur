package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.SulfurPlugin
import redis.clients.jedis.JedisPooled
import java.util.*
import kotlin.collections.HashSet

class SulfurDatabase(
    host: String,
    port: Int,
) : Database {
    private val redis = JedisPooled(host, port)

    override fun registerGame(host: UUID, plugin: SulfurPlugin, server: String?): SulfurGame {
        val game = SulfurGame(UUID.randomUUID(), redis)

        val hash = hashMapOf(
            "host" to host.toString(),
            "plugin" to plugin.name,
        )
        if (server != null) hash["server"] = server

        redis.hset("games:${game.id}", hash)
        redis.sadd("games", game.id.toString())

        game.addPlayer(host)

        plugin.onRegisterGame(game)

        return game
    }

    override fun games(): HashSet<Game> {
        return redis
            .smembers("games")
            .map { SulfurGame(UUID.fromString(it), redis) }
            .toHashSet()
    }
    override fun findGame(id: UUID): Game? {
        val game = SulfurGame(id, redis)
        return if (game.exists()) game else null
    }

    override fun startGame(game: Game) {
        if(game.running()) throw IllegalStateException("Game ${game.id} already running")

        redis.hset("games:${game.id}", "running", "1")

        game.plugin().onStartGame(game)
    }

    override fun stopGame(game: Game) {
        if (!game.running()) throw IllegalStateException("Game ${game.id} is not currently running")

        game.plugin().onStopGame(game)
        game.delete()
    }

    override fun findPlayer(id: UUID): Game.Player? {
        val player = SulfurGame.SulfurPlayer(id, redis)
        return if (player.exists()) player else null
    }

    override fun findWorld(name: String): Game.World? {
        val world = SulfurGame.SulfurWorld(name, redis)
        return if (world.exists()) world else null
    }
}
