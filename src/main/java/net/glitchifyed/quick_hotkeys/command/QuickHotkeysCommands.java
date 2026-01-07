package net.glitchifyed.quick_hotkeys.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.glitchifyed.quick_hotkeys.config.QuickHotkeysConfig;

import static net.minecraft.server.command.CommandManager.*;

public class QuickHotkeysCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("quickelytra")
                .executes(context -> {
                    // almost used ClientTickEvents.END_CLIENT_TICK to open the screen since it needs to be run on the render thread
                    // then I found this method which is much better
                    QuickHotkeysClient.CLIENT.execute(() -> QuickHotkeysClient.CLIENT.setScreen(QuickHotkeysConfig.instance().GenerateScreen(QuickHotkeysClient.CLIENT.currentScreen)));

                    return 1;
                })));
    }
}
