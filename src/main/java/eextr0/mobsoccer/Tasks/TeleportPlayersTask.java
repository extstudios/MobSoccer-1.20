package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.Map;

public class TeleportPlayersTask extends BukkitRunnable {

    private final MobSoccer plugin;

    public TeleportPlayersTask(MobSoccer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : plugin.getWorld().getPlayers()) {
            Team playerTeam = plugin.playerTeams.get(p);
            if (playerTeam != null) {
                String teamName = playerTeam.getName();
                if(plugin.teamStartLocation.containsKey(teamName)) {
                    Location location = plugin.teamStartLocation.get(teamName);
                    p.teleport(location);
                }
            }
        }
    }
}