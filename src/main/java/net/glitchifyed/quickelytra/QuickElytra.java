package net.glitchifyed.quickelytra;

import net.fabricmc.api.ModInitializer;
import net.glitchifyed.quickelytra.command.QuickElytraCommands;
import net.glitchifyed.quickelytra.config.QuickElytraConfig;

public class QuickElytra implements ModInitializer {
    @Override
    public void onInitialize() {
        QuickElytraConfig.loadConfig();
        QuickElytraCommands.registerCommands();
    }
}
