package eextr0.mobsoccer.Managers;

import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Tasks.CreateScoreboardDisplayTask;
import org.bukkit.Location;

public class TeamScoreManager {

    private final MobSoccer plugin;
    private final CreateScoreboardDisplayTask createScoreboardDisplayTask;

    public TeamScoreManager(MobSoccer plugin, CreateScoreboardDisplayTask createScoreboardDisplayTask) {
        this.plugin = plugin;
        this.createScoreboardDisplayTask = createScoreboardDisplayTask;
    }


    public void setTeamScore(String teamName, int score) {
        plugin.teamScores.put(teamName, score);
    }
    public void incrementTeamScore(String teamName) {
        int currentScore = plugin.teamScores.getOrDefault(teamName, 0);
        plugin.teamScores.put(teamName, currentScore + 1);
        Location scoreboardLocation = plugin.getScoreboardLocation();
        createScoreboardDisplayTask.createDisplay(plugin.teamScores, scoreboardLocation);
    }

    public int getTeamScores(String teamName) {
        return plugin.teamScores.getOrDefault(teamName, 0);
    }
}