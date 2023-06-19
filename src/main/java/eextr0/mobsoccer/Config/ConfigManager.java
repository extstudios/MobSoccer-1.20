package eextr0.mobsoccer.Config;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class ConfigManager {

    public final MobSoccer plugin;

    public ConfigManager(MobSoccer plugin) {
        this.plugin = plugin;
    }

    protected File configFile;
    protected FileConfiguration config;

    public void createConfig (String name) {
        configFile = new File(plugin.getDataFolder(), name);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void updateConfig(File configFile, InputStream defaultConfigStream) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        File tempFile;
        try {
            tempFile = File.createTempFile("temp", ".yml");
            Files.copy(defaultConfigStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(tempFile);

        boolean modified = false;
        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
                modified = true;
            }
        }

        if(modified) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tempFile.delete();
    }

    public abstract void load();

    public FileConfiguration getConfig() {
        return this.config;
    }


}
