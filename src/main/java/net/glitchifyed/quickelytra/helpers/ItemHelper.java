package net.glitchifyed.quickelytra.helpers;

import net.glitchifyed.quickelytra.client.QuickElytraClient;
import net.glitchifyed.quickelytra.config.QuickElytraConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ItemHelper {
    public static int getEnchantCountOfItemStack(ItemStack itemStack) {
        int count = 0;

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

        for (var entry : enchantments.keySet()) {
            count += enchantments.getLevel(entry);
        }

        return count;
    }

    public static boolean doesItemGoInChestplateSlot(ItemStack itemStack) {
        if (itemStack.getItem() == Items.AIR) {
            return false;
        }

        return QuickElytraClient.CLIENT.player.getEquipmentSlotForItem(itemStack) == EquipmentSlot.CHEST;
    }

    public static boolean isItemElytra(ItemStack itemStack) {
        return QuickElytraConfig.elytraSwapItems.contains(itemStack.getItem());
    }

    public static boolean isItemGroundedOffhand(ItemStack itemStack) {
        return QuickElytraConfig.groundedOffhandItems.contains(itemStack.getItem());
    }

    public static boolean isItemFlyingOffhand(ItemStack itemStack) {
        return QuickElytraConfig.flyingOffhandItems.contains(itemStack.getItem());
    }

    public static boolean isItemOffhandOption(ItemStack itemStack) {
        return QuickElytraConfig.offhandSwapItems.contains(itemStack.getItem());
    }
}
