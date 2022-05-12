package io.phoenix.sulfur.plugin.structures

import io.phoenix.sulfur.api.Game
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import redis.clients.jedis.JedisPooled

class SulfurScheduler(private val redis: JedisPooled, private val game: Game) : Game.Scheduler {
    override fun runTaskLater(plugin: Plugin, delay: Long, run: () -> Unit): BukkitTask {
        val task = object: BukkitRunnable() {
            override fun run() {
                run()
                redis.srem("${game.id}:tasks", taskId.toString())
            }
        }.runTaskLater(game.plugin(), delay)
        redis.sadd("${game.id}:tasks", task.taskId.toString())

        return task
    }

    override fun runTaskTimer(plugin: Plugin, delay: Long, period: Long, run: () -> Unit): BukkitTask {
        val task = object: BukkitRunnable() {
            override fun run() {
                run()
                redis.srem("${game.id}:tasks", taskId.toString())
            }
        }.runTaskTimer(game.plugin(), delay, period)
        redis.sadd("${game.id}:tasks", task.taskId.toString())

        return task
    }

    override fun cancel(id: Int) {
        Bukkit.getScheduler().cancelTask(id)
    }

    override fun clear() {
        redis.smembers("${game.id}:tasks").forEach { Bukkit.getScheduler().cancelTask(it.toInt()) }
    }
}
