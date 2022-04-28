package io.phoenix.sulfur.api

import org.bukkit.OfflinePlayer
import org.bukkit.World

/** Finds a player from the database associated with this player */
fun OfflinePlayer.gamePlayer(): Game.Player? = Sulfur.sulfur().database.findPlayer(uniqueId)

/** Finds a world from the database associated with this player */
fun World.gameWorld(): Game.World? = Sulfur.sulfur().database.findWorld(name)
