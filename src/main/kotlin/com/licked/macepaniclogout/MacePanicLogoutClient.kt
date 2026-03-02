package com.licked.macepaniclogout

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

/**
 * Client-side "panic logout" if a nearby player is holding a Mace and moving too fast.
 *
 * - Client-only: runs purely on your client tick loop.
 * - "Regular disconnect": uses the vanilla translatable key "disconnect.disconnected".
 * - Has a Mod Menu config screen (and a client command to open it).
 */
object MacePanicLogoutClient : ClientModInitializer {

    private val disconnectText = Text.translatable("disconnect.disconnected")
    private var cooldownLeft = 0

    override fun onInitializeClient() {
        // Load config once during init
        MacePanicConfigManager.load()

        // Tick logic
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client ->
            onEndClientTick(client)
        })

        // Command to open the config screen: /macepaniclogout config
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("escapeelytramacer")
                    .then(
                        ClientCommandManager.literal("config")
                            .executes {
                                val mc = MinecraftClient.getInstance()
                                mc.execute {
                                    mc.setScreen(MacePanicConfigScreen(mc.currentScreen))
                                }
                                1
                            }
                    )
                    .then(
                        ClientCommandManager.literal("reload")
                            .executes {
                                MacePanicConfigManager.load()
                                1
                            }
                    )
                    .then(
                        ClientCommandManager.literal("test")
                            .executes {
                                val mc = MinecraftClient.getInstance()
                                disconnectNow(mc)
                                1
                            }
                    )
            )
        })
    }

    private fun onEndClientTick(client: MinecraftClient) {
        val me = client.player ?: return
        val world = client.world ?: return
        if (client.networkHandler == null) return

        val cfg = MacePanicConfigManager.config

        // Tick-based cooldown
        if (cooldownLeft > 0) {
            cooldownLeft--
            return
        }

        // Optional: don't scan every tick
        if (cfg.checkEveryTicks > 1 && (world.time % cfg.checkEveryTicks) != 0L) return

        val radiusSq = cfg.radiusBlocks * cfg.radiusBlocks
        val speedThresholdSq = cfg.speedThreshold * cfg.speedThreshold

        for (other in world.players) {
            if (other === me) continue
            if (other.squaredDistanceTo(me) > radiusSq) continue
            if (!other.isHolding(Items.MACE)) continue

            val v: Vec3d = other.velocity

            if (cfg.requireFalling && v.y > cfg.downYThreshold) continue

            // Speed estimate #1: velocity (squared)
            val velSq = v.lengthSquared()

            // Fast path: only compute displacement if velocity isn't already enough.
            val speedSq = if (velSq >= speedThresholdSq) {
                velSq
            } else {
                // Speed estimate #2 (fallback): per-tick displacement (squared)
                val dx = other.x - other.lastX
                val dy = other.y - other.lastY
                val dz = other.z - other.lastZ
                val deltaSq = (dx * dx) + (dy * dy) + (dz * dz)
                maxOf(velSq, deltaSq)
            }

            if (speedSq >= speedThresholdSq) {
                cooldownLeft = cfg.cooldownTicks
                disconnectNow(client)
                return
            }
        }
    }

    private fun disconnectNow(client: MinecraftClient) {
        val handler = client.networkHandler ?: return
        handler.connection.disconnect(disconnectText)
    }
}
