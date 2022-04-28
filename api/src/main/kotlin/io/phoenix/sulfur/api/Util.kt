package io.phoenix.sulfur.api

import org.bukkit.OfflinePlayer

/** Finds a player from the database associated with this player */
fun OfflinePlayer.gamePlayer(): Game.Player? = Sulfur.sulfur().database.findPlayer(uniqueId)
