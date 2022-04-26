package io.phoenix.sulfur.api

import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashSet

interface Sulfur {
    val games: HashSet<UUID>

    /** Creates a game */
    fun registerGame(plugin: JavaPlugin, host: UUID, players: HashSet<UUID>): Game
    /** Finds a game */
    fun findGame(id: UUID): Game?
}
