package net.glitchifyed.quickelytra.mixin;

import net.glitchifyed.quickelytra.config.QuickElytraConfig;
import net.glitchifyed.quickelytra.enums.ElytraSwapMode;
import net.glitchifyed.quickelytra.event.KeyInputHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ElytraSwapMixin {
    @Unique boolean lastJump = false;
    @Unique boolean lastGrounded = true;

    @Unique boolean airSwapped = false;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void swapElytra(CallbackInfo info) {
        if (!QuickElytraConfig.autoSwapEnabled) {
            return;
        }

        LocalPlayer player = (LocalPlayer) (Object)this;

        // jump/ground check
        boolean jumping = player.input.keyPresses.jump();
        boolean grounded = player.onGround();

        boolean groundedChanged = grounded != lastGrounded;
        boolean jumpChanged = jumping != lastJump;

        lastGrounded = grounded;
        lastJump = jumping;

        // check if the player actually did something
        if (!groundedChanged && !jumpChanged) {
            return;
        }

        // if just landed & actually swapped to elytra, switch back to chestplate
        if (grounded) {
            if (groundedChanged && airSwapped) {
                airSwapped = false;

                KeyInputHandler.attemptElytraSwap(ElytraSwapMode.ONLY_CHESTPLATE, false);
            }
        }
        // check if just jumped + flying disabled + not touching water & not levitating, then make sure the player hasnt already auto swapped, and make sure the swap actually succeeded
        else if (!groundedChanged && jumpChanged && jumping && !player.getAbilities().mayfly && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION) && !player.isPassenger()) {
            if (!airSwapped && KeyInputHandler.attemptElytraSwap(ElytraSwapMode.ONLY_ELYTRA, false)) {
                airSwapped = true;

                player.tryToStartFallFlying();
                player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
        }
    }
}
