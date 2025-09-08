package net.glitchifyed.quick_hotkeys.mixin;

import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.glitchifyed.quick_hotkeys.config.QuickHotkeysConfig;
import net.glitchifyed.quick_hotkeys.event.KeyInputHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ElytraSwapMixin {
    boolean lastJump = false;
    boolean lastGrounded = true;

    boolean airSwapped = false;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void swapElytra(CallbackInfo info) {
        if (!QuickHotkeysClient.CONFIG.autoSwapEnabled) {
            return;
        }

        ClientPlayerEntity player = (ClientPlayerEntity) (Object)this;

        // jump/ground check
        boolean jumping = player.input.playerInput.jump();
        boolean grounded = player.isOnGround();

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

                KeyInputHandler.attemptElytraSwap(2, false);
            }
        }
        // check if just jumped + flying disabled + not touching water & not levitating, then make sure the player hasnt already auto swapped, and make sure the swap actually succeeded
        else if (!groundedChanged && jumpChanged && jumping && !player.getAbilities().allowFlying && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION)) {
            if (!airSwapped && KeyInputHandler.attemptElytraSwap(1, false)) {
                airSwapped = true;

                player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
        }
    }
}
