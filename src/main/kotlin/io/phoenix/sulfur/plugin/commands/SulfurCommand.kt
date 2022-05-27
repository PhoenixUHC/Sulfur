package io.phoenix.sulfur.plugin.commands

import io.phoenix.sulfur.api.SulfurPlugin
import io.phoenix.sulfur.api.gamePlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class SulfurCommand(
    private val sulfur: io.phoenix.sulfur.plugin.JavaSulfur,
) : org.bukkit.command.CommandExecutor, TabCompleter {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Must be a player")
            return false
        }

        if (args.isEmpty()) {
            sender.sendMessage("Not enough arguments")
            return false
        }

        when (args[0]) {
            "create" -> {
                if (args.size < 2) {
                    sender.sendMessage("Missing plugin argument")
                    return false
                }
                val plugin = Bukkit.getPluginManager().getPlugin(args[1])
                if (plugin == null || plugin !is SulfurPlugin) {
                    sender.sendMessage("Invalid plugin \"${args[1]}\"")
                    return false
                }
                sulfur.database.registerGame(sender.uniqueId, plugin)
                return true
            }
            "start" -> {
                val game = sender.gamePlayer()?.game()
                if (game == null) {
                    sender.sendMessage("No game found")
                    return false
                }

                sulfur.database.startGame(game)

                return true
            }
            "get" -> {
                val game = sender.gamePlayer()?.game()
                if (game == null) {
                    sender.sendMessage("No game found")
                    return false
                }

                sender.sendMessage("Players: ${game.players().size}, Plugin: ${game.plugin().name}")

                return true
            }
            "stop" -> {
                val game = sender.gamePlayer()?.game()
                if (game == null) {
                    sender.sendMessage("No game found")
                    return false
                }

                sulfur.database.stopGame(game)

                return true
            }
            else -> {
                sender.sendMessage("Unknown option \"${args[0]}\"")
                return false
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String> {
        val completions = when (args.size) {
            1 -> listOf("create", "start", "get", "stop")
            2 -> if (args[0] == "create")
                Bukkit.getPluginManager().plugins.filterIsInstance<SulfurPlugin>().map { it.name }
            else null
            else -> null
        } ?: return MutableList(0) { "" }

        val result: MutableList<String> = ArrayList()
        StringUtil.copyPartialMatches(args[args.size - 1], completions, result)

        result.sort()
        return result
    }
}
