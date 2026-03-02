package com.licked.macepaniclogout

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.text.Text
import kotlin.math.roundToInt

class MacePanicConfigScreen(private val parent: Screen?) : Screen(Text.literal("EscapeElytraMacer")) {

    private val snapshot = MacePanicConfigManager.config.copy()

    private lateinit var radiusSlider: DoubleSlider
    private lateinit var speedSlider: DoubleSlider
    private lateinit var downYSlider: DoubleSlider
    private lateinit var fallingToggle: ButtonWidget
    private var initError: String? = null

    override fun init() {
        clearChildren()
        super.init()

        try {
            val c = MacePanicConfigManager.config
            val centerX = width / 2
            val startY = 52
            val row = 24
            val w = (width.coerceAtMost(320) - 40).coerceAtLeast(140)
            val x = centerX - w / 2

            radiusSlider = DoubleSlider(
                x, startY, w, 20,
                Text.literal("Radius"),
                c.radiusBlocks,
                1.0, 20.0
            ) { newValue ->
                MacePanicConfigManager.update { it.radiusBlocks = newValue }
            }
            addDrawableChild(radiusSlider)

            speedSlider = DoubleSlider(
                x, startY + row, w, 20,
                Text.literal("Speed threshold"),
                c.speedThreshold,
                0.2, 3.5
            ) { newValue ->
                MacePanicConfigManager.update { it.speedThreshold = newValue }
            }
            addDrawableChild(speedSlider)

            fallingToggle = ButtonWidget.builder(toggleText(c.requireFalling)) { _ ->
                val now = !MacePanicConfigManager.config.requireFalling
                MacePanicConfigManager.update { it.requireFalling = now }
                fallingToggle.message = toggleText(now)
                downYSlider.active = now
            }.dimensions(x, startY + row * 2, w, 20).build()
            addDrawableChild(fallingToggle)

            downYSlider = DoubleSlider(
                x, startY + row * 3, w, 20,
                Text.literal("Downward Y required"),
                c.downYThreshold,
                -2.5, 0.0
            ) { newValue ->
                MacePanicConfigManager.update { it.downYThreshold = newValue }
            }
            downYSlider.active = c.requireFalling
            addDrawableChild(downYSlider)

            // Bottom buttons
            val bw = 120
            val by = height - 34
            addDrawableChild(
                ButtonWidget.builder(Text.translatable("gui.done")) {
                    MacePanicConfigManager.save()
                    close()
                }.dimensions(centerX - bw - 6, by, bw, 20).build()
            )
            addDrawableChild(
                ButtonWidget.builder(Text.translatable("gui.cancel")) {
                    // revert to snapshot
                    MacePanicConfigManager.update {
                        it.radiusBlocks = snapshot.radiusBlocks
                        it.speedThreshold = snapshot.speedThreshold
                        it.requireFalling = snapshot.requireFalling
                        it.downYThreshold = snapshot.downYThreshold
                        it.checkEveryTicks = snapshot.checkEveryTicks
                        it.cooldownTicks = snapshot.cooldownTicks
                    }
                    close()
                }.dimensions(centerX + 6, by, bw, 20).build()
            )
        } catch (t: Throwable) {
            initError = "${t::class.simpleName}: ${t.message ?: "unknown error"}"
            t.printStackTrace()
            addDrawableChild(
                ButtonWidget.builder(Text.translatable("gui.done")) { close() }
                    .dimensions(width / 2 - 60, height - 34, 120, 20)
                    .build()
            )
        }
    }

    override fun close() {
        client?.setScreen(parent)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // Avoid Screen#renderBackground blur path (can throw on Lunar: "Can only blur once per frame").
        context.fill(0, 0, width, height, 0x66000000)
        super.render(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFFFF.toInt())
        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Also available via /escapeelytramacer config"),
            width / 2,
            34,
            0xFFA0A0A0.toInt()
        )
        initError?.let { error ->
            context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("UI init error: $error"),
                width / 2,
                48,
                0xFFFF5555.toInt()
            )
        }
    }

    private fun toggleText(on: Boolean): Text {
        return Text.literal("Require falling: " + if (on) "ON" else "OFF")
    }

    private class DoubleSlider(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        label: Text,
        startValue: Double,
        min: Double,
        max: Double,
        onChanged: (Double) -> Unit
    ) : SliderWidget(x, y, width, height, Text.empty(), 0.0) {

        // SliderWidget may call updateMessage() during superclass construction.
        // Keep safe defaults until we assign real values in init.
        private var labelText: Text? = null
        private var minValue: Double = 0.0
        private var maxValue: Double = 1.0
        private var onChangedHandler: ((Double) -> Unit)? = null

        init {
            labelText = label
            minValue = min
            maxValue = max
            onChangedHandler = onChanged

            val range = (maxValue - minValue).takeIf { it != 0.0 } ?: 1.0
            value = ((startValue - minValue) / range).coerceIn(0.0, 1.0)
            updateMessage()
        }

        override fun applyValue() {
            onChangedHandler?.invoke(getActualValue())
        }

        override fun updateMessage() {
            val v = getActualValue()
            val shown = ((v * 100.0).roundToInt() / 100.0)
            val prefix = labelText?.string ?: ""
            message = Text.literal("$prefix: $shown")
        }

        private fun getActualValue(): Double {
            return minValue + (maxValue - minValue) * value
        }
    }
}
