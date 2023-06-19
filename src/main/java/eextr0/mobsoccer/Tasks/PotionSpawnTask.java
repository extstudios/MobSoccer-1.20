package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PotionSpawnTask extends BukkitRunnable {

    private final MobSoccer plugin;
    private final Location spawnLocation;
    private final Random random;

    public PotionSpawnTask(MobSoccer plugin) {
        this.plugin = plugin;
        this.spawnLocation = plugin.getPotionSpawnArea();
        this.random = new Random();
    }

    @Override
    public void run() {
        //Randomly select potion number
        int maxPotions = getRandomNumber(1,4);

        World world = spawnLocation.getWorld();
        for (int i = 0; i < maxPotions; i++) {
            Location randomLocation = getRandomLocationWithinSpawnArea();
            if (randomLocation != null) {
                //Randomly selection Potion type
                PotionType potionType = getRandomPotionType();
                ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
                potionMeta.setBasePotionData(new PotionData(potionType));
                potion.setItemMeta(potionMeta);
                Item item = world.dropItem(randomLocation, potion);
                item.setPickupDelay(1);
            } else {
                plugin.getLogger().warning("randomLocation is null");
            }
        }
    }

    private PotionType getRandomPotionType() {
        //Generate a random number between 0 and 1
        double randomValue = random.nextDouble();

        //Randomly select a potion type based on the generated number
        if (randomValue < 0.5) {
            return PotionType.SPEED;
        } else {
            return PotionType.SLOWNESS;
        }
    }

    private Location getRandomLocationWithinSpawnArea() {
        double minX = spawnLocation.getX() -5;
        double minZ = spawnLocation.getZ() -5;
        double maxX = spawnLocation.getX() +5;
        double maxZ = spawnLocation.getZ() +5;

        World world = spawnLocation.getWorld();
        double randomX = minX + random.nextDouble() * (maxX - minX);
        double randomZ = minZ + random.nextDouble() * (maxZ - minZ);

        return new Location(world, randomX, spawnLocation.getY(), randomZ);
    }

    private int getRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}