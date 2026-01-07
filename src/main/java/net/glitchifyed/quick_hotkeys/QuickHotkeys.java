package net.glitchifyed.quick_hotkeys;

import net.fabricmc.api.ModInitializer;
import net.glitchifyed.quick_hotkeys.command.QuickHotkeysCommands;
import net.glitchifyed.quick_hotkeys.config.QuickHotkeysConfig;

public class QuickHotkeys implements ModInitializer {
    @Override
    public void onInitialize() {
        QuickHotkeysConfig.loadConfig();
        QuickHotkeysCommands.registerCommands();
    }
}
