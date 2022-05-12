package io.phoenix.sulfur.plugin.structures

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.plugin.SulfurGame
import io.phoenix.sulfur.plugin.SulfurMetadata
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import redis.clients.jedis.JedisPooled
import java.util.*

class SulfurPlayer(
    override val id: UUID,

    private val redis: JedisPooled,
) : Game.Player {
    override fun exists(): Boolean = redis.sismember("players", id.toString())
    override val metadata = SulfurMetadata(redis, "players:$id:metadata")
    override fun game(): SulfurGame = SulfurGame(UUID.fromString(redis.hget("players:$id", "game")), redis)
    override fun spectator(): Boolean = redis.hget("players:$id", "spec") == "1"
    override fun spectator(spectator: Boolean) {
        if (spectator) redis.hset("players:$id", "spec", "1")
        else redis.hdel("players:$id", "spec")
    }
    override fun bukkitPlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(id)
    override fun delete() {
        metadata.clear()
        redis.srem("players", id.toString())
        redis.del("players:$id")
    }
}
