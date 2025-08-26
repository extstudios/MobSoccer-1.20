package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.MobSoccer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.checkerframework.checker.units.qual.A;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateScoreboardDisplayTask {

    private final MobSoccer plugin;
    private final TeamManager teamManager;
    private final List<TextDisplay> textDisplays;
    private TextDisplay textDisplay;

    private double scoreboardRotation;

    public CreateScoreboardDisplayTask(MobSoccer plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.textDisplays = new ArrayList<>();
    }

    public void createDisplay(Map<String, Integer> scores, Location location) {
        removeTextDisplay();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String teamName = entry.getKey();
            Team team = teamManager.getTeamByName(teamName);
            ChatColor teamColor = teamManager.getTeamColor(team);
            Integer score = entry.getValue();
            sb.append(teamColor).append(teamName).append(": ").append(ChatColor.WHITE).append(score).append("\n");
        }
        String teamScores = sb.toString().trim();
        spawnTextDisplay(location, teamScores);
    }

    public void spawnTextDisplay(Location location, String text) {
        textDisplay = (TextDisplay) plugin.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        scoreboardRotation = 0;
        Transformation transformation = textDisplay.getTransformation();
        Transformation newScale = new Transformation(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale().mul(plugin.getScoreboardSize()), transformation.getRightRotation());
        textDisplay.setText(text);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setVisibleByDefault(true);
        textDisplay.setGravity(false);
        textDisplay.setAlignment(TextDisplay.TextAlignment.LEFT);
        textDisplay.setTransformation(newScale);
        textDisplay.setDisplayHeight(plugin.getConfig().getInt("scoreboardHeight"));
        textDisplay.setDisplayWidth(plugin.getConfig().getInt("scoreboardWidth"));
        textDisplay.setCustomName("scoreboard");
        textDisplays.add(textDisplay);

        plugin.setScoreboardLocation(location);
        plugin.setScoreboardText(text);
    }

    public void removeTextDisplay() {
        for (TextDisplay textDisplay : textDisplays) {
            textDisplay.remove();
        }
        textDisplays.clear();
    }

    public void rotateScoreboard(Location location, Map<String, Integer> scores) {
        removeTextDisplay();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String teamName = entry.getKey();
            Team team = teamManager.getTeamByName(teamName);
            ChatColor teamColor = teamManager.getTeamColor(team);
            Integer score = entry.getValue();
            sb.append(teamColor).append(teamName).append(": ").append(ChatColor.WHITE).append(score).append("\n");
        }
        String teamScores = sb.toString().trim();
        rotateText(location, teamScores);
    }

    public void rotateText(Location location, String text) {
        textDisplay = (TextDisplay) plugin.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        if(scoreboardRotation == 0) {
            scoreboardRotation = 1;
        }else if (scoreboardRotation == 1) {
            scoreboardRotation = 2;
        }else if (scoreboardRotation == 2) {
            scoreboardRotation = 3;
        }else if (scoreboardRotation == 3) {
            scoreboardRotation = 4;
        }else {
            scoreboardRotation = 0;
        }
        Transformation transformation = textDisplay.getTransformation();

        transformation.getLeftRotation().y = (float) scoreboardRotation;
        Transformation newScale = new Transformation(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale().mul(plugin.getScoreboardSize()), transformation.getRightRotation());
        textDisplay.setText(text);
        textDisplay.setBillboard(Display.Billboard.VERTICAL);
        textDisplay.setVisibleByDefault(true);
        textDisplay.setGravity(false);
        textDisplay.setAlignment(TextDisplay.TextAlignment.LEFT);
        textDisplay.setTransformation(newScale);
        textDisplay.setDisplayHeight(plugin.getConfig().getInt("scoreboardHeight"));
        textDisplay.setDisplayWidth(plugin.getConfig().getInt("scoreboardWidth"));
        textDisplay.setCustomName("scoreboard");
        textDisplays.add(textDisplay);
    }
}
