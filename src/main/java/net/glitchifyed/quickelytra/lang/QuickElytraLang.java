package net.glitchifyed.quickelytra.lang;

import net.glitchifyed.quickelytra.client.QuickElytraClient;

public class QuickElytraLang {
    public static final String GENERIC_TOGGLES = "generic.toggles";

    public static final String KEY_GENERIC = String.format("key.glitchifyed.%s.", QuickElytraClient.MODID);
    public static String CONFIG_GENERIC = String.format("config.glitchifyed.%s.", QuickElytraClient.MODID);

    public static final String KEY_ELYTRA = KEY_GENERIC + "equip_elytra";
    public static final String KEY_TOTEM = KEY_GENERIC + "equip_totem";

    public static final String KEY_AUTO = KEY_GENERIC + "automatic_elytra";


    public static final String STATUS_KEY_AUTO = "status.glitchifyed.quickelytra.automatic_elytra";


    public static final String CONFIG_ELYTRA_SWAP = CONFIG_GENERIC + "elytra_swapping.";

    public static final String CONFIG_AUTO_SWAP = CONFIG_ELYTRA_SWAP + "automated.";
    public static final String CONFIG_FIREWORK_SWAP = CONFIG_ELYTRA_SWAP + "fireworks.";
    public static final String CONFIG_RESTOCK_FIREWORKS = CONFIG_ELYTRA_SWAP + "restock_fireworks.";

    public static final String CONFIG_ITEM_LISTS = CONFIG_GENERIC + "item_lists.";

    public static final String CONFIG_ELYTRA_LIST = CONFIG_ITEM_LISTS + "elytras.";
    public static final String CONFIG_FIREWORK_LIST = CONFIG_ITEM_LISTS + "fireworks.";
    public static final String CONFIG_OFFHAND1_LIST = CONFIG_ITEM_LISTS + "offhand1.";
    public static final String CONFIG_OFFHAND2_LIST = CONFIG_ITEM_LISTS + "offhand2.";

    public static final String CONFIG_ALERTS = CONFIG_GENERIC + "alerts.";

    public static final String CONFIG_ALERTS_CHAT = CONFIG_ALERTS + "chat.";
    public static final String CONFIG_ALERTS_SOUNDS = CONFIG_ALERTS + "sounds.";
}
