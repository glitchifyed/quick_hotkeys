package net.glitchifyed.quick_hotkeys.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        QuickHotkeysConfig.loadConfig();

        return QuickHotkeysConfig.HANDLER.instance().GenerateScreen();
    }
}
