package net.glitchifyed.quick_hotkeys.config;

import com.google.gson.GsonBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.glitchifyed.quick_hotkeys.client.QuickHotkeysClient.LOGGER;

public class QuickHotkeysConfig {
    public static ConfigClassHandler<QuickHotkeysConfig> HANDLER = ConfigClassHandler.createBuilder(QuickHotkeysConfig.class)
            .id(Identifier.of("quick_hotkeys", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("quick_hotkeys.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public boolean autoSwapEnabled = false;

    private static boolean loadedConfig = false;

    public static void saveConfig() {
        HANDLER.save();
    }

    public static void loadConfig() {
        if (loadedConfig) {
            return;
        }

        loadedConfig = true;

        HANDLER.load();
    }

    public ConfigScreenFactory<?> GenerateScreen() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Quick Elytra Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Quick Elytra"))
                        .tooltip(Text.literal("Quick Elytra functionality"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Automatic Elytra"))
                                .description(OptionDescription.of(Text.literal("Controls the functionality of automated elytra")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Automated Elytra"))
                                        .description(OptionDescription.of(Text.literal("When enabled, pressing space while in mid-air will automatically swap your currently worn chestplate with your best pair of elytra")))
                                        .binding(true, () -> this.autoSwapEnabled, newVal -> this.autoSwapEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .save(QuickHotkeysConfig::saveConfig)
                .build()
                .generateScreen(parentScreen);
    }
}