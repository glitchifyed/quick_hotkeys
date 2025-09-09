package net.glitchifyed.quick_hotkeys.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.minecraft.client.gui.screen.Screen;

public class ModMenuIntegration implements ModMenuApi {
    private Screen generateScreen(Screen parentScreen) {
        //QuickHotkeysClient.LOGGER.info("LOADING CONFIG");

        //QuickHotkeysConfig.loadConfig();

        return QuickHotkeysConfig.instance().GenerateScreen(parentScreen);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        /*QuickHotkeysClient.LOGGER.info("LOADING CONFIG");

        QuickHotkeysConfig.loadConfig();

        return QuickHotkeysConfig.HANDLER.instance().GenerateScreen();*/

        return this::generateScreen;
    }
}
