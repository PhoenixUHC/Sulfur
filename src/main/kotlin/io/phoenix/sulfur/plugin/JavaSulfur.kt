package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Sulfur
import org.bukkit.plugin.java.JavaPlugin

class JavaSulfur : JavaPlugin(), Sulfur {
    override lateinit var database: Database

    override fun onEnable() {
        saveDefaultConfig()

        database = SulfurDatabase(
            config.getString("redis.host"),
            config.getInt("redis.port"),
        )

        logger.info("Enabled Sulfur")
    }
}
