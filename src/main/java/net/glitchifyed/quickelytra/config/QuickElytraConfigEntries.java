package net.glitchifyed.quickelytra.config;

import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class QuickElytraConfigEntries {
    public boolean autoSwapEnabled = false;
    public boolean fireworkSwapEnabled = false;
    public boolean fireworkRestockEnabled = true;

    public boolean chatAlertsEnabled = true;
    public boolean soundAlertsEnabled = true;

    public List<Item> elytraSwapItems = Collections.singletonList(Items.ELYTRA);
    public List<Item> totemSwapItems = Collections.singletonList(Items.TOTEM_OF_UNDYING);
    public List<Item> offhandSwapItems = Collections.singletonList(Items.SHIELD);
    public List<Item> fireworkSwapItems = Collections.singletonList(Items.FIREWORK_ROCKET);
}