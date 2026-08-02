package net.glitchifyed.quickelytra.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuIntegration implements ModMenuApi {
    private Screen generateScreen(Screen parentScreen) {
        return QuickElytraConfig.instance().GenerateScreen(parentScreen);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::generateScreen;
    }
}
