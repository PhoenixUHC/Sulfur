package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.Sulfur
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashSet

class JavaSulfur : JavaPlugin(), Sulfur {
    override val games: HashSet<UUID> = HashSet()

    override fun registerGame(plugin: JavaPlugin, host: UUID, players: HashSet<UUID>): Game {
        val game = Game(
            UUID.randomUUID(),
            "server",
            plugin,
            host,
            players,
        )
        games.add(game.id)
        Database.registerGame(game)
        return game
    }

    override fun findGame(id: UUID): Game? {
        return Database.findGame(id)
    }

    override fun onEnable() {
        Database.init("127.0.0.1", 6379)
    }
}
