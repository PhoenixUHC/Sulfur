package io.phoenix.sulfur.api

import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.HashSet

class Game(
    /** Identifier of this game */
    val id: UUID,

    /** Name of the server running the game */
    val server: String,

    /** Sulfur dependent plugin responsible for this game */
    val plugin: JavaPlugin,

    /** Unique identifier of the host for this game */
    val host: UUID,
    /** Unique identifiers of each player participating in this game */
    val players: HashSet<UUID>,
) {
    /** Host for this game */
    fun host(): OfflinePlayer = plugin.server.getOfflinePlayer(host)
    /** List of players participating in this game */
    fun players(): List<OfflinePlayer> = players.map { plugin.server.getOfflinePlayer(it) }
}
