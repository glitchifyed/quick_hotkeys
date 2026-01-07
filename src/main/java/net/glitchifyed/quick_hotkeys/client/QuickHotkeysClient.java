package net.glitchifyed.quick_hotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.glitchifyed.quick_hotkeys.event.KeyInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickHotkeysClient implements ClientModInitializer {
    public static final String MODID = "quick_hotkeys";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static Minecraft CLIENT;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Currently loading Quick Hotkeys");

        CLIENT = Minecraft.getInstance();
        LOGGER.info("Got the client");

        KeyInputHandler.initialiseKeyInputHandler();
        LOGGER.info("Registered keybinds");

        LOGGER.info("Quick Hotkeys has been fully loaded");
    }

    public static void playSound(SoundEvent soundEvent, float pitch, float volume) {
        CLIENT.getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume));
    }

    public static void playSound(Holder.Reference<SoundEvent> soundEvent, float pitch) {
        CLIENT.getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch));
    }
}
