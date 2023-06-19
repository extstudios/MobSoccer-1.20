package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.Managers.TeamScoreManager;
import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Utils.SpoonUtils;
import eextr0.mobsoccer.Watchers.TeamScoreWatcher;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class StartGameTask extends BukkitRunnable {
    private final MobSoccer plugin;
    private final TeamScoreManager teamScoreManager;
    private final TeamManager teamManager;
    private final TeamScoreWatcher teamScoreWatcher;

    public StartGameTask(MobSoccer plugin,TeamManager teamManager, TeamScoreManager teamScoreManager, TeamScoreWatcher teamScoreWatcher) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.teamScoreManager = teamScoreManager;
        this.teamScoreWatcher = teamScoreWatcher;
    }

    private int countdown = 5;
    @Override

    public void run() {
        TeleportPlayersTask teleportPlayersTask = new TeleportPlayersTask(plugin);
        teleportPlayersTask.run();
        World world = plugin.getWorld();
        if (countdown > 0) {
            world.getPlayers().forEach(player -> player.sendMessage(plugin.getMessagesConfigManager().getBroadcastMessages().get("gameStarting").replace("%seconds%", Integer.toString(countdown))));
            countdown--;
        } else {
            world.getPlayers().forEach(player -> player.sendMessage(plugin.getMessagesConfigManager().getBroadcastMessages().get("gameStart")));
            cancel();
            plugin.gameRunning = true;
            if (plugin.getPotionSpawnArea() != null) {
                PotionSpawnTask potionSpawnTask = new PotionSpawnTask(plugin);
                potionSpawnTask.runTaskTimer(plugin, 45 * 20, 45 * 20);
            }
            Location scoreboardLocation = plugin.getScoreboardLocation();
            scoreboardLocation.add(0,2,0);
            MobSpawnTask mobSpawnTask = new MobSpawnTask(plugin);
            mobSpawnTask.runTaskTimer(plugin, 0, 200);
            ScoreGoalTask scoreGoalTask = new ScoreGoalTask(plugin, teamScoreManager, teamManager);
            scoreGoalTask.runTaskTimer(plugin, 0, 20);
            new BukkitRunnable() {

                public void run() {
                    teamScoreWatcher.checkTeamScores();
                }
            }.runTaskTimer(plugin, 0, 20);
            TeamEquipChestplateTask teamEquipChestplateTask = new TeamEquipChestplateTask(plugin, teamManager);
            teamEquipChestplateTask.run();
            for (String teamName : teamManager.getTeamNames()) {
                Team team = teamManager.getTeamByName(teamName);
                if (team != null) {
                    for(OfflinePlayer offlinePlayer : team.getPlayers()) {
                        if(offlinePlayer.isOnline()) {
                            Player p = (Player) offlinePlayer;
                            PlayerInventory inventory = p.getInventory();
                            ItemStack spoon = SpoonUtils.createSpoon();
                            inventory.addItem(spoon);
                        }
                    }
                }
            }
        }
    }
}