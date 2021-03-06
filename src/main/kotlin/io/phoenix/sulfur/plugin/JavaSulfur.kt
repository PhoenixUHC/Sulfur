package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Sulfur
import io.phoenix.sulfur.plugin.commands.SulfurCommand
import io.phoenix.sulfur.plugin.listeners.TimeoutListener

class JavaSulfur : Sulfur() {
    override lateinit var database: Database

    override fun onEnable() {
        saveDefaultConfig()

        server.pluginManager.registerEvents(TimeoutListener(this), this)

        database = SulfurDatabase(
            config.getString("redis.host"),
            config.getInt("redis.port"),
        )

        getCommand("sulfur").let {
            val cmd = SulfurCommand(this)
            it.executor = cmd
            it.tabCompleter = cmd
        }

        logger.info("Enabled Sulfur")
    }

    override fun onDisable() {
        logger.info("Disabling Sulfur, stopping games")

        val i = database.games().iterator()
        while (i.hasNext()) {
            val game = i.next()
            logger.info("- Stopping ${game.id}")
            database.stopGame(game)
        }

        logger.info("Done, bye bye")
    }
}
