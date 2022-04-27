package io.phoenix.sulfur.api

import org.bukkit.plugin.java.JavaPlugin
import java.util.*

interface Sulfur {
    /** Registers a new game */
    fun registerGame(host: UUID, plugin: JavaPlugin, server: String = "server"): Game
    /** Finds a game */
    fun findGame(id: UUID): Game?
}
