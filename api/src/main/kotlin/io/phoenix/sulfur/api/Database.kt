package io.phoenix.sulfur.api

import java.util.*

interface Database {
    /** Registers a new game */
    fun registerGame(host: UUID, plugin: SulfurPlugin, server: String = "server"): Game
    /** Finds a game */
    fun findGame(id: UUID): Game?
}
