package io.github.seriousguy888.chatmessageconfirmation.config;

import io.github.seriousguy888.chatmessageconfirmation.ChatMessageConfirmationMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    private final Path CONFIG_FILE_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(ChatMessageConfirmationMod.MOD_ID + ".properties");

    public boolean ALLOW_BYPASS = true;
    public String BYPASS_PREFIX = "#";


    public Config() {
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE_PATH)) {
            Properties properties = new Properties();
            properties.load(reader);

            ALLOW_BYPASS = Boolean.parseBoolean(properties.getProperty("allow_bypass", "true"));
            BYPASS_PREFIX = properties.getProperty("bypass_prefix", "#");
        } catch (Exception _e) {
            save();
        }
    }

    public void save() {
        Properties properties = new Properties();
        properties.put("allow_bypass", String.valueOf(ALLOW_BYPASS));
        properties.put("bypass_prefix", BYPASS_PREFIX);

        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE_PATH)) {
            properties.store(writer, "Configuration for Chat Message Confirmation");
        } catch (IOException exception) {
            ChatMessageConfirmationMod.LOGGER.error(exception.getMessage(), exception);
        }
    }
}