package io.phoenix.sulfur.api

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import redis.clients.jedis.JedisPooled
import java.util.*
import kotlin.collections.HashSet

class Game(
    /** Identifier of this game */
    val id: UUID,

    private val redis: JedisPooled,
) {
    class Player(
        /** Game of the player */
        val game: Game,
        /** Unique identifier of the player */
        val id: UUID,
    ) {
        /** Whether the player exists */
        fun exists(): Boolean = game.redis.sismember("${game.id}:players", id.toString())

        /** Whether the player was eliminated */
        fun dead(): Boolean = game.redis.hget("${game.id}:players:$id", "dead") != "0"

        /** Bukkit player */
        fun bukkitPlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(id)

        /** Removes the player from the database */
        fun delete() {
            game.redis.del("${game.id}:players:$id")
            game.redis.srem("${game.id}:players", id.toString())
        }
    }

    /** Whether the game exists in the database */
    fun exists(): Boolean = redis.sismember("games", id.toString())

    /** Name of the server running the game */
    fun server(): String = redis.hget(id.toString(), "server")

    /** Sulfur dependent plugin responsible for this game */
    fun plugin(): JavaPlugin =
        Bukkit.getPluginManager().getPlugin(redis.hget(id.toString(), "plugin")) as JavaPlugin

    /** Unique identifier of the host for this game */
    fun host(): Player = Player(this, UUID.fromString(redis.hget(id.toString(), "host")))
    /** Unique identifiers of each player participating in this game */
    fun players(): HashSet<Player> = redis
        .smembers("$id:players")
        .map { Player(this, UUID.fromString(it)) }
        .toHashSet()
    /** Adds a player to the game */
    fun addPlayer(id: UUID) {
        redis.hset("${this.id}:players:$id", hashMapOf(
            "dead" to "0",
        ))
        redis.sadd("${this.id}:players", id.toString())
    }
    /** Finds a player from its unique id */
    fun findPlayer(id: UUID): Player? {
        val player = Player(this, id)
        return if (player.exists()) player else null
    }
    /** Removes a player from the game */
    fun removePlayer(id: UUID) {
        redis.del("${this.id}:players:$id")
        redis.srem("${this.id}:players", id.toString())
    }

    /** Host for this game */
    fun bukkitHost(): OfflinePlayer = plugin().server.getOfflinePlayer(host().id)
    /** List of players participating in this game */
    fun bukkitPlayers(): List<OfflinePlayer> = players().map { plugin().server.getOfflinePlayer(it.id) }

    /** Removes the game from the database */
    fun delete() {
        redis.del(id.toString())
        redis.srem("games", id.toString())
        players().forEach { it.delete() }
    }
}
