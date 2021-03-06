package io.phoenix.sulfur.api

import io.phoenix.sulfur.api.event.GameListener
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin

abstract class SulfurPlugin : JavaPlugin() {
    /** Registers a game listener */
    @Suppress("UNCHECKED_CAST")
    fun registerEvents(listener: GameListener) {
        for (handler in listener::class.java.declaredMethods) {
            val annotation = handler.getDeclaredAnnotation(EventHandler::class.java) ?: continue

            if (handler.parameterCount < 1) continue
            val tEvent = if (Event::class.java.isAssignableFrom(handler.parameterTypes[0]))
                handler.parameterTypes[0] as Class<Event>
            else continue

            val executor = when (handler.parameterCount) {
                1 -> EventExecutor { _, e -> handler.invoke(listener, e) }
                2 -> EventExecutor { _, e ->
                    val game = when (e) {
                        is BlockEvent -> e.block.world.gameWorld()?.game()
                        is EntityEvent -> e.entity.world.gameWorld()?.game()
                        is InventoryEvent -> e.view.player.world.gameWorld()?.game()
                        is PlayerEvent -> e.player.gamePlayer()?.game()
                        is VehicleEvent -> e.vehicle.world.gameWorld()?.game()
                        is WeatherEvent -> e.world.gameWorld()?.game()
                        is WorldEvent -> e.world.gameWorld()?.game()
                        else -> null
                    }
                    if (game != null && game.plugin().name == name) handler.invoke(listener, e, game)
                }
                else -> continue
            }

            server.pluginManager.registerEvent(
                tEvent,
                listener,
                annotation.priority,
                executor,
                this,
                annotation.ignoreCancelled,
            )
        }
    }

    /** Called when a game using your plugin was registered */
    open fun onRegisterGame(@Suppress("UNUSED_PARAMETER") game: Game) {}

    /** Called when a game using your plugin is starting */
    open fun onStartGame(@Suppress("UNUSED_PARAMETER") game: Game) {}

    /** Called when a game using your plugin is stopping */
    open fun onStopGame(@Suppress("UNUSED_PARAMETER") game: Game) {}

    /** Called when a player joins a game */
    open fun onAddPlayer(@Suppress("UNUSED_PARAMETER") player: Game.Player) {}

    /** Called when a player leaves a game */
    open fun onRemovePlayer(@Suppress("UNUSED_PARAMETER") player: Game.Player) {}
}
