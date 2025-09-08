package net.glitchifyed.quick_hotkeys.client;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import net.fabricmc.api.ClientModInitializer;
import net.glitchifyed.quick_hotkeys.config.QuickHotkeysConfig;
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

    public static QuickHotkeysConfig CONFIG = QuickHotkeysConfig.createInstance();

    @Override
    public void onInitializeClient() {
        LOGGER.info("Currently loading Quick Hotkeys");

        CLIENT = MinecraftClient.getInstance();
        LOGGER.info("Got the client");

        KeyInputHandler.initialiseKeyInputHandler();
        LOGGER.info("Registered keybinds");

        LOGGER.info("Quick Hotkeys has been fully loaded");
    }

    public static void PlaySound(SoundEvent soundEvent, float pitch, float volume) {
        CLIENT.getSoundManager().play(PositionedSoundInstance.master(soundEvent, pitch, volume));
    }

    public static void PlaySound(RegistryEntry.Reference<SoundEvent> soundEvent, float pitch) {
        CLIENT.getSoundManager().play(PositionedSoundInstance.master(soundEvent, pitch));
    }
}
