package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.MobSoccer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateScoreboardDisplayTask {

    private final MobSoccer plugin;
    private final TeamManager teamManager;
    private final List<TextDisplay> textdisplays;
    private TextDisplay textDisplay;

    public CreateScoreboardDisplayTask(MobSoccer plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this. textdisplays = new ArrayList<>();
    }

    public void createScoreboardTitle(Location location) {

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
        spawnTextDisplay(location, sb.toString().trim());
    }

    public void spawnTextDisplay(Location location, String text) {
        textDisplay = (TextDisplay) plugin.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY);
        Location titleLocation = location.add(0,3,0);

        Transformation transformation = textDisplay.getTransformation();
        Transformation newScale = new Transformation(transformation.getTranslation(), transformation.getLeftRotation(), transformation.getScale().mul(plugin.getScoreboardSize()), transformation.getRightRotation());
        textDisplay.setText(text);
        textDisplay.setBillboard(Display.Billboard.FIXED);
        textDisplay.setVisibleByDefault(true);
        textDisplay.setGravity(false);
        textDisplay.setAlignment(TextDisplay.TextAlignment.LEFT);
        textDisplay.setTransformation(newScale);
        textdisplays.add(textDisplay);
        textDisplay.setDisplayWidth(plugin.getConfig().getInt("scoreboardWidth"));
        textDisplay.setDisplayWidth(plugin.getConfig().getInt("scoreboardHeight"));
    }

    public void removeTextDisplay() {
        for (TextDisplay textDisplay : textdisplays) {
            textDisplay.remove();
        }
        textdisplays.clear();
    }
}
