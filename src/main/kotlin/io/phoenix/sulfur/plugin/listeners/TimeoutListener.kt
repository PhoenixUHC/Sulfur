package io.phoenix.sulfur.plugin.listeners

import io.phoenix.sulfur.api.gamePlayer
import io.phoenix.sulfur.plugin.JavaSulfur
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class TimeoutListener(private val sulfur: JavaSulfur) : Listener {
    private val tasks: HashMap<UUID, Int> = HashMap()

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val game = e.player.gamePlayer()?.game() ?: return

        fun delete() {
            e.player.gamePlayer()?.delete()
        }
        if (game.running()) {
            tasks[e.player.uniqueId] = object: BukkitRunnable() {
                override fun run() {
                    tasks.remove(e.player.uniqueId)
                    delete()
                }
            }.runTaskLater(sulfur, sulfur.config.getLong("game.timeout")).taskId
        } else delete()
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val task = tasks[e.player.uniqueId] ?: return
        Bukkit.getScheduler().cancelTask(task)
    }
}
