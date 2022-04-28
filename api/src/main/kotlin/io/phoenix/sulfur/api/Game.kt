package io.phoenix.sulfur.api

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import redis.clients.jedis.JedisPooled
import java.util.*
import kotlin.collections.HashSet

class Game(
    /** Identifier of this game */
    val id: UUID,

    private val redis: JedisPooled,
) {
    class Player(
        /** Unique identifier of the player */
        val id: UUID,

        private val redis: JedisPooled,
    ) {
        /** Whether the player exists */
        fun exists(): Boolean = redis.sismember("players", id.toString())

        /** Metadata hash for this player */
        val metadata = Metadata(redis, "players:$id:metadata")

        /** Game of the player */
        fun game(): Game = Game(UUID.fromString(redis.hget("players:$id", "game")), redis)

        /** Whether the player was eliminated */
        fun dead(): Boolean = redis.hget("players:$id", "dead") != "0"
        /** Whether the player was eliminated */
        fun dead(dead: Boolean) = redis.hset("players:$id", "dead", if (dead) "1" else "0")

        /** Bukkit player */
        fun bukkitPlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(id)

        /** Removes the player from the database */
        fun delete() {
            metadata.clear()
            redis.srem("players", id.toString())
            redis.del("players:$id")
        }
    }

    /** Whether the game exists in the database */
    fun exists(): Boolean = redis.sismember("games", id.toString())

    /** Metadata hash for this game */
    val metadata = Metadata(redis, "games:$id:metadata")

    /** Whether game is running */
    fun running(): Boolean = redis.hget("games:$id", "running") == "1"

    /** Name of the server running the game */
    fun server(): String? = redis.hget("games:$id", "server")

    /** Sulfur dependent plugin responsible for this game */
    fun plugin(): SulfurPlugin =
        Bukkit.getPluginManager().getPlugin(redis.hget("games:$id", "plugin")) as SulfurPlugin

    /** Unique identifier of the host for this game */
    fun host(): Player = Player(UUID.fromString(redis.hget("games:$id", "host")), redis)
    /** Unique identifiers of each player participating in this game */
    fun players(): HashSet<Player> = redis
        .smembers("players")
        .map { Player(UUID.fromString(it), redis) }
        .filter { it.game().id == id }
        .toHashSet()
    /** Adds a player to the game */
    fun addPlayer(id: UUID): Player {
        redis.hset("players:$id", hashMapOf(
            "game" to this.id.toString(),
            "dead" to "0",
        ))
        redis.sadd("players", id.toString())

        return Player(id, redis)
    }
    /** Finds a player from its unique id */
    fun findPlayer(id: UUID): Player? {
        val player = Player(id, redis)
        return if (player.exists()) player else null
    }

    /** Host for this game */
    fun bukkitHost(): OfflinePlayer = plugin().server.getOfflinePlayer(host().id)
    /** List of players participating in this game */
    fun bukkitPlayers(): List<OfflinePlayer> = players().map { plugin().server.getOfflinePlayer(it.id) }

    /** Removes the game from the database */
    fun delete() {
        players().forEach { it.delete() }
        metadata.clear()
        redis.srem("games", id.toString())
        redis.del(id.toString())
    }
}
