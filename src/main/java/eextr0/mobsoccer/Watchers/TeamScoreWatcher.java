package eextr0.mobsoccer.Watchers;

import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Tasks.EndGameTask;
import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.Managers.TeamScoreManager;
import org.bukkit.scoreboard.Team;

public class TeamScoreWatcher {

    private final MobSoccer plugin;
    private final TeamManager teamManager;
    private final TeamScoreManager teamScoreManager;
    private final EndGameTask endGameTask;

    public TeamScoreWatcher(MobSoccer plugin, TeamManager teamManager, TeamScoreManager teamScoreManager, EndGameTask endGameTask) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.teamScoreManager = teamScoreManager;
        this.endGameTask = endGameTask;
    }

    public void checkTeamScores() {
        for (Team team : teamManager.getTeams()) {
            String teamName = team.getName();
            int teamScore = teamScoreManager.getTeamScores(teamName);

            if(teamScore >= 10) {
                String winningTeam = teamName;
                plugin.setWinningTeam(winningTeam);
                endGameTask.endGame(teamName);
            }
        }
    }
}