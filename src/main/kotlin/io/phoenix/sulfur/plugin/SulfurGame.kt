package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.SulfurPlugin
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import redis.clients.jedis.JedisPooled
import java.util.*
import kotlin.collections.HashSet

class SulfurGame(
    override val id: UUID,

    private val redis: JedisPooled,
) : Game {
    class SulfurPlayer(
        override val id: UUID,

        private val redis: JedisPooled,
    ) : Game.Player {
        override fun exists(): Boolean = redis.sismember("players", id.toString())
        override val metadata = SulfurMetadata(redis, "players:$id:metadata")
        override fun game(): SulfurGame = SulfurGame(UUID.fromString(redis.hget("players:$id", "game")), redis)
        override fun dead(): Boolean = redis.hget("players:$id", "dead") != "0"
        override fun dead(dead: Boolean) { redis.hset("players:$id", "dead", if (dead) "1" else "0") }
        override fun bukkitPlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(id)
        override fun delete() {
            metadata.clear()
            redis.srem("players", id.toString())
            redis.del("players:$id")
        }
    }

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

    override fun exists(): Boolean = redis.sismember("games", id.toString())
    override val metadata = SulfurMetadata(redis, "games:$id:metadata")
    override fun running(): Boolean = redis.hget("games:$id", "running") == "1"
    override fun server(): String? = redis.hget("games:$id", "server")
    override fun plugin(): SulfurPlugin =
        Bukkit.getPluginManager().getPlugin(redis.hget("games:$id", "plugin")) as SulfurPlugin
    override fun host(): SulfurPlayer = SulfurPlayer(UUID.fromString(redis.hget("games:$id", "host")), redis)
    override fun players(): HashSet<Game.Player> = redis
        .smembers("players")
        .map { SulfurPlayer(UUID.fromString(it), redis) }
        .filter { it.game().id == id }
        .toHashSet()
    override fun addPlayer(id: UUID): SulfurPlayer {
        redis.hset("players:$id", hashMapOf(
            "game" to this.id.toString(),
            "dead" to "0",
        ))
        redis.sadd("players", id.toString())

        return SulfurPlayer(id, redis)
    }
    override fun findPlayer(id: UUID): SulfurPlayer? {
        val player = SulfurPlayer(id, redis)
        return if (player.exists()) player else null
    }
    override fun worlds(): HashSet<Game.World> = redis
        .smembers("worlds")
        .map { SulfurWorld(it, redis) }
        .filter { it.game().id == id }
        .toHashSet()
    override fun addWorld(world: String): Game.World {
        redis.hset("worlds:$world", hashMapOf(
            "game" to id.toString(),
        ))
        redis.sadd("worlds", world)

        return SulfurWorld(world, redis)
    }
    override fun findWorld(world: String): Game.World? {
        val w = SulfurWorld(world, redis)
        return if (w.exists()) w else null
    }
    override fun bukkitHost(): OfflinePlayer = plugin().server.getOfflinePlayer(host().id)
    override fun bukkitPlayers(): List<OfflinePlayer> = players().map { plugin().server.getOfflinePlayer(it.id) }
    override fun delete() {
        players().forEach { it.delete() }
        metadata.clear()
        redis.srem("games", id.toString())
        redis.del(id.toString())
    }
}
