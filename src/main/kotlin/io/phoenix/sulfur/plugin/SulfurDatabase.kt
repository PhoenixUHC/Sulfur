package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.SulfurPlugin
import io.phoenix.sulfur.plugin.structures.SulfurPlayer
import io.phoenix.sulfur.plugin.structures.SulfurWorld
import org.bukkit.Bukkit
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
            "start" to "-1",
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

        redis.hset("games:${game.id}", "start", Bukkit.getWorlds()[0].fullTime.toString())

        game.plugin().onStartGame(game)
    }

    override fun stopGame(game: Game) {
        game.plugin().onStopGame(game)
        game.delete()
    }

    override fun findPlayer(id: UUID): Game.Player? {
        val player = SulfurPlayer(id, redis)
        return if (player.exists()) player else null
    }

    override fun findWorld(name: String): Game.World? {
        val world = SulfurWorld(name, redis)
        return if (world.exists()) world else null
    }
}
