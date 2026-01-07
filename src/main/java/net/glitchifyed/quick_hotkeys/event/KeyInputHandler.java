package net.glitchifyed.quick_hotkeys.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.glitchifyed.quick_hotkeys.config.QuickHotkeysConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath("glitchifyed", "quick_hotkeys"));

    public static final String KEY_ELYTRA = "key.glitchifyed.quick_hotkeys.equip_elytra";
    public static final String KEY_TOTEM = "key.glitchifyed.quick_hotkeys.equip_totem";

    public static final String KEY_AUTO = "key.glitchifyed.quick_hotkeys.automatic_elytra";

    public static KeyMapping equipElytraKeyBinding;
    public static KeyMapping equipTotemKeyBinding;
    public static KeyMapping toggleAutoElytraBinding;

    private static boolean elytraPressed;
    private static boolean totemPressed;
    private static boolean autoPressed;

    private static boolean elytraGliding;

    private static final int ARMOUR_SLOT = 6;
    private static final int CHESTPLATE_SLOT = 38;

    private static final int OFFHAND_SLOT1 = 45;
    private static final int OFFHAND_SLOT2 = 40;

    private static int fireworkSlot = -1;

    private static Minecraft CLIENT;
    private static LocalPlayer PLAYER;

    public static void initialiseKeyInputHandler() {
        CLIENT = QuickHotkeysClient.CLIENT;

        equipElytraKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_ELYTRA,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KEY_CATEGORY
        ));

        equipTotemKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_TOTEM,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KEY_CATEGORY
        ));

        toggleAutoElytraBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_AUTO,
                InputConstants.Type.KEYSYM,
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

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            checkFireworkSwap();
        });
    }

    private static void checkFireworkSwap() {
        boolean lastGliding = elytraGliding;
        elytraGliding = CLIENT.player.isFallFlying();

        boolean changedGliding = lastGliding != elytraGliding;

        boolean attemptSwap = elytraGliding && fireworkSlot == -1 || !elytraGliding && fireworkSlot != -1;

        if (QuickHotkeysConfig.fireworkRestockEnabled && elytraGliding && fireworkSlot != -1) {
            ItemStack offhandStack = CLIENT.player.getOffhandItem();

            if (!isItemFirework(offhandStack)) {
                attemptFireworkSwap();
                attemptFireworkSwap();
            }
        }

        if (!changedGliding || !QuickHotkeysConfig.fireworkSwapEnabled) {
            return;
        }

        if (attemptSwap) {
            attemptFireworkSwap();
        }
    }

    private static void checkElytraSwapInput() {
        if (!equipElytraKeyBinding.isDown()) {
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
        if (!toggleAutoElytraBinding.isDown()) {
            autoPressed = false;

            return;
        }

        if (autoPressed) {
            return;
        }

        autoPressed = true;

        boolean enabled = !QuickHotkeysConfig.autoSwapEnabled;

        QuickHotkeysConfig.autoSwapEnabled = enabled;

        CLIENT.gui.getChat().addMessage(Component.literal(enabled ? "[Quick Hotkeys] Enabled automatic elytra swapping" : "[Quick Hotkeys] Disabled automatic elytra swapping"));
    }

    public static boolean attemptElytraSwap(int swapMode, boolean playError) {
        PLAYER = CLIENT.player;

        Inventory playerInventory = PLAYER.getInventory();
        NonNullList<ItemStack> inventory = playerInventory.getNonEquipmentItems();
        ItemStack chestplateSlot = playerInventory.getItem(CHESTPLATE_SLOT);

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
            ItemStack offhandSlot = PLAYER.getOffhandItem();
            if (doesItemGoInChestplateSlot(offhandSlot) && (wearingNothing || wearingChestplate && isItemElytra(offhandSlot) || wearingElytra)) {
                CLIENT.gameMode.handleInventoryMouseClick(
                        PLAYER.inventoryMenu.containerId,
                        ARMOUR_SLOT,
                        OFFHAND_SLOT2,
                        ClickType.SWAP,
                        PLAYER
                );

                QuickHotkeysClient.playSound(swapIsElytra ? SoundEvents.ARMOR_EQUIP_ELYTRA.value() : SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1f, 1f);

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
                int durability = (int) ((float) (itemStack.getMaxDamage() - itemStack.getDamageValue()) * (getEnchantCountOfItemStack(itemStack) * 0.5f + 1));
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
                QuickHotkeysClient.playSound(SoundEvents.NOTE_BLOCK_COW_BELL, 1f);
            }

            return false;
        }

        attemptToSwapSlot(swapSlot, ARMOUR_SLOT);

        QuickHotkeysClient.playSound(swapIsElytra ? SoundEvents.ARMOR_EQUIP_ELYTRA.value() : SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1f, 1f);

        return true;
    }

    private static void attemptTotemSwap() {
        if (!equipTotemKeyBinding.isDown()) {
            totemPressed = false;

            return;
        }

        if (totemPressed) {
            return;
        }

        totemPressed = true;

        PLAYER = CLIENT.player;

        int swapSlot = -1;

        Inventory playerInventory = PLAYER.getInventory();
        NonNullList<ItemStack> inventory = playerInventory.getNonEquipmentItems();

        ItemStack offhandStack = PLAYER.getOffhandItem();

        boolean swapToOne = !isItemTotem(offhandStack);

        for (ItemStack itemStack : inventory) {
            if (swapToOne && !isItemTotem(itemStack) || !swapToOne && !isItemOffhandOption(itemStack)) {
                continue;
            }

            swapSlot = inventory.indexOf(itemStack);

            break;
        }

        if (swapSlot == -1) {
            QuickHotkeysClient.playSound(SoundEvents.NOTE_BLOCK_COW_BELL, 1f);

            return;
        }

        attemptToSwapSlot(swapSlot, OFFHAND_SLOT1);

        QuickHotkeysClient.playSound(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1f, 1f);
    }

    private static void attemptFireworkSwap() {
        PLAYER = CLIENT.player;

        int swapSlot = -1;

        if (fireworkSlot != -1) {
            swapSlot = fireworkSlot;
            fireworkSlot = -1;
        }
        else {
            ItemStack offhandSlot = PLAYER.getOffhandItem();
            if (isItemFirework(offhandSlot)) {
                return;
            }

            int highestDuration = -1;
            
            Inventory playerInventory = PLAYER.getInventory();
            NonNullList<ItemStack> inventory = playerInventory.getNonEquipmentItems();
            for (ItemStack itemStack : inventory) {
                if (!isItemFirework(itemStack)) {
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

        attemptToSwapSlot(swapSlot, OFFHAND_SLOT1);
    }

    private static int getEnchantCountOfItemStack(ItemStack itemStack) {
        int count = 0;

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

        for (var entry : enchantments.keySet()) {
            count += enchantments.getLevel(entry);
        }

        return count;
    }

    private static boolean doesItemGoInChestplateSlot(ItemStack itemStack) {
        if (itemStack.getItem() == Items.AIR) {
            return false;
        }

        PLAYER = CLIENT.player;

        return PLAYER.getEquipmentSlotForItem(itemStack) == EquipmentSlot.CHEST;
    }

    private static boolean isItemElytra(ItemStack itemStack) {
        return QuickHotkeysConfig.elytraSwapItems.contains(itemStack.getItem());
    }

    private static boolean isItemTotem(ItemStack itemStack) {
        return QuickHotkeysConfig.totemSwapItems.contains(itemStack.getItem());
    }

    private static boolean isItemFirework(ItemStack itemStack) {
        return QuickHotkeysConfig.fireworkSwapItems.contains(itemStack.getItem());
    }

    private static boolean isItemOffhandOption(ItemStack itemStack) {
        return QuickHotkeysConfig.offhandSwapItems.contains(itemStack.getItem());
    }

    private static void attemptToSwapSlot(int slotId, int equippedSlotId) {
        PLAYER = CLIENT.player;

        // if its in the hotbar
        if (slotId < 9) {
            CLIENT.gameMode.handleInventoryMouseClick(
                    PLAYER.inventoryMenu.containerId,
                    equippedSlotId,
                    slotId,
                    ClickType.SWAP,
                    PLAYER
            );
        } else { // do the hacky workaround because mojang added checks if its not in the hotbar (WHY)
            CLIENT.gameMode.handleInventoryMouseClick(
                    PLAYER.inventoryMenu.containerId,
                    slotId,
                    0,
                    ClickType.PICKUP,
                    PLAYER
            );

            CLIENT.gameMode.handleInventoryMouseClick(
                    PLAYER.inventoryMenu.containerId,
                    equippedSlotId,
                    0,
                    ClickType.PICKUP,
                    PLAYER
            );

            CLIENT.gameMode.handleInventoryMouseClick(
                    PLAYER.inventoryMenu.containerId,
                    slotId,
                    0,
                    ClickType.PICKUP,
                    PLAYER
            );
        }
    }
}
