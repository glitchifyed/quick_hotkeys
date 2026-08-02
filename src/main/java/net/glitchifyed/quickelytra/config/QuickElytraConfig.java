package net.glitchifyed.quickelytra.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.glitchifyed.quickelytra.lang.QuickElytraLang;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.*;

import static net.glitchifyed.quickelytra.client.QuickElytraClient.LOGGER;

public class QuickElytraConfig {
    public static final QuickElytraConfigEntries DEFAULT_VALUES = new QuickElytraConfigEntries();

    @SerialEntry public static boolean autoSwapEnabled = DEFAULT_VALUES.autoSwapEnabled;
    @SerialEntry public static boolean fireworkSwapEnabled = DEFAULT_VALUES.fireworkSwapEnabled;
    @SerialEntry public static boolean fireworkRestockEnabled = DEFAULT_VALUES.fireworkRestockEnabled;

    @SerialEntry public static boolean chatAlertsEnabled = DEFAULT_VALUES.chatAlertsEnabled;
    @SerialEntry public static boolean soundAlertsEnabled = DEFAULT_VALUES.soundAlertsEnabled;

    @SerialEntry public static List<Item> elytraSwapItems = DEFAULT_VALUES.elytraSwapItems;
    @SerialEntry public static List<Item> groundedOffhandItems = DEFAULT_VALUES.totemSwapItems;
    @SerialEntry public static List<Item> offhandSwapItems = DEFAULT_VALUES.offhandSwapItems;
    @SerialEntry public static List<Item> flyingOffhandItems = DEFAULT_VALUES.fireworkSwapItems;


    private static final ConfigClassHandler<QuickElytraConfig> HANDLER = ConfigClassHandler.createBuilder(QuickElytraConfig.class)
            .id(Identifier.fromNamespaceAndPath("quick_hotkeys", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("quick_hotkeys.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    public static QuickElytraConfig instance() {
        return HANDLER.instance();
    }

    private static boolean loadedConfig = false;

    public static void saveConfig() {
        HANDLER.save();
    }

    public static void loadConfig() {
        if (loadedConfig) {
            return;
        }

        LOGGER.info("Loading config");

        loadedConfig = HANDLER.load();

        if (!loadedConfig) {
            LOGGER.info("Config load was unsuccessful.");
        }
        else {
            LOGGER.info("Successfully loaded config!");
        }
    }

    public Screen GenerateScreen(Screen parentScreen) {
        return YetAnotherConfigLib.createBuilder()
                // Quick Elytra Reborn Config
                .title(Component.translatable(QuickElytraLang.CONFIG_GENERIC))

                // Page one
                .category(ConfigCategory.createBuilder()
                        // Elytra swapping
                        .name(Component.translatable(QuickElytraLang.CONFIG_ELYTRA_SWAP + "title"))
                        .tooltip(Component.translatable(QuickElytraLang.CONFIG_ELYTRA_SWAP + "desc"))

                        .option(Option.<Boolean>createBuilder()
                                // Automated Elytra
                                .name(Component.translatable(QuickElytraLang.CONFIG_AUTO_SWAP + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_AUTO_SWAP + "desc")))

                                .binding(DEFAULT_VALUES.autoSwapEnabled, () -> autoSwapEnabled, newVal -> autoSwapEnabled = newVal)

                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                // Swap fireworks
                                .name(Component.translatable(QuickElytraLang.CONFIG_FIREWORK_SWAP + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_FIREWORK_SWAP + "desc")))

                                .binding(DEFAULT_VALUES.fireworkSwapEnabled, () -> fireworkSwapEnabled, newVal -> fireworkSwapEnabled = newVal)

                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                // Auto-restock fireworks
                                .name(Component.translatable(QuickElytraLang.CONFIG_RESTOCK_FIREWORKS + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_RESTOCK_FIREWORKS + "desc")))

                                .binding(DEFAULT_VALUES.fireworkRestockEnabled, () -> fireworkRestockEnabled, newVal -> fireworkRestockEnabled = newVal)

                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .build())

                // Page two
                .category(ConfigCategory.createBuilder()
                        // Swap item lists
                        .name(Component.translatable(QuickElytraLang.CONFIG_ITEM_LISTS + "title"))
                        .tooltip(Component.translatable(QuickElytraLang.CONFIG_ITEM_LISTS + "desc"))

                        .group(ListOption.<Item>createBuilder()
                                // Elytra swap items
                                .name(Component.translatable(QuickElytraLang.CONFIG_ELYTRA_LIST + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_ELYTRA_LIST + "desc")))

                                .binding(DEFAULT_VALUES.elytraSwapItems, () -> elytraSwapItems, newVal -> elytraSwapItems = newVal)
                                .initial(DEFAULT_VALUES.elytraSwapItems.get(0))

                                .controller(ItemControllerBuilder::create)
                                .build())

                        .group(ListOption.<Item>createBuilder()
                                .name(Component.translatable(QuickElytraLang.CONFIG_FIREWORK_LIST + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_FIREWORK_LIST + "desc")))

                                .binding(DEFAULT_VALUES.fireworkSwapItems, () -> flyingOffhandItems, newVal -> flyingOffhandItems = newVal)
                                .initial(DEFAULT_VALUES.fireworkSwapItems.get(0))

                                .controller(ItemControllerBuilder::create)
                                .build())

                        .group(ListOption.<Item>createBuilder()
                                // Offhand swap list #1
                                .name(Component.translatable(QuickElytraLang.CONFIG_OFFHAND1_LIST + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_OFFHAND2_LIST + "desc")))

                                .binding(DEFAULT_VALUES.totemSwapItems, () -> groundedOffhandItems, newVal -> groundedOffhandItems = newVal)
                                .initial(DEFAULT_VALUES.totemSwapItems.get(0))

                                .controller(ItemControllerBuilder::create)
                                .build())

                        .group(ListOption.<Item>createBuilder()
                                // Offhand swap list #2
                                .name(Component.translatable(QuickElytraLang.CONFIG_OFFHAND2_LIST + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_OFFHAND2_LIST + "desc")))

                                .binding(DEFAULT_VALUES.offhandSwapItems, () -> offhandSwapItems, newVal -> offhandSwapItems = newVal)
                                .initial(DEFAULT_VALUES.offhandSwapItems.get(0))

                                .controller(ItemControllerBuilder::create)
                                .build())

                        .build())

                // Page three
                .category(ConfigCategory.createBuilder()
                        // Alerts
                        .name(Component.translatable(QuickElytraLang.CONFIG_ALERTS + "title"))
                        .tooltip(Component.translatable(QuickElytraLang.CONFIG_ALERTS + "desc"))

                        .option(Option.<Boolean>createBuilder()
                                // Chat alerts
                                .name(Component.translatable(QuickElytraLang.CONFIG_ALERTS_CHAT + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_ALERTS_CHAT + "desc")))

                                .binding(DEFAULT_VALUES.chatAlertsEnabled, () -> chatAlertsEnabled, newVal -> chatAlertsEnabled = newVal)

                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                // Chat alerts
                                .name(Component.translatable(QuickElytraLang.CONFIG_ALERTS_SOUNDS + "title"))
                                .description(OptionDescription.of(Component.translatable(QuickElytraLang.CONFIG_ALERTS_SOUNDS + "desc")))

                                .binding(DEFAULT_VALUES.soundAlertsEnabled, () -> soundAlertsEnabled, newVal -> soundAlertsEnabled = newVal)

                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .build())

                .save(QuickElytraConfig::saveConfig)
                .build()
                .generateScreen(parentScreen);
    }
}