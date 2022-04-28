package io.phoenix.sulfur.plugin

import io.phoenix.sulfur.api.Database
import io.phoenix.sulfur.api.Sulfur

class JavaSulfur : Sulfur() {
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
