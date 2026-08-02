package net.glitchifyed.quickelytra.client;

import net.fabricmc.api.ClientModInitializer;
import net.glitchifyed.quickelytra.event.KeyInputHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickElytraClient implements ClientModInitializer {
    public static final String MODID = "quickelytra";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static Minecraft CLIENT;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Currently loading Quick Elytra");

        CLIENT = Minecraft.getInstance();
        LOGGER.info("Got the client");

        KeyInputHandler.initialiseKeyInputHandler();
        LOGGER.info("Registered keybinds");

        LOGGER.info("Quick Elytra has been fully loaded");
    }

    public static void playSound(SoundEvent soundEvent, float pitch, float volume) {
        CLIENT.getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch, volume));
    }

    public static void playSound(Holder.Reference<SoundEvent> soundEvent, float pitch) {
        CLIENT.getSoundManager().play(SimpleSoundInstance.forUI(soundEvent, pitch));
    }
}
