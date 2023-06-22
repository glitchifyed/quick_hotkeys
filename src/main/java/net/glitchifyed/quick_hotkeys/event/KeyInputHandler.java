package net.glitchifyed.quick_hotkeys.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "key.category.glitchifyed.quick_hotkeys";

    public static final String KEY_ELYTRA = "key.glitchifyed.quick_hotkeys.equip_elytra";
    public static final String KEY_TOTEM = "key.glitchifyed.quick_hotkeys.equip_totem";

    public static KeyBinding equipElytraKeyBinding;
    public static KeyBinding equipTotemKeyBinding;

    private static boolean elytraPressed;
    private static boolean totemPressed;

    private static final int ARMOUR_SLOT = 6;
    private static final int OFFHAND_SLOT = 45;

    public static void initialiseKeyInputHandler() {
        equipElytraKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ELYTRA,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY
        ));

        equipTotemKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOTEM,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY
        ));

        registerCurrentKeyInputs();
    }

    public static void registerCurrentKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            attemptElytraSwap();
            attemptTotemSwap();
        });
    }

    private static void attemptElytraSwap() {
        if (!equipElytraKeyBinding.isPressed()) {
            elytraPressed = false;

            return;
        }

        if (elytraPressed) {
            return;
        }

        elytraPressed = true;

        int firstSlot = ARMOUR_SLOT;

        int swapSlot = -1;

        int priorityDurability = -1;
        int priorityEnchants = -1;

        PlayerInventory playerInventory = QuickHotkeysClient.CLIENT.player.getInventory();
        DefaultedList<ItemStack> inventory = playerInventory.main;
        ItemStack chestplateSlot = playerInventory.armor.get(2);

        boolean wearingNothing = chestplateSlot.getItem() == Items.AIR;
        boolean wearingElytra = !wearingNothing && isItemElytra(chestplateSlot);
        boolean wearingChestplate = !wearingElytra;

        boolean swapIsElytra = !wearingElytra;

        ItemStack offhandSlot = QuickHotkeysClient.CLIENT.player.getOffHandStack();
        if (doesItemGoInChestplateSlot(offhandSlot) && (wearingNothing || wearingChestplate && isItemElytra(offhandSlot) || wearingElytra)) {
            firstSlot = OFFHAND_SLOT;
            swapSlot = 38;
        }

        if (swapSlot == -1) {
            for (ItemStack itemStack : inventory) {
                Item item = itemStack.getItem();
                if (item == Items.AIR) {
                    continue;
                }

                if (!doesItemGoInChestplateSlot(itemStack)) {
                    continue;
                }

                boolean isElytra = isItemElytra(itemStack);
                if (wearingNothing || wearingElytra && !isElytra || wearingChestplate) {
                    if (!wearingElytra) {
                        int durability = (int)((float)(itemStack.getMaxDamage() - itemStack.getDamage()) * (getEnchantCountOfItemStack(itemStack) * 0.5f + 1));
                        if (durability <= priorityDurability) {
                            continue;
                        }

                        priorityDurability = durability;
                        swapSlot = inventory.indexOf(itemStack);

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

                    int enchants = getEnchantCountOfItemStack(itemStack);
                    if (enchants <= priorityEnchants) {
                        continue;
                    }

                    priorityEnchants = enchants;
                    swapSlot = inventory.indexOf(itemStack);
                }
            }
        }

        if (swapSlot == -1) {
            QuickHotkeysClient.PlaySound(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, 1f);

            return;
        }

        QuickHotkeysClient.CLIENT.interactionManager.clickSlot(
                QuickHotkeysClient.CLIENT.player.playerScreenHandler.syncId,
                firstSlot,
                swapSlot,
                SlotActionType.SWAP,
                QuickHotkeysClient.CLIENT.player
        );

        QuickHotkeysClient.PlaySound(swapIsElytra ? SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA : SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
    }

    private static void attemptTotemSwap() {
        if (!equipTotemKeyBinding.isPressed()) {
            totemPressed = false;

            return;
        }

        if (totemPressed) {
            return;
        }

        totemPressed = true;

        int swapSlot = -1;

        ItemStack offhandSlot = QuickHotkeysClient.CLIENT.player.getOffHandStack();

        if (isItemTotem(offhandSlot)) {
            QuickHotkeysClient.PlaySound(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, 1f);

            return;
        }

        PlayerInventory playerInventory = QuickHotkeysClient.CLIENT.player.getInventory();
        DefaultedList<ItemStack> inventory = playerInventory.main;
        for (ItemStack itemStack : inventory) {
            if (!isItemTotem(itemStack)) {
                continue;
            }

            swapSlot = inventory.indexOf(itemStack);

            break;
        }

        if (swapSlot == -1) {
            QuickHotkeysClient.PlaySound(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, 1f);

            return;
        }

        QuickHotkeysClient.CLIENT.interactionManager.clickSlot(
                QuickHotkeysClient.CLIENT.player.playerScreenHandler.syncId,
                OFFHAND_SLOT,
                swapSlot,
                SlotActionType.SWAP,
                QuickHotkeysClient.CLIENT.player
        );

        QuickHotkeysClient.PlaySound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
    }

    private static int getEnchantCountOfItemStack(ItemStack itemStack) {
        int count = 0;

        for (var entry : EnchantmentHelper.get(itemStack).entrySet()) {
            count += entry.getValue();
        }

        return count;
    }

    private static boolean itemStackContainsString(ItemStack itemStack, String contains) {
        return itemStack.getItem().getName().getString().toLowerCase().contains(contains.toLowerCase());
    }

    private static boolean doesItemGoInChestplateSlot(ItemStack itemStack) {
        return itemStack.getItem() != Items.AIR && MobEntity.getPreferredEquipmentSlot(itemStack) == EquipmentSlot.CHEST;
    }

    private static boolean checkItemNameForElytra(ItemStack itemStack) {
        return itemStackContainsString(itemStack, "elytra");
    }

    private static boolean isItemElytra(ItemStack itemStack) {
        return doesItemGoInChestplateSlot(itemStack) && checkItemNameForElytra(itemStack);
    }

    private static boolean isItemChestplate(ItemStack itemStack) {
        return doesItemGoInChestplateSlot(itemStack) && !checkItemNameForElytra(itemStack);
    }

    private static boolean isItemTotem(ItemStack itemStack) {
        return itemStackContainsString(itemStack, "totem");
    }
}
