package net.glitchifyed.quickelytra.helpers;

import net.glitchifyed.quickelytra.client.QuickElytraClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;

public class ContainerHelper {
    public static void swapInventorySlotGeneric(int slotOne, int slotTwo, ContainerInput inputType) {
        Minecraft client = QuickElytraClient.CLIENT;
        LocalPlayer localPlayer = client.player;

        if (client.gameMode == null || localPlayer == null) {
            QuickElytraClient.LOGGER.debug("CLIENT.gameMode or CLIENT.player doesn't exist. Aborting inventory swap..");

            return;
        }

        client.gameMode.handleContainerInput(
                localPlayer.inventoryMenu.containerId,
                slotOne,
                slotTwo,
                inputType,
                localPlayer
        );
    }

    public static void attemptToSwapSlot(int slotId, int equippedSlotId) {
        // if its in the hotbar
        if (slotId < 9) {
            swapInventorySlotGeneric(equippedSlotId, slotId, ContainerInput.SWAP);

            return;
        }

        // do the hacky workaround because mojang added checks if its not in the hotbar (WHY)
        swapInventorySlotGeneric(slotId, 0, ContainerInput.PICKUP);
        swapInventorySlotGeneric(equippedSlotId, 0, ContainerInput.PICKUP);
        swapInventorySlotGeneric(slotId, 0, ContainerInput.PICKUP);
    }
}
