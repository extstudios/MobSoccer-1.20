package eextr0.mobsoccer.Config;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MessagesConfigManager extends ConfigManager {

    private final Map<String, String> errorMessages = new HashMap<>();
    private final Map<String, String> commandMessages = new HashMap<>();
    private final Map<String,String> broadcastMessages = new HashMap<>();
    public MessagesConfigManager(MobSoccer plugin) {
        super(plugin);
        if(plugin.messagesFile != null && plugin.messagesConfigStream != null) {
            updateConfig(plugin.messagesFile, plugin.messagesConfigStream);
        }
        createConfig("messages.yml");

        load();
    }

    public void load() {
        ConfigurationSection errorSection = getConfig().getConfigurationSection("messages.error");
        for (String key : errorSection.getKeys(false)) {
            String message = translateText(errorSection, key);
            errorMessages.put(key, message);
        }

        ConfigurationSection commandSection = getConfig().getConfigurationSection("messages.commands");
        for (String key : commandSection.getKeys(false)) {
            String message = translateText(commandSection, key);
            commandMessages.put(key, message);
        }
        ConfigurationSection broadcastSection = getConfig().getConfigurationSection("messages.broadcast");
        for (String key: broadcastSection.getKeys(false)) {
            String message = translateText(broadcastSection, key);
            broadcastMessages.put(key, message);
        }



    }
    public String translateText(ConfigurationSection config, String key) {

        String text = config.getString(key);
        String translatedText = "";
        if (!text.isEmpty()) {
            translatedText = ChatColor.translateAlternateColorCodes('&', text);
        }
        return translatedText;
    }

    public Map<String, String> getErrorMessages() {return errorMessages;}
    public Map<String, String> getCommandMessages() {return commandMessages;}
    public Map<String, String> getBroadcastMessages() {return broadcastMessages;}

}
