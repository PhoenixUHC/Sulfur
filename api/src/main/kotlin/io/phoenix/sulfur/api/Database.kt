package io.phoenix.sulfur.api

import java.util.*

interface Database {
    /**
     * Registers a new game
     *
     * @param host Unique identifier of the player managing this game
     * @param plugin Sulfur plugin responsible for this game
     * @param server Name of the server for this game, only specify when using a proxy
     */
    fun registerGame(host: UUID, plugin: SulfurPlugin, server: String? = null): Game
    /** Finds a game */
    fun findGame(id: UUID): Game?
}
