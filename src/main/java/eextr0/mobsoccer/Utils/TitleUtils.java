package eextr0.mobsoccer.Utils;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.MobSoccer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TitleUtils {
    private final MobSoccer plugin;
    private final TeamManager teamManager;

    public TitleUtils(MobSoccer plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    public void displayTitle(Team team, String title, String subtitle, int fadeInTicks, int StayTicks, int fadeOutTicks) {

        ChatColor teamColor = teamManager.getTeamColor(team);
        String coloredTitle = teamColor + title;

        for (Player player : plugin.getWorld().getPlayers()) {
            player.sendTitle(coloredTitle, subtitle, fadeInTicks, StayTicks, fadeOutTicks);
        }
    }
}
