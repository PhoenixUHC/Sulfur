package io.phoenix.sulfur.api

/** Finds a player from the database associated with this player */
fun org.bukkit.OfflinePlayer.gamePlayer(): Game.Player? = Sulfur.sulfur().database.findPlayer(uniqueId)

/** Finds a world from the database associated with this player */
fun org.bukkit.World.gameWorld(): Game.World? = Sulfur.sulfur().database.findWorld(name)
