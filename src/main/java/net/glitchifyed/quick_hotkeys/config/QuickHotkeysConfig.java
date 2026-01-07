package net.glitchifyed.quick_hotkeys.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient.LOGGER;

public class QuickHotkeysConfig {
    public static final QuickHotkeysConfigEntries DEFAULT_VALUES = new QuickHotkeysConfigEntries();

    @SerialEntry public static boolean autoSwapEnabled = DEFAULT_VALUES.autoSwapEnabled;
    @SerialEntry public static boolean fireworkSwapEnabled = DEFAULT_VALUES.fireworkSwapEnabled;
    @SerialEntry public static boolean fireworkRestockEnabled = DEFAULT_VALUES.fireworkRestockEnabled;

    @SerialEntry public static List<Item> elytraSwapItems = DEFAULT_VALUES.elytraSwapItems;
    @SerialEntry public static List<Item> totemSwapItems = DEFAULT_VALUES.totemSwapItems;
    @SerialEntry public static List<Item> offhandSwapItems = DEFAULT_VALUES.offhandSwapItems;
    @SerialEntry public static List<Item> fireworkSwapItems = DEFAULT_VALUES.fireworkSwapItems;


    private static final ConfigClassHandler<QuickHotkeysConfig> HANDLER = ConfigClassHandler.createBuilder(QuickHotkeysConfig.class)
            .id(Identifier.fromNamespaceAndPath("quick_hotkeys", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("quick_hotkeys.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    public static QuickHotkeysConfig instance() {
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
                .title(Component.literal("Quick Elytra Reborn Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Elytra swapping"))
                        .tooltip(Component.literal("Configure how the elytra swapping works."))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Toggles"))
                                .description(OptionDescription.of(Component.literal("Controls the functionality of automated elytra")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Automated Elytra"))
                                        .description(OptionDescription.of(Component.literal("When enabled, pressing space while in mid-air will automatically swap your currently worn chestplate with your best pair of elytra")))
                                        .binding(DEFAULT_VALUES.autoSwapEnabled, () -> autoSwapEnabled, newVal -> autoSwapEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Swap fireworks"))
                                        .description(OptionDescription.of(Component.literal("When enabled, fireworks will also be swapped into your offhand when you start gliding, assuming you have any. Once you land, the fireworks will be switched away.")))
                                        .binding(DEFAULT_VALUES.fireworkSwapEnabled, () -> fireworkSwapEnabled, newVal -> fireworkSwapEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Auto restock fireworks"))
                                        .description(OptionDescription.of(Component.literal("When enabled, fireworks that have been auto-swapped on gliding will be restocked from your inventory when they run out.")))
                                        .binding(DEFAULT_VALUES.fireworkRestockEnabled, () -> fireworkRestockEnabled, newVal -> fireworkRestockEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Swap item lists"))
                        .tooltip(Component.literal("Configure which items are used during swaps"))
                        .group(ListOption.<Item>createBuilder()
                                .name(Component.literal("Elytra swap items"))
                                .description(OptionDescription.of(Component.literal("Uses this list to determine the swapped item during an elytra swap.")))
                                .binding(DEFAULT_VALUES.elytraSwapItems, () -> elytraSwapItems, newVal -> elytraSwapItems = newVal)
                                .initial(DEFAULT_VALUES.elytraSwapItems.get(0))
                                .controller(ItemControllerBuilder::create)
                                .build())
                        .group(ListOption.<Item>createBuilder()
                                .name(Component.literal("Offhand swap list #1"))
                                .description(OptionDescription.of(Component.literal("When the offhand swap key is pressed, your offhand item is switched between this list and the list below.")))
                                .binding(DEFAULT_VALUES.totemSwapItems, () -> totemSwapItems, newVal -> totemSwapItems = newVal)
                                .initial(DEFAULT_VALUES.totemSwapItems.get(0))
                                .controller(ItemControllerBuilder::create)
                                .build())
                        .group(ListOption.<Item>createBuilder()
                                .name(Component.literal("Offhand swap list #2"))
                                .description(OptionDescription.of(Component.literal("When the offhand swap key is pressed, your offhand item is switched between the list above and this.")))
                                .binding(DEFAULT_VALUES.offhandSwapItems, () -> offhandSwapItems, newVal -> offhandSwapItems = newVal)
                                .initial(DEFAULT_VALUES.offhandSwapItems.get(0))
                                .controller(ItemControllerBuilder::create)
                                .build())
                        .group(ListOption.<Item>createBuilder()
                                .name(Component.literal("Automatic firework swap items"))
                                .description(OptionDescription.of(Component.literal("Uses this list to determine the swapped item during the automatic firework swap.")))
                                .binding(DEFAULT_VALUES.fireworkSwapItems, () -> fireworkSwapItems, newVal -> fireworkSwapItems = newVal)
                                .initial(DEFAULT_VALUES.fireworkSwapItems.get(0))
                                .controller(ItemControllerBuilder::create)
                                .build())
                        .build())
                .save(QuickHotkeysConfig::saveConfig)
                .build()
                .generateScreen(parentScreen);
    }
}