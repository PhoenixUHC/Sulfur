package io.phoenix.sulfur.plugin.listeners.scenarios

import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.gameWorld
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class CutCleanScenario : Listener {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val game = e.block.world.gameWorld()?.game()
        if (
            game == null ||
            !game.scenario(Game.Scenario.CUT_CLEAN) ||
            e.block.getDrops(e.player.itemInHand).isEmpty()
        ) return

        val material = when (e.block.type) {
            Material.IRON_ORE -> Material.IRON_INGOT
            Material.GOLD_ORE -> Material.GOLD_INGOT
            else -> null
        } ?: return

        e.block.type = Material.AIR
        e.block.world.dropItem(
            Location(e.block.world, e.block.x + .5, e.block.y + .5, e.block.z + .5), ItemStack(material)
        )
    }

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        val game = e.entity.world.gameWorld()?.game()
        if (game == null || !game.scenario(Game.Scenario.CUT_CLEAN)) return

        for (i in e.drops.indices) {
            val material = when (e.drops[i].type) {
                Material.RAW_BEEF -> Material.COOKED_BEEF
                Material.RAW_CHICKEN -> Material.COOKED_CHICKEN
                Material.RABBIT -> Material.COOKED_RABBIT
                Material.MUTTON -> Material.COOKED_MUTTON
                Material.PORK -> Material.GRILLED_PORK
                else -> null
            } ?: continue

            e.drops[i] = ItemStack(material)
        }
    }
}
