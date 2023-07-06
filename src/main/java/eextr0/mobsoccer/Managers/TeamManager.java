package eextr0.mobsoccer.Managers;

import eextr0.mobsoccer.MobSoccer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {
    private final Scoreboard scoreboard;
    private final List<Team> teams;
    public TeamManager(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.teams = new ArrayList<>();
    }

    public Team createTeam(String teamName, String displayName, ChatColor color) {
        Team team = scoreboard.getTeam(teamName);
        if(team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setDisplayName(displayName);
            team.setColor(color);
            teams.add(team);

        } return team;
    }

    public void setTeamProperties(Team team, String prefix, String suffix, boolean allowFriendlyFire, boolean canSeeInvisible) {

        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.setAllowFriendlyFire(allowFriendlyFire);
        team.setCanSeeFriendlyInvisibles(canSeeInvisible);
    }

    public Team getTeamByName(String teamName) {
        return scoreboard.getTeam(teamName);
    }
    public List<String> getTeamNames() {
        List<String> teamNames = new ArrayList<>();
        for (Team team : teams) {
            teamNames.add(team.getName());
        }
        return teamNames;
    }
    public List<Team> getTeams() {
        return teams;
    }

    public void setTeamName(String teamName, String newName, ChatColor color) {
        Team getTeam = getTeamByName(teamName);
        getTeam.unregister();
        teams.remove(getTeam);
        Team newTeam = createTeam(newName, newName, color);
        teams.add(newTeam);
    }

    public Team getTeamByPlayer(Player player) {
        for (Team team: teams) {
            if (team.hasEntry(player.getName())) {
                return team;
            }
        }
        return null;
    }
    public ChatColor getTeamColor(Team team) {
        return team.getColor();
    }
}