package net.glitchifyed.quickelytra.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.glitchifyed.quickelytra.client.QuickElytraClient;
import net.glitchifyed.quickelytra.config.QuickElytraConfig;
import net.glitchifyed.quickelytra.config.QuickElytraKeybinds;
import net.glitchifyed.quickelytra.enums.ElytraSwapMode;
import net.glitchifyed.quickelytra.helpers.ClientHudHelper;
import net.glitchifyed.quickelytra.helpers.ContainerHelper;
import net.glitchifyed.quickelytra.helpers.ItemHelper;
import net.glitchifyed.quickelytra.lang.QuickElytraLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

// TODO: rewrite this whole class and split it into separate scripts for easier readability
public class KeyInputHandler {
    private static boolean elytraGliding;

    private static final int ARMOUR_SLOT = 6;
    private static final int CHESTPLATE_SLOT = 38;

    private static final int OFFHAND_SLOT1 = 45;
    private static final int OFFHAND_SLOT2 = 40;

    private static int fireworkSlot = -1;

    private static Minecraft CLIENT;
    private static LocalPlayer PLAYER;


    public static void initialiseKeyInputHandler() {
        CLIENT = QuickElytraClient.CLIENT;

        QuickElytraKeybinds.init();
        registerCurrentKeyInputs();
    }


