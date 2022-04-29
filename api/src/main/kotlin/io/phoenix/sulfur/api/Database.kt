package io.phoenix.sulfur.api

import java.util.*
import kotlin.collections.HashSet

interface Database {
    /**
     * Registers a new game
     *
     * @param host Unique identifier of the player managing this game
     * @param plugin Sulfur plugin responsible for this game
     * @param server Name of the server for this game, only specify when using a proxy
     */
    fun registerGame(host: UUID, plugin: SulfurPlugin, server: String? = null): Game
    /** Every game */
    fun games(): HashSet<Game>
    /** Finds a game from the database */
    fun findGame(id: UUID): Game?
    /**
     * Starts the given game
     *
     * @throws IllegalStateException The game is already running
     */
    fun startGame(game: Game)
    /**
     * Stops the given game
     *
     * @throws IllegalStateException The game is not running
     */
    fun stopGame(game: Game)

    /** Finds a player from the database */
    fun findPlayer(id: UUID): Game.Player?

    /** Finds a world from the database */
    fun findWorld(name: String): Game.World?
}
