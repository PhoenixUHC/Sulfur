package io.phoenix.sulfur.api

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*
import kotlin.collections.HashSet

interface Game {
    interface Player {
        /** Unique identifier of the player */
        val id: UUID
        /** Whether the player exists */
        fun exists(): Boolean
        /** Metadata hash for this player */
        val metadata: Metadata
        /** Game of the player */
        fun game(): Game
        /** Whether the player was eliminated */
        fun dead(): Boolean
        /** Whether the player was eliminated */
        fun dead(dead: Boolean)
        /** Bukkit player */
        fun bukkitPlayer(): OfflinePlayer = Bukkit.getOfflinePlayer(id)

        /** Removes the player from the database */
        fun delete()
    }

    /** Identifier of this game */
    val id: UUID
    /** Whether the game exists in the database */
    fun exists(): Boolean
    /** Metadata hash for this game */
    val metadata: Metadata
    /** Whether game is running */
    fun running(): Boolean
    /** Name of the server running the game */
    fun server(): String?
    /** Sulfur dependent plugin responsible for this game */
    fun plugin(): SulfurPlugin
    /** Unique identifier of the host for this game */
    fun host(): Player
    /** Unique identifiers of each player participating in this game */
    fun players(): HashSet<Player>
    /** Adds a player to the game */
    fun addPlayer(id: UUID): Player
    /** Finds a player from its unique id */
    fun findPlayer(id: UUID): Player?
    /** Host for this game */
    fun bukkitHost(): OfflinePlayer
    /** List of players participating in this game */
    fun bukkitPlayers(): List<OfflinePlayer>
    /** Removes the game from the database */
    fun delete()
}
