package io.phoenix.sulfur.plugin.listeners.scenarios

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.phoenix.sulfur.api.Game
import io.phoenix.sulfur.api.gameWorld
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CutCleanScenarioTest {
    private lateinit var scenario: CutCleanScenario
    private lateinit var g: Game
    private lateinit var gw: Game.World
    private lateinit var bw: World
    private lateinit var d: Collection<ItemStack>
    private lateinit var b: Block
    private lateinit var p: Player

    @BeforeEach
    fun setUp() {
        mockkStatic("io.phoenix.sulfur.api.UtilKt")

        scenario = CutCleanScenario()

        val itemInHand: ItemStack = mockk()

        g = mockk()
        gw = mockk()
        bw = mockk()
        d = mockk()
        b = mockk()
        p = mockk()
        every { gw.game() } returns g
        every { bw.gameWorld() } returns gw
        every { d.isEmpty() } returns false
        every { b.world } returns bw
        every { b.type } returns Material.IRON_ORE
        every { b.x } returns 69
        every { b.y } returns 64
        every { b.z } returns 420
        every { b.getDrops(any()) } returns d
        every { p.itemInHand } returns itemInHand
        every { g.scenario(any()) } returns true

        val droppedItem: Item = mockk()
        every { bw.dropItem(any(), any()) } returns droppedItem
        every { b.type = any() } returns Unit
    }

    @Test
    fun trigger() {
        val e: BlockBreakEvent = mockk()
        every { e.block } returns b
        every { e.player } returns p
        every { g.scenario(any()) } returns false

        scenario.onBlockBreak(e)
        verify (exactly = 0) { bw.dropItem(any(), any()) }

        every { g.scenario(any()) } returns true

        scenario.onBlockBreak(e)
        verify(exactly = 1) { bw.dropItem(any(), any()) }
    }

    @Test
    fun tool() {
        every { d.isEmpty() } returns true

        val e: BlockBreakEvent = mockk()
        every { e.block } returns b
        every { e.player } returns p

        scenario.onBlockBreak(e)
        verify(exactly = 0) { bw.dropItem(any(), any()) }
    }

    @Test
    fun iron() {
        val e: BlockBreakEvent = mockk()
        every { b.type } returns Material.IRON_ORE
        every { e.block } returns b
        every { e.player } returns p

        scenario.onBlockBreak(e)
        verify { bw.dropItem(any(), withArg {
            Assertions.assertEquals(Material.IRON_INGOT, it.type)
        }) }
    }

    @Test
    fun gold() {
        val e: BlockBreakEvent = mockk()
        every { b.type } returns Material.GOLD_ORE
        every { e.block } returns b
        every { e.player } returns p

        scenario.onBlockBreak(e)
        verify { bw.dropItem(any(), withArg {
            Assertions.assertEquals(Material.GOLD_INGOT, it.type)
        }) }
    }

    @Test
    fun anyItem() {
        val e: BlockBreakEvent = mockk()
        every { b.type } returns Material.GRASS
        every { e.block } returns b
        every { e.player } returns p

        scenario.onBlockBreak(e)
        verify(exactly = 0) { bw.dropItem(any(), any()) }
    }
}
