package io.phoenix.sulfur.api

import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.plugin.java.JavaPlugin

abstract class SulfurPlugin : JavaPlugin() {
    /** The public brand of your plugin game mode */
    abstract val brand: TextComponent

    /** Called when a game using your plugin was registered */
    fun onRegisterGame(game: Game) {}
}
