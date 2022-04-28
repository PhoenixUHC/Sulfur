package io.phoenix.sulfur.plugin

import io.mockk.*
import io.phoenix.sulfur.api.Sulfur
import io.phoenix.sulfur.api.SulfurPlugin
import io.phoenix.sulfur.api.gamePlayer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.PluginManager
import org.junit.jupiter.api.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.lang.NullPointerException
import java.util.*

@Testcontainers
class JavaSulfurTest {
    @Container
    val redis = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379)

    lateinit var database: SulfurDatabase
    lateinit var plugin: SulfurPlugin
    lateinit var sulfur: Sulfur

    @BeforeEach
    fun setUp() {
        database = SulfurDatabase(redis.host, redis.firstMappedPort)

        plugin = mockk()
        every { plugin.onRegisterGame(any()) } returns Unit
        every { plugin.name } returns "UHC Sample"

        sulfur = mockk()
        every { sulfur.database } returns database

        mockkStatic(Bukkit::class)
        val pm: PluginManager = mockk()
        every { pm.getPlugin("UHC Sample") } returns plugin
        every { pm.getPlugin("Sulfur") } returns sulfur
        every { Bukkit.getPluginManager() } returns pm
    }

    @Test
    fun basic() {
        val host = UUID.randomUUID()

        val game = database.registerGame(host, plugin)

        verify(exactly = 1) { plugin.onRegisterGame(any()) }
        Assertions.assertEquals(null, game.server())
        Assertions.assertEquals("UHC Sample", game.plugin().name)
        Assertions.assertEquals(host, game.host().id)
    }

    @Test
    fun metadata() {
        val game = database.registerGame(UUID.randomUUID(), plugin)

        game.metadata["foo"] = "bar"
        game.metadata["baz"] = "qux"

        Assertions.assertEquals("qux", game.metadata["baz"])
        Assertions.assertEquals("bar", game.metadata["foo"])
        Assertions.assertEquals(null, game.metadata["none"])

        game.metadata.clear()

        Assertions.assertEquals(null, game.metadata["baz"])

        game.metadata["a"] = "1"
        game.metadata["b"] = "2"
        game.metadata["a"] = "3"

        Assertions.assertEquals("3", game.metadata["a"])

        game.metadata.remove("b")

        Assertions.assertEquals("3", game.metadata["a"])
        Assertions.assertEquals(null, game.metadata["b"])
    }

    @Test
    fun players() {
        val game = database.registerGame(UUID.randomUUID(), plugin)

        val i1 = UUID.randomUUID()
        val i2 = UUID.randomUUID()
        val i3 = UUID.randomUUID()

        val p1 = game.addPlayer(i1)
        val p2 = game.addPlayer(i2)
        val p3 = game.addPlayer(i3)

        p1.metadata["foo"] = "bar"
        p2.dead(true)
        p3.delete()

        Assertions.assertEquals(2, game.players().size)

        Assertions.assertEquals("bar", p1.metadata["foo"])
        Assertions.assertEquals(null, p1.metadata["none"])
        Assertions.assertEquals(null, p2.metadata["foo"])
        Assertions.assertEquals(null, game.metadata["foo"])

        Assertions.assertEquals(false, p1.dead())
        Assertions.assertEquals(true, p2.dead())

        Assertions.assertEquals(true, p1.exists())
        Assertions.assertEquals(true, p2.exists())
        Assertions.assertEquals(false, p3.exists())

        Assertions.assertEquals(game.id, p1.game().id)
        Assertions.assertEquals(game.id, p2.game().id)
        assertThrows<NullPointerException> { p3.game() }

        val op1: OfflinePlayer = mockk()
        every { op1.uniqueId } returns p1.id

        val op2: OfflinePlayer = mockk()
        every { op2.uniqueId } returns p2.id

        Assertions.assertEquals(false, op1.gamePlayer()?.dead())
        Assertions.assertEquals(true, op2.gamePlayer()?.dead())
    }
}
