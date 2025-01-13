package net.glitchifyed.quick_hotkeys.mixin;

import net.glitchifyed.quick_hotkeys.config.QuickHotkeysConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient.LOGGER;

@Mixin(MinecraftClient.class)
public class JoinWorldMixin {
    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void loadConfigOnWorldJoin(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason worldEntryReason, CallbackInfo ci) {
        QuickHotkeysConfig.loadConfig();
    }
}
