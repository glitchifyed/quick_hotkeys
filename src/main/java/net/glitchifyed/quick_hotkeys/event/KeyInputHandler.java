package net.glitchifyed.quick_hotkeys.event;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.glitchifyed.quick_hotkeys.QuickHotkeys;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KeyInputHandler {
    public static final String KEY_CATEGORY = "key.category.glitchifyed.quick_hotkeys";

    public static final String KEY_ELYTRA = "key.glitchifyed.quick_hotkeys.equip_elytra";
    public static final String KEY_TOTEM = "key.glitchifyed.quick_hotkeys.equip_totem";

    public static final String KEY_AUTO = "key.glitchifyed.quick_hotkeys.automatic_elytra";

    public static KeyBinding equipElytraKeyBinding;
    public static KeyBinding equipTotemKeyBinding;
    public static KeyBinding toggleAutoElytraBinding;

    private static boolean elytraPressed;
    private static boolean totemPressed;
    private static boolean autoPressed;

    private static final int ARMOUR_SLOT = 6;

    private static final int OFFHAND_SLOT1 = 45;
    private static final int OFFHAND_SLOT2 = 40;

    private static MinecraftClient CLIENT;
    private static ClientPlayerEntity PLAYER;

    public static void initialiseKeyInputHandler() {
        CLIENT = QuickHotkeysClient.CLIENT;

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

        toggleAutoElytraBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_AUTO,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY
        ));

        registerCurrentKeyInputs();
    }

    public static void registerCurrentKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkElytraSwapInput();
            attemptTotemSwap();
            toggleAutoElytra();
        });
    }

    private static void checkElytraSwapInput() {
        if (!equipElytraKeyBinding.isPressed()) {
            elytraPressed = false;

            return;
        }

        if (elytraPressed) {
            return;
        }

        elytraPressed = true;

        attemptElytraSwap(0, true);
    }

    private static void toggleAutoElytra() {
        if (!toggleAutoElytraBinding.isPressed()) {
            autoPressed = false;

            return;
        }

        if (autoPressed) {
            return;
        }

        autoPressed = true;

        boolean enabled = !QuickHotkeysClient.CONFIG.autoSwapEnabled;

        QuickHotkeysClient.CONFIG.autoSwapEnabled = enabled;

        CLIENT.inGameHud.getChatHud().addMessage(Text.literal(enabled ? "[Quick Hotkeys] Enabled automatic elytra swapping" : "[Quick Hotkeys] Disabled automatic elytra swapping"));
    }

    public static boolean attemptElytraSwap(int swapMode, boolean playError) {
        PLAYER = CLIENT.player;

        PlayerInventory playerInventory = PLAYER.getInventory();
        DefaultedList<ItemStack> inventory = playerInventory.main;
        ItemStack chestplateSlot = playerInventory.armor.get(2);

        boolean wearingNothing = chestplateSlot.getItem() == Items.AIR;
        boolean wearingElytra = !wearingNothing && isItemElytra(chestplateSlot);
        boolean wearingChestplate = !wearingElytra;

        boolean swapIsElytra = !wearingElytra;

        boolean swapBothWays = swapMode == 0;
        boolean swapOnlyElytra = swapMode == 1;
        boolean swapOnlyChestplate = swapMode == 2;

        if (swapOnlyElytra) {
            if (wearingElytra) {
                return false;
            }
        }
        else if (swapOnlyChestplate) {
            if (!wearingElytra) {
                return false;
            }
        }

        if (swapBothWays) {
            ItemStack offhandSlot = PLAYER.getOffHandStack();
            if (doesItemGoInChestplateSlot(offhandSlot) && (wearingNothing || wearingChestplate && isItemElytra(offhandSlot) || wearingElytra)) {
                CLIENT.interactionManager.clickSlot(
                        PLAYER.playerScreenHandler.syncId,
                        ARMOUR_SLOT,
                        OFFHAND_SLOT2,
                        SlotActionType.SWAP,
                        PLAYER
                );

                QuickHotkeysClient.PlaySound(swapIsElytra ? SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value() : SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(), 1f, 1f);

                return true;
            }
        }

        int swapSlot = -1;

        int priorityDurability = -1;
        int priorityEnchants = -1;

        for (ItemStack itemStack : inventory) {
            Item item = itemStack.getItem();
            if (item == Items.AIR) {
                continue;
            }

            if (!doesItemGoInChestplateSlot(itemStack)) {
                continue;
            }

            boolean isElytra = isItemElytra(itemStack);

            if (wearingElytra && isElytra) {
                continue;
            }

            if (wearingChestplate && !isElytra) {
                continue;
            }

            if (!wearingElytra) {
                int durability = (int) ((float) (itemStack.getMaxDamage() - itemStack.getDamage()) * (getEnchantCountOfItemStack(itemStack) * 0.5f + 1));
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

        if (swapSlot == -1) {
            if (playError) {
                QuickHotkeysClient.PlaySound(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, 1f);
            }

            return false;
        }

        AttemptToSwapSlot(swapSlot, ARMOUR_SLOT);

        QuickHotkeysClient.PlaySound(swapIsElytra ? SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA.value() : SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(), 1f, 1f);

        return true;
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

        PLAYER = CLIENT.player;

        int swapSlot = -1;

        ItemStack offhandSlot = PLAYER.getOffHandStack();

        if (isItemTotem(offhandSlot)) {
            QuickHotkeysClient.PlaySound(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, 1f);

            return;
        }

        PlayerInventory playerInventory = PLAYER.getInventory();
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

        AttemptToSwapSlot(swapSlot, OFFHAND_SLOT1);

        QuickHotkeysClient.PlaySound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value(), 1f, 1f);
    }

    private static int getEnchantCountOfItemStack(ItemStack itemStack) {
        int count = 0;

        var enchantments = EnchantmentHelper.getEnchantments(itemStack);

        for (var entry : enchantments.getEnchantments()) {
            count += enchantments.getLevel(entry);
        }

        return count;
    }

    private static boolean itemStackContainsString(ItemStack itemStack, String contains) {
        return itemStack.getItem().getName().getString().toLowerCase().contains(contains.toLowerCase());
    }

    // ITS NOT STATIC ANYMORE 😭
    private static boolean doesItemGoInChestplateSlot(ItemStack itemStack) {
        if (itemStack.getItem() == Items.AIR) {
            return false;
        }

        PLAYER = CLIENT.player;

        return PLAYER.getPreferredEquipmentSlot(itemStack) == EquipmentSlot.CHEST;
    }

    private static boolean checkItemNameForElytra(ItemStack itemStack) {
        return itemStackContainsString(itemStack, "elytra");
    }

    private static boolean isItemElytra(ItemStack itemStack) {
        return itemStack.getItem() instanceof ElytraItem;
        //return doesItemGoInChestplateSlot(itemStack) && checkItemNameForElytra(itemStack);
    }

    private static boolean isItemChestplate(ItemStack itemStack) {
        return doesItemGoInChestplateSlot(itemStack) && !isItemElytra(itemStack);
    }

    private static boolean isItemTotem(ItemStack itemStack) {
        return itemStackContainsString(itemStack, "totem");
    }

    private static void AttemptToSwapSlot(int slotId, int equippedSlotId) {
        PLAYER = CLIENT.player;

        // if its in the hotbar
        if (slotId < 9) {
            CLIENT.interactionManager.clickSlot(
                    PLAYER.playerScreenHandler.syncId,
                    equippedSlotId,
                    slotId,
                    SlotActionType.SWAP,
                    PLAYER
            );
        } else { // do the hacky workaround because mojang added checks if its not in the hotbar (WHY)
            CLIENT.interactionManager.clickSlot(
                    PLAYER.playerScreenHandler.syncId,
                    slotId,
                    0,
                    SlotActionType.PICKUP,
                    PLAYER
            );

            CLIENT.interactionManager.clickSlot(
                    PLAYER.playerScreenHandler.syncId,
                    equippedSlotId,
                    0,
                    SlotActionType.PICKUP,
                    PLAYER
            );

            CLIENT.interactionManager.clickSlot(
                    PLAYER.playerScreenHandler.syncId,
                    slotId,
                    0,
                    SlotActionType.PICKUP,
                    PLAYER
            );
        }
    }
}
