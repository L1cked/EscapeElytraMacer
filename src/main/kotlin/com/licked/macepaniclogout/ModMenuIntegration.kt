package com.licked.macepaniclogout

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen

/**
 * Mod Menu finds this class via the "modmenu" entrypoint in fabric.mod.json,
 * and uses it to create the Config screen for the Config button.
 */
class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent -> MacePanicConfigScreen(parent) }
    }
}
