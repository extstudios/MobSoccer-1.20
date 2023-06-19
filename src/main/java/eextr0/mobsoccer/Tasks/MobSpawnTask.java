package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobSpawnTask extends BukkitRunnable {
    private final MobSoccer plugin;
    private final Location mobSpawnLocation;
    private final Random random;
    private final List<EntityType> entityTypes;

    public MobSpawnTask(MobSoccer plugin) {
        this.plugin = plugin;
        this.mobSpawnLocation = plugin.getMobSpawnLocation();
        this.random = new Random();
        this.entityTypes = new ArrayList<>();
    }

    @Override
    public void run() {
        FileConfiguration config = plugin.getConfig();
        List<String> entityTypeKeys = config.getStringList("entityTypes");
        if (entityTypeKeys != null) {
            for (String entityKey : entityTypeKeys) {
                EntityType entityType = EntityType.valueOf(entityKey);

                if (entityType != null) {
                    entityTypes.add(entityType);
                } else {
                    plugin.getLogger().warning("Invalid entity type: " + entityKey);
                }
            }
        } else {
            plugin.getLogger().warning("entityTypeKeys is null");
        }
        if (!entityTypes.isEmpty()) {

            World world = mobSpawnLocation.getWorld();
            for (int i = 0; i < 3; i++) {
                EntityType randomEntityType = getRandomEntityType();
                if (randomEntityType != null) {
                    Entity entity = world.spawnEntity(mobSpawnLocation, randomEntityType);
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.setCustomName("ball");
                        livingEntity.setCustomNameVisible(false);
                    }
                } else {
                    plugin.getLogger().warning("randomEntityType is null");
                }
            }
        } else {
            plugin.getLogger().warning("EntityType list is empty");
        }
    }

    private EntityType getRandomEntityType() {
        if (!entityTypes.isEmpty()) {
            int randomIndex = random.nextInt(entityTypes.size());
            return entityTypes.get(randomIndex);
        }
        return null;
    }
}