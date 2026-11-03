package com.elytraswing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ElytraSwingConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("elytraswing.json");

    /** Ticks to wait after pressing the key before the rocket is used (20 ticks = 1 second). */
    public int delayTicks = 0;
    /** Minimum ticks between two rockets. */
    public int cooldownTicks = 10;
    /** If true, the key only works while gliding with elytra. */
    public boolean onlyWhileGliding = true;

    public static ElytraSwingConfig INSTANCE = new ElytraSwingConfig();

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                ElytraSwingConfig loaded = GSON.fromJson(Files.readString(PATH), ElytraSwingConfig.class);
                if (loaded != null) INSTANCE = loaded;
            }
            INSTANCE.delayTicks = Math.max(0, INSTANCE.delayTicks);
            INSTANCE.cooldownTicks = Math.max(0, INSTANCE.cooldownTicks);
            save();
        } catch (Exception e) {
            ElytraSwingClient.LOGGER.warn("Could not read config, using defaults", e);
        }
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            ElytraSwingClient.LOGGER.warn("Could not save config", e);
        }
    }
}
