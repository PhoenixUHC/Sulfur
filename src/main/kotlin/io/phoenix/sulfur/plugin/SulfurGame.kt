package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.SulfurPlugin
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import redis.clients.jedis.JedisPooled
import java.util.*
import kotlin.collections.HashSet

class SulfurGame(
    /** Identifier of this game */
    override val id: UUID,

    private val redis: JedisPooled,
) : Game {
    class SulfurPlayer(
        /** Unique identifier of the player */
        override val id: UUID,

        private val redis: JedisPooled,
    ) : Game.Player {
        /** Whether the player exists */
        override fun exists(): Boolean = redis.sismember("players", id.toString())

        /** Metadata hash for this player */
        override val metadata = SulfurMetadata(redis, "players:$id:metadata")

        /** Game of the player */
        override fun game(): SulfurGame = SulfurGame(UUID.fromString(redis.hget("players:$id", "game")), redis)

        /** Whether the player was eliminated */
        override fun dead(): Boolean = redis.hget("players:$id", "dead") != "0"
        /** Whether the player was eliminated */
        override fun dead(dead: Boolean) { redis.hset("players:$id", "dead", if (dead) "1" else "0") }

        /** Bukkit player */
        override fun bukkitPlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(id)

        /** Removes the player from the database */
        override fun delete() {
            metadata.clear()
            redis.srem("players", id.toString())
            redis.del("players:$id")
        }
    }

    /** Whether the game exists in the database */
    override fun exists(): Boolean = redis.sismember("games", id.toString())

    /** Metadata hash for this game */
    override val metadata = SulfurMetadata(redis, "games:$id:metadata")

    /** Whether game is running */
    override fun running(): Boolean = redis.hget("games:$id", "running") == "1"

    /** Name of the server running the game */
    override fun server(): String? = redis.hget("games:$id", "server")

    /** Sulfur dependent plugin responsible for this game */
    override fun plugin(): SulfurPlugin =
        Bukkit.getPluginManager().getPlugin(redis.hget("games:$id", "plugin")) as SulfurPlugin

    /** Unique identifier of the host for this game */
    override fun host(): SulfurPlayer = SulfurPlayer(UUID.fromString(redis.hget("games:$id", "host")), redis)
    /** Unique identifiers of each player participating in this game */
    override fun players(): HashSet<Game.Player> = redis
        .smembers("players")
        .map { SulfurPlayer(UUID.fromString(it), redis) }
        .filter { it.game().id == id }
        .toHashSet()
    /** Adds a player to the game */
    override fun addPlayer(id: UUID): SulfurPlayer {
        redis.hset("players:$id", hashMapOf(
            "game" to this.id.toString(),
            "dead" to "0",
        ))
        redis.sadd("players", id.toString())

        return SulfurPlayer(id, redis)
    }
    /** Finds a player from its unique id */
    override fun findPlayer(id: UUID): SulfurPlayer? {
        val player = SulfurPlayer(id, redis)
        return if (player.exists()) player else null
    }

    /** Host for this game */
    override fun bukkitHost(): OfflinePlayer = plugin().server.getOfflinePlayer(host().id)
    /** List of players participating in this game */
    override fun bukkitPlayers(): List<OfflinePlayer> = players().map { plugin().server.getOfflinePlayer(it.id) }

    /** Removes the game from the database */
    override fun delete() {
        players().forEach { it.delete() }
        metadata.clear()
        redis.srem("games", id.toString())
        redis.del(id.toString())
    }
}
