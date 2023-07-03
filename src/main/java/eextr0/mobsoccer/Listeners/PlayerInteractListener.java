package eextr0.mobsoccer.Listeners;

import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Tasks.CreateScoreboardDisplayTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


public class PlayerInteractListener implements Listener {

    private final MobSoccer plugin;
    private final CreateScoreboardDisplayTask createScoreboardDisplayTask;
    public PlayerInteractListener(MobSoccer plugin, CreateScoreboardDisplayTask createScoreboardDisplayTask) {
        this.plugin = plugin;
        this.createScoreboardDisplayTask = createScoreboardDisplayTask;
    }



        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            Player player = event.getPlayer();
            if (plugin.scoreboardCommandExecuted && player.hasPermission("mobsoccer.setscoreboardlocation")) {
                if (plugin.playerSelections.containsKey(player.getUniqueId())) {
                    Location[] locations = plugin.playerSelections.get(player.getUniqueId());
                    if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR) {
                        if (locations[0] == null) {
                            locations[0] = event.getClickedBlock().getLocation();
                            player.sendMessage("First position set to " + locations[0] + ".");
                        } else if (locations[1] == null) {
                            locations[1] = event.getClickedBlock().getLocation();
                            player.sendMessage("Second position set to " + locations[1] + ".");
                            Location center = getCenterBlock(locations[0], locations[1]);

                            plugin.setScoreboardLocation(center);
                            createScoreboardDisplayTask.createDisplay(plugin.teamScores, center);
                            plugin.playerSelections.remove(player.getUniqueId());
                            plugin.scoreboardCommandExecuted = false;
                        }
                    }
                }
            }
        }

        private Location getCenterBlock(Location loc1, Location loc2) {
            int centerX = loc1.getBlockX() + loc2.getBlockX() /2;
            int centerY = (loc1.getBlockY() + loc2.getBlockY()) / 2;
            int centerZ = (loc1.getBlockZ() + loc2.getBlockZ()) / 2;
            return new Location(loc1.getWorld(), centerX, centerY, centerZ);
        }
}
