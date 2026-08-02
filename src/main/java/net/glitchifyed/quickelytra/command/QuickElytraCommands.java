package net.glitchifyed.quickelytra.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.glitchifyed.quickelytra.client.QuickElytraClient;
import net.glitchifyed.quickelytra.config.QuickElytraConfig;

import static net.minecraft.commands.Commands.*;

public class QuickElytraCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("quickelytra")
                .executes(context -> {
                    // almost used ClientTickEvents.END_CLIENT_TICK to open the screen since it needs to be run on the render thread
                    // then I found this method which is much better
                    QuickElytraClient.CLIENT.execute(() -> QuickElytraClient.CLIENT.setScreenAndShow(QuickElytraConfig.instance().GenerateScreen(QuickElytraClient.CLIENT.gui.screen())));

                    return 1;
                })));
    }
}
