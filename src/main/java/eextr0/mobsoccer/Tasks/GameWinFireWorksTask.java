package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.MobSoccer;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class GameWinFireWorksTask extends BukkitRunnable {

    private final MobSoccer plugin;
    private final TeamManager teamManager;

    public GameWinFireWorksTask (MobSoccer plugin, TeamManager teamManager) {

        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    int countdown = 0;

    @Override
    public void run() {
        if(countdown >= 10) {
            this.cancel();
            return;
        }
        String teamName = plugin.winningTeam;
        Team team = teamManager.getTeamByName(teamName);
        ChatColor teamColor = team.getColor();
        for (int i = 0; i < 10; i++) {
            Location fireworksLocation = getRandomLocationWithinSquare();
            Firework firework = (Firework) fireworksLocation.getWorld().spawnEntity(fireworksLocation, EntityType.FIREWORK);

            FireworkMeta fireworkMeta = firework.getFireworkMeta();
            Color fireworkColor = plugin.ChatColorToColor(teamColor);
            FireworkEffect.Type fireworkTypes = getRandomFireworkType();
            if (fireworkTypes != null) {

                FireworkEffect fireworkEffect = FireworkEffect.builder()
                        .withColor(fireworkColor)
                        .with(fireworkTypes)
                        .build();

                fireworkMeta.addEffect(fireworkEffect);
                fireworkMeta.setPower(1);
                firework.setFireworkMeta(fireworkMeta);
            } else {
                plugin.getLogger().warning("fireworkTypes is null");
            }
        }
        countdown++;
    }

    private Location getRandomLocationWithinSquare() {
        Location centerLocation = plugin.getMobSpawnLocation();
        int squareSize = 50;
        int halfSize = squareSize /2;

        double centerX = centerLocation.getX();
        double centerY = centerLocation.getY();
        double centerZ = centerLocation.getZ();

        double offsetX = (Math.random() * squareSize) - halfSize;
        double offsetY = centerY - 15;
        double offsetZ = (Math.random() * squareSize) - halfSize;

        double randomX = centerX + offsetX;
        double randomZ = centerZ + offsetZ;
        World world = centerLocation.getWorld();
        return new Location(centerLocation.getWorld(), randomX, offsetY, randomZ);
    }

    private int getRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private FireworkEffect.Type getRandomFireworkType() {
        FireworkEffect.Type[] types = FireworkEffect.Type.values();
        int randomIndex = getRandomNumber(0, types.length -1);

        return types[randomIndex];
    }
}