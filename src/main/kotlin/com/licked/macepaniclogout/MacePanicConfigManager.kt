package com.licked.macepaniclogout

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

data class MacePanicConfig(
    var radiusBlocks: Double = 6.0,
    var speedThreshold: Double = 1.10,     // blocks/tick
    var requireFalling: Boolean = true,
    var downYThreshold: Double = -0.50,    // only used if requireFalling == true
    var checkEveryTicks: Long = 1,         // 1 = every tick
    var cooldownTicks: Int = 30            // ~1.5s
)

object MacePanicConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val path: Path = FabricLoader.getInstance().configDir.resolve("escapeelytramacer.json")

    var config: MacePanicConfig = MacePanicConfig()
        private set

    fun load() {
        try {
            if (!path.exists()) {
                save() // write defaults
                return
            }
            val text = path.readText()
            val loaded = gson.fromJson(text, MacePanicConfig::class.java)
            if (loaded != null) {
                config = sanitize(loaded)
            }
        } catch (_: JsonSyntaxException) {
            // If the file is corrupted, keep defaults and overwrite on next save.
            config = MacePanicConfig()
        } catch (_: Exception) {
            // Fail closed: don't crash client init for config issues.
            config = MacePanicConfig()
        }
    }

    fun save() {
        try {
            path.parent?.createDirectories()
            val json = gson.toJson(config)
            path.writeText(json)
        } catch (_: Exception) {
            // Ignore write errors (e.g., read-only FS)
        }
    }

    fun update(mutator: (MacePanicConfig) -> Unit) {
        val c = config.copy()
        mutator(c)
        config = sanitize(c)
    }

    private fun sanitize(c: MacePanicConfig): MacePanicConfig {
        // Clamp to sane ranges (prevents weird values from manual edits)
        c.radiusBlocks = c.radiusBlocks.coerceIn(1.0, 64.0)
        c.speedThreshold = c.speedThreshold.coerceIn(0.01, 10.0)
        c.downYThreshold = c.downYThreshold.coerceIn(-10.0, 10.0)
        c.checkEveryTicks = c.checkEveryTicks.coerceIn(1, 20)
        c.cooldownTicks = c.cooldownTicks.coerceIn(0, 200)
        return c
    }
}
