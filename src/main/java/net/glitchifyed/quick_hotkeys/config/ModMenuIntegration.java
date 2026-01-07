package net.glitchifyed.quick_hotkeys.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegration implements ModMenuApi {
    private Screen generateScreen(Screen parentScreen) {
        return QuickHotkeysConfig.instance().GenerateScreen(parentScreen);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::generateScreen;
    }
}
