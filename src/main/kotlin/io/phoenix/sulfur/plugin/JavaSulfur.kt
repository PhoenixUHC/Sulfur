package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Sulfur
import io.phoenix.sulfur.plugin.commands.SulfurCommand

class JavaSulfur : Sulfur() {
    override lateinit var database: Database

    override fun onEnable() {
        saveDefaultConfig()

        database = SulfurDatabase(
            config.getString("redis.host"),
            config.getInt("redis.port"),
        )

        getCommand("sulfur").executor = SulfurCommand(this)

        logger.info("Enabled Sulfur")
    }
}
