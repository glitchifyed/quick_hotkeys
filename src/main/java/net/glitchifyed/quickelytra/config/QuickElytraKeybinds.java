package net.glitchifyed.quickelytra.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.glitchifyed.quickelytra.client.QuickElytraClient;
import net.glitchifyed.quickelytra.lang.QuickElytraLang;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class QuickElytraKeybinds {
    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath("glitchifyed", QuickElytraClient.MODID));

    public static KeyMapping equipElytraKeyBinding;
    public static KeyMapping equipTotemKeyBinding;
    public static KeyMapping toggleAutoElytraBinding;


    public static void init() {
        equipElytraKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                QuickElytraLang.KEY_ELYTRA,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY
        ));

        equipTotemKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                QuickElytraLang.KEY_TOTEM,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY
        ));

        toggleAutoElytraBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                QuickElytraLang.KEY_AUTO,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY
        ));
    }
}
