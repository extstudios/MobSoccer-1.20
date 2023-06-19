package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.Managers.TeamScoreManager;
import eextr0.mobsoccer.MobSoccer;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Monster;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class ScoreGoalTask extends BukkitRunnable {

    private final MobSoccer plugin;
    private final TeamScoreManager teamScoreManager;
    private final TeamManager teamManager;
    private final World world;

    public ScoreGoalTask(MobSoccer plugin, TeamScoreManager teamScoreManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.world = plugin.getWorld();
        this.teamScoreManager = teamScoreManager;
        this.teamManager = teamManager;
    }

    @Override
    public void run() {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Monster && !entity.isDead() && entity.getCustomName() != null && entity.getCustomName().equals("ball")) {
                for (String teamName : plugin.goalLocation.keySet()) {
                    Location goalLocation = plugin.goalLocation.get(teamName);
                    if (isEntityInGoal(entity, goalLocation)) {
                        Team team = teamManager.getTeamByName(teamName);
                        if (team != null) {
                            teamScoreManager.incrementTeamScore(teamName);
                            ChatColor teamColor = team.getColor();
                            world.getPlayers().forEach(player -> player.sendMessage(plugin.getMessagesConfigManager().getBroadcastMessages().get("teamScored").replace("%team%", teamName)));
                            world.playSound(goalLocation, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
                            Color fireWorkColor = plugin.ChatColorToColor(teamColor);
                            Firework firework = world.spawn(goalLocation, Firework.class);
                            FireworkMeta fireworkMeta = firework.getFireworkMeta();
                            FireworkEffect effect = FireworkEffect.builder()
                                    .withColor(fireWorkColor)
                                    .with(FireworkEffect.Type.BALL_LARGE)
                                    .build();
                            fireworkMeta.addEffect(effect);
                            fireworkMeta.setPower(2);
                            firework.setFireworkMeta(fireworkMeta);

                            Bukkit.getScheduler().runTaskLater(plugin, firework::remove, 60L);
                        } else {
                            plugin.getLogger().warning(teamName + " is null");
                        }
                        entity.remove();
                        break;
                    }
                }
            }
        }
    }

    private boolean isEntityInGoal(Entity entity, Location goalLocation) {
        Location entityLocation = entity.getLocation();
        return entityLocation.getBlockX() >= goalLocation.getBlockX() - 1 &&
                entityLocation.getBlockX() <= goalLocation.getBlockX() + 1 &&
                entityLocation.getBlockZ() >= goalLocation.getBlockZ() - 1 &&
                entityLocation.getBlockZ() <= goalLocation.getBlockZ() + 1 &&
                entityLocation.getBlockY() < goalLocation.getBlockY();
    }
}