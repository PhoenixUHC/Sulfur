package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.SulfurPlugin
import io.phoenix.sulfur.plugin.structures.SulfurPlayer
import io.phoenix.sulfur.plugin.structures.SulfurWorld
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import redis.clients.jedis.JedisPooled
import java.util.*
import kotlin.collections.HashSet

class SulfurGame(
    override val id: UUID,

    private val redis: JedisPooled,
) : Game {
    override fun exists(): Boolean = redis.sismember("games", id.toString())
    override val metadata = SulfurMetadata(redis, "games:$id:metadata")
    override fun running(): Boolean = redis.hget("games:$id", "running") == "1"
    override fun server(): String? = redis.hget("games:$id", "server")
    override fun plugin(): SulfurPlugin =
        Bukkit.getPluginManager().getPlugin(redis.hget("games:$id", "plugin")) as SulfurPlugin

    override fun scenarios(): HashSet<Game.Scenario> = redis
        .smembers("games:$id:scenarios")
        .mapNotNull { a -> Game.Scenario.values().find { b -> a == b.name } }
        .toHashSet()

    override fun scenario(scenario: Game.Scenario, enabled: Boolean) {
        if (enabled) redis.sadd("games:$id:scenarios", scenario.name)
        else redis.srem("games:$id:$scenario", scenario.name)
    }

    override fun scenario(scenario: Game.Scenario): Boolean = redis.sismember("games:$id:scenarios", scenario.name)
    override fun host(): SulfurPlayer = SulfurPlayer(UUID.fromString(redis.hget("games:$id", "host")), redis)
    override fun players(): HashSet<Game.Player> = redis
        .smembers("players")
        .map { SulfurPlayer(UUID.fromString(it), redis) }
        .filter { it.game().id == id }
        .toHashSet()
    override fun addPlayer(id: UUID): SulfurPlayer {
        redis.hset("players:$id", hashMapOf(
            "game" to this.id.toString(),
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
    override fun bukkitPlayers(): List<OfflinePlayer> = players().map { plugin().server.getOfflinePlayer(it.id) }
    override fun delete() {
        players().forEach { it.delete() }
        metadata.clear()
        redis.srem("games", id.toString())
        redis.del(id.toString())
    }
}
