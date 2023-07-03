package eextr0.mobsoccer.Config;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LocationConfigManager extends ConfigManager{

    private HashMap<String, Location> goalLocations;
    private HashMap<String, Location> teamStartLocation;
    private Location mobspawnLocation;
    private Location teamExitLocation;
    private Location potionSpawnArea;
    public LocationConfigManager(MobSoccer plugin) {

        super(plugin);
        if (plugin.locationFile != null && plugin.locationConfigStream != null) {
            updateConfig(plugin.locationFile, plugin.locationConfigStream);
        }
        createConfig("locations.yml");

        load();
    }

    public void load() {
        goalLocations = getMap("locations.goallocations");
        plugin.loadGoalLocation(goalLocations);
        teamStartLocation = getMap("locations.teamstartlocations");
        plugin.loadTeamStartLocation(teamStartLocation);
        mobspawnLocation = config.getLocation("locations.mobspawnlocation.locations.mobspawnlocation");
        plugin.loadMobSpawnLocation(mobspawnLocation);
        potionSpawnArea = config.getLocation("locations.potionspawnarea");
        plugin.potionSpawnArea = potionSpawnArea;
        teamExitLocation = config.getLocation("locations.teamexitlocationlocations.locations.teamexitlocation");
        plugin.loadTeamExit(teamExitLocation);
    }


    public HashMap<String, Location> getMap(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        HashMap<String, Location> hashMap = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                hashMap.put(key, (Location) section.get(key));
            }
        }
        return hashMap;
    }

    public void setHashMap(String path, HashMap<String,Location> hashMap) {
        ConfigurationSection section = config.createSection(path);
        for(Map.Entry<String, Location> entry : hashMap.entrySet()) {
            section.set(entry.getKey(), entry.getValue());
        }
        saveConfig();
    }

    public void saveLocation(String path, Location location) {
        ConfigurationSection section = config.createSection(path);
        section.set(path, location);
        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
