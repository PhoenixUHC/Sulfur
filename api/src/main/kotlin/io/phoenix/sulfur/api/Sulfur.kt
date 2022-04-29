package io.phoenix.sulfur.api

import org.bukkit.Bukkit

abstract class Sulfur : org.bukkit.plugin.java.JavaPlugin() {
    /** Redis database utility class */
    abstract var database: Database

    companion object {
        /** Sulfur plugin instance */
        fun sulfur(): Sulfur {
            return Bukkit.getPluginManager().getPlugin("Sulfur") as Sulfur
        }
    }
}
