package net.glitchifyed.quickelytra.helpers;

import net.glitchifyed.quickelytra.client.QuickElytraClient;
import net.minecraft.network.chat.Component;

public class ClientHudHelper {
    public static void SendClientChatMessage(String text) {
        QuickElytraClient.CLIENT.gui.hud.getChat().addClientSystemMessage(Component.literal(text));
    }

    public static void SendClientChatMessage(Component text) {
        QuickElytraClient.CLIENT.gui.hud.getChat().addClientSystemMessage(text);
    }
}
