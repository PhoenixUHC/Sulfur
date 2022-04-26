package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Game
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import redis.clients.jedis.JedisPooled
import java.util.*

object Database {
    private lateinit var redis: JedisPooled

    fun registerGame(game: Game) {
        val id = game.id

        redis.set("$id:server", game.server)
        redis.set("$id:plugin", game.plugin.name)
        redis.set("$id:host", game.host.toString())
        game.players.forEach { redis.rpush("${game.id}:players", it.toString()) }
    }

    fun findGame(id: UUID): Game? {
        val server = redis.get("$id:server") ?: return null

        val plugin = Bukkit.getPluginManager().getPlugin(redis.get("$id:plugin")) as JavaPlugin
        val host = UUID.fromString(redis.get("$id:host"))
        val players = redis.lrange("$id:players", 0, -1).map { UUID.fromString(it) }

        return Game(
            id,
            server,
            plugin,
            host,
            players.toHashSet(),
        )
    }

    fun deleteGame(id: UUID) {
        redis.del(
            "$id:server",
            "$id:plugin",
            "$id:host",
            "$id:players",
        )
    }

    fun init(host: String, port: Int) {
        redis = JedisPooled(host, port)
    }
}
