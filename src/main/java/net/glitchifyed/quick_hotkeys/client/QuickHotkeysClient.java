package net.glitchifyed.quick_hotkeys.client;

import net.fabricmc.api.ClientModInitializer;
import net.glitchifyed.quick_hotkeys.event.KeyInputHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickHotkeysClient implements ClientModInitializer {
    public static final String MODID = "quick_hotkeys";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static MinecraftClient CLIENT;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Getting the client");
        CLIENT = MinecraftClient.getInstance();
        LOGGER.info("Got the client");

        LOGGER.info("Registering keybinds");
        KeyInputHandler.initialiseKeyInputHandler();
        LOGGER.info("Registered keybinds");

        LOGGER.info("Quick hotkeys fully loaded");
    }

    public static void PlaySound(SoundEvent soundEvent, float pitch, float volume) {
        CLIENT.getSoundManager().play(PositionedSoundInstance.master(soundEvent, pitch, volume));
    }

    public static void PlaySound(RegistryEntry.Reference<SoundEvent> soundEvent, float pitch) {
        CLIENT.getSoundManager().play(PositionedSoundInstance.master(soundEvent, pitch));
    }
}
