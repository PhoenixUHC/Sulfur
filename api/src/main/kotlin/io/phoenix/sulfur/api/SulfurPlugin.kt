package io.phoenix.sulfur.api

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.plugin.java.JavaPlugin

abstract class SulfurPlugin : JavaPlugin() {
    /** Public brand of your plugin game mode */
    abstract val brand: TextComponent

    /** Name of the world in which players are teleported before the host starts the game */
    abstract val waitingWorld: String

    /** Called when a game using your plugin was registered */
    open fun onRegisterGame(@Suppress("UNUSED_PARAMETER") game: Game) {}

    /** Called when a game using your plugin is starting */
    open fun onStartGame(@Suppress("UNUSED_PARAMETER") game: Game) {}

    /** Called when a game using your plugin is stopping */
    open fun onStopGame(@Suppress("UNUSED_PARAMETER") game: Game) {}
}