    private static void registerCurrentKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkElytraSwapInput();
            attemptTotemSwap();
            toggleAutoElytra();
        });

        ClientTickEvents.END_LEVEL_TICK.register(level -> {
            checkFireworkSwap();
        });
    }


    private static void updatePlayer() {
        if (CLIENT.player == null)
            return;

        PLAYER = CLIENT.player;
    }


    private static void checkFireworkSwap() {
        updatePlayer();

        boolean lastGliding = elytraGliding;
        elytraGliding = PLAYER.isFallFlying();

        boolean changedGliding = lastGliding != elytraGliding;

        boolean attemptSwap = elytraGliding && fireworkSlot == -1 || !elytraGliding && fireworkSlot != -1;

        if (QuickElytraConfig.fireworkRestockEnabled && elytraGliding && fireworkSlot != -1) {
            ItemStack offhandStack = PLAYER.getOffhandItem();

            if (!ItemHelper.isItemFlyingOffhand(offhandStack)) {
                attemptFireworkSwap();
                attemptFireworkSwap();
            }
        }

        if (!changedGliding || !QuickElytraConfig.fireworkSwapEnabled) {
            return;
        }

        if (attemptSwap) {
            attemptFireworkSwap();
        }
    }

    private static void checkElytraSwapInput() {
        if (!QuickElytraKeybinds.equipElytraKeyBinding.consumeClick())
            return;

        attemptElytraSwap(ElytraSwapMode.SWAP_BOTH, true);
    }

    private static void toggleAutoElytra() {
        if (!QuickElytraKeybinds.toggleAutoElytraBinding.consumeClick())
            return;

        QuickElytraConfig.autoSwapEnabled = !QuickElytraConfig.autoSwapEnabled;

        // let the player know what they did
        if (QuickElytraConfig.chatAlertsEnabled)
            ClientHudHelper.SendClientChatMessage(Component.translatable(QuickElytraLang.STATUS_KEY_AUTO, QuickElytraConfig.autoSwapEnabled ? "✓" : "\uD800\uDD02"));

        if (QuickElytraConfig.soundAlertsEnabled)
            QuickElytraClient.playSound(SoundEvents.ARROW_HIT_PLAYER, QuickElytraConfig.autoSwapEnabled ? 1f : 0.8f, 1f);
    }


    public static boolean attemptElytraSwap(ElytraSwapMode swapMode, boolean playError) {
        updatePlayer();

        Inventory playerInventory = PLAYER.getInventory();
        NonNullList<ItemStack> inventory = playerInventory.getNonEquipmentItems();
        ItemStack chestplateSlot = playerInventory.getItem(CHESTPLATE_SLOT);

        boolean wearingNothing = chestplateSlot.getItem() == Items.AIR;
        boolean wearingElytra = !wearingNothing && ItemHelper.isItemElytra(chestplateSlot);

        if (swapMode == ElytraSwapMode.ONLY_ELYTRA) {
            if (wearingElytra) {
                return false;
            }
        }
        else if (swapMode == ElytraSwapMode.ONLY_CHESTPLATE) {
            if (!wearingElytra) {
                return false;
            }
        }

        if (swapMode == ElytraSwapMode.SWAP_BOTH) {
            ItemStack offhandSlot = PLAYER.getOffhandItem();
            if (ItemHelper.doesItemGoInChestplateSlot(offhandSlot) && (wearingNothing || !wearingElytra && ItemHelper.isItemElytra(offhandSlot) || wearingElytra)) {
                ContainerHelper.swapInventorySlotGeneric(ARMOUR_SLOT, OFFHAND_SLOT2, ContainerInput.SWAP);

                if (QuickElytraConfig.soundAlertsEnabled)
                    QuickElytraClient.playSound(!wearingElytra ? SoundEvents.ARMOR_EQUIP_ELYTRA.value() : SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1f, 1f);

                return true;
            }
        }

        int swapSlot = -1;

        int priorityDurability = -1;
        int priorityEnchants = -1;

        for (ItemStack itemStack : inventory) {
            Item item = itemStack.getItem();
            if (item == Items.AIR || !ItemHelper.doesItemGoInChestplateSlot(itemStack)) {
                continue;
            }

            boolean isElytra = ItemHelper.isItemElytra(itemStack);

            if (wearingElytra == isElytra) {
                continue;
            }

            if (!wearingElytra) {
                int durability = (int) ((float) (itemStack.getMaxDamage() - itemStack.getDamageValue()) * (ItemHelper.getEnchantCountOfItemStack(itemStack) * 0.5f + 1));

                if (durability > priorityDurability) {
                    priorityDurability = durability;
                    swapSlot = inventory.indexOf(itemStack);
                }

                continue;
            }

            int durability = itemStack.getMaxDamage();
            if (durability > priorityDurability) {
                priorityDurability = durability;
                priorityEnchants = -1;
            }

            if (durability < priorityDurability) {
                continue;
            }

            int enchants = ItemHelper.getEnchantCountOfItemStack(itemStack);
            if (enchants <= priorityEnchants) {
                continue;
            }

            priorityEnchants = enchants;
            swapSlot = inventory.indexOf(itemStack);
        }

        if (swapSlot == -1) {
            if (playError && QuickElytraConfig.soundAlertsEnabled)
                QuickElytraClient.playSound(SoundEvents.NOTE_BLOCK_COW_BELL, 1f);

            return false;
        }

        ContainerHelper.attemptToSwapSlot(swapSlot, ARMOUR_SLOT);

        if (QuickElytraConfig.soundAlertsEnabled)
            QuickElytraClient.playSound(!wearingElytra ? SoundEvents.ARMOR_EQUIP_ELYTRA.value() : SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1f, 1f);

        return true;
    }

    private static void attemptTotemSwap() {
        if (!QuickElytraKeybinds.equipTotemKeyBinding.consumeClick())
            return;

        updatePlayer();

        int swapSlot = -1;

        Inventory playerInventory = PLAYER.getInventory();
        NonNullList<ItemStack> inventory = playerInventory.getNonEquipmentItems();

        ItemStack offhandStack = PLAYER.getOffhandItem();

        boolean swapToOne = !ItemHelper.isItemGroundedOffhand(offhandStack);

        for (ItemStack itemStack : inventory) {
            if (swapToOne && !ItemHelper.isItemGroundedOffhand(itemStack) || !swapToOne && !ItemHelper.isItemOffhandOption(itemStack)) {
                continue;
            }

            swapSlot = inventory.indexOf(itemStack);

            break;
        }

        if (swapSlot == -1) {
            if (QuickElytraConfig.soundAlertsEnabled)
                QuickElytraClient.playSound(SoundEvents.NOTE_BLOCK_COW_BELL, 1f);

            return;
        }

        ContainerHelper.attemptToSwapSlot(swapSlot, OFFHAND_SLOT1);

        if (QuickElytraConfig.soundAlertsEnabled)
            QuickElytraClient.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1f, 1f);
    }

    private static void attemptFireworkSwap() {
        updatePlayer();

        int swapSlot = -1;

        if (fireworkSlot != -1) {
            swapSlot = fireworkSlot;
            fireworkSlot = -1;
        }
        else {
            ItemStack offhandSlot = PLAYER.getOffhandItem();
            if (ItemHelper.isItemFlyingOffhand(offhandSlot)) {
                return;
            }

            int highestDuration = -1;
            
            Inventory playerInventory = PLAYER.getInventory();
            NonNullList<ItemStack> inventory = playerInventory.getNonEquipmentItems();
            for (ItemStack itemStack : inventory) {
                if (!ItemHelper.isItemFlyingOffhand(itemStack)) {
                    continue;
                }

                if (highestDuration != -2) {
                    var fireworksComponent = itemStack.get(DataComponents.FIREWORKS);

                    if (fireworksComponent != null) {
                        int duration = fireworksComponent.flightDuration();
                        if (duration <= highestDuration) {
                            continue;
                        }

                        highestDuration = duration;
                        swapSlot = inventory.indexOf(itemStack);

                        continue;
                    }
                }

                highestDuration = -2;
                swapSlot = inventory.indexOf(itemStack);
            }

            if (swapSlot == -1) {
                return;
            }

            fireworkSlot = swapSlot;
        }

        ContainerHelper.attemptToSwapSlot(swapSlot, OFFHAND_SLOT1);
    }
}
