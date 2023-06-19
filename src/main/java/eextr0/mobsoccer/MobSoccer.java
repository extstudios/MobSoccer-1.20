package eextr0.mobsoccer;

import eextr0.mobsoccer.Commands.*;
import eextr0.mobsoccer.Config.LocationConfigManager;
import eextr0.mobsoccer.Config.MessagesConfigManager;
import eextr0.mobsoccer.Listeners.PlayerLeaveListener;
import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.Managers.TeamScoreManager;
import eextr0.mobsoccer.Tasks.CreateScoreboardDisplayTask;
import eextr0.mobsoccer.Tasks.EndGameTask;
import eextr0.mobsoccer.Utils.*;
import eextr0.mobsoccer.Watchers.TeamScoreWatcher;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.InputStream;
import java.util.*;

public final class MobSoccer extends JavaPlugin implements Listener {
    public HashMap<String, Location> goalLocation;
    public HashMap<String, Location> teamStartLocation;
    public Map <Player, Team> playerTeams;
    public Map<String, Integer>teamScores;
    public Scoreboard scoreboard;
    String world = getConfig().getString("world");

    Integer scoreboardSize = getConfig().getInt("scoreboardSize");
    public boolean gameRunning;

    public List<Team> teams;

    private Location mobSpawnLocation;
    public Location potionSpawnArea;
    private Location teamExit;
    private Location scoreboardLocation;
    public String winningTeam;
    public InputStream messagesConfigStream;
    public File messagesFile;
    public InputStream locationConfigStream;
    public File locationFile;
    public boolean scoreboardCommandExecuted = false;
    public Map<UUID, Location[]> playerSelections;
    private MessagesConfigManager messagesConfigManager;
    private LocationConfigManager locationConfigManager;

    public Location getMobSpawnLocation() {
        return mobSpawnLocation;
    }
    public Location getPotionSpawnArea() {
        return potionSpawnArea;
    }
    public Location getTeamExit() {return teamExit;}
    public Location getScoreboardLocation() {return scoreboardLocation;}
    public World getWorld() {
        return Bukkit.getWorld(world);
    }
    public Integer getScoreboardSize() {return this.scoreboardSize;}

    public void setMobSpawnLocation(Location mobSpawnLocation) {
        this.mobSpawnLocation = mobSpawnLocation;
        locationConfigManager.saveLocation("locations.mobspawnlocation", mobSpawnLocation);
    }
    public void loadMobSpawnLocation(Location mobSpawnLocation) {
        this.mobSpawnLocation = mobSpawnLocation;
    }
    public void setTeamExit (Location teamExit) {
        this.teamExit = teamExit;
        locationConfigManager.saveLocation("locations.teamexitlocation", teamExit);}
    public void loadTeamExit (Location teamExit) {
        this.teamExit = teamExit;
    }
    public void setScoreboardLocation(Location scoreboardLocation) {this.scoreboardLocation = scoreboardLocation;
    }
    public void setGoalLocationConfig(HashMap<String, Location> hashMap) {
        locationConfigManager.setHashMap("locations.goallocations", hashMap);
    }
    public void loadGoalLocation(HashMap<String, Location> hashMap) {
        this.goalLocation = hashMap;
    }
    public void setTeamStartLocationConfig(HashMap<String, Location> hashMap) {
        locationConfigManager.setHashMap("locations.teamstartlocations", hashMap);
    }
    public void loadTeamStartLocation(HashMap<String, Location> hashMap) {
        this.teamStartLocation = hashMap;
    }
    public void setPotionSpawnArea(Location corner1, Location corner2) {
        double centerX = (corner1.getX() + corner2.getX() / 2);
        double centerY = (corner1.getY() + corner2.getY() / 2 + 10);
        double centerZ = (corner1.getZ() + corner2.getZ() / 2);

        potionSpawnArea = new Location(corner1.getWorld(), centerX, centerY, centerZ);
        locationConfigManager.saveLocation("locations.potionspawnarea", potionSpawnArea);
    }

    public void setWinningTeam(String winningTeam) {
        this.winningTeam = winningTeam;
    }

    public Color ChatColorToColor(ChatColor chatColor) {
        return switch (chatColor) {
            case BLACK -> Color.BLACK;
            case DARK_BLUE -> Color.NAVY;
            case DARK_GREEN -> Color.GREEN;
            case DARK_AQUA -> Color.AQUA;
            case DARK_RED -> Color.MAROON;
            case DARK_PURPLE -> Color.PURPLE;
            case GOLD -> Color.ORANGE;
            case GRAY -> Color.SILVER;
            case DARK_GRAY -> Color.GRAY;
            case BLUE -> Color.BLUE;
            case GREEN -> Color.LIME;
            case AQUA -> Color.TEAL;
            case RED -> Color.RED;
            case LIGHT_PURPLE -> Color.FUCHSIA;
            case YELLOW -> Color.YELLOW;
            case WHITE -> Color.WHITE;
            default -> Color.WHITE;
        };
    }
    @Override
    public void onEnable() {
        // plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.messagesConfigStream = getResource("messages.yml");
        this.messagesFile = new File(getDataFolder(), "messages.yml");
        this.goalLocation = new HashMap<>();
        this.teamStartLocation = new HashMap<>();
        this.teamScores = new HashMap<>();
        this.gameRunning = false;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.playerTeams = new HashMap<>();
        this.playerSelections = new HashMap<>();
        this.messagesConfigManager = new MessagesConfigManager(this);
        this.locationConfigManager = new LocationConfigManager(this);

        TeamManager teamManager = new TeamManager(scoreboard);
        CreateScoreboardDisplayTask createScoreboardDisplayTask = new CreateScoreboardDisplayTask(this, teamManager);
        TeamScoreManager teamScoreManager = new TeamScoreManager(this, createScoreboardDisplayTask);
        TitleUtils titleUtils = new TitleUtils(this, teamManager);
        EndGameTask endGameTask = new EndGameTask(this, teamManager, titleUtils);
        TeamScoreWatcher teamScoreWatcher = new TeamScoreWatcher(this, teamManager, teamScoreManager, endGameTask);


        this.teams = teamManager.getTeams();




        getCommand("ms").setExecutor(new MobSoccerCommands(this, endGameTask, teamManager, teamScoreManager, teamScoreWatcher, createScoreboardDisplayTask));
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);

        for (Team team : this.teams) {
            team.unregister();
        }

        Team team1 = teamManager.createTeam("Team1", "Team1", ChatColor.GREEN);
        Team team2 = teamManager.createTeam("Team2", "Team2", ChatColor.GOLD);
        Team team3 = teamManager.createTeam("Team3", "Team3", ChatColor.RED);
        Team team4 = teamManager.createTeam("Team4", "Team4", ChatColor.DARK_PURPLE);

        teamManager.setTeamProperties(team1, ChatColor.GREEN + "Team1", ChatColor.RESET.toString(), false, true);
        teamManager.setTeamProperties(team2, ChatColor.GOLD + "Team2", ChatColor.RESET.toString(), false, true);
        teamManager.setTeamProperties(team3, ChatColor.RED + "Team3", ChatColor.RESET.toString(), false, true);
        teamManager.setTeamProperties(team4, ChatColor.DARK_PURPLE + "Team4", ChatColor.RESET.toString(), false, true);

        List<String> teamNames = teamManager.getTeamNames();
        for (int i = 0; i <= teamNames.size()-1; i++) {
            System.out.println(teamNames.get(i));
            teamScoreManager.setTeamScore(teamNames.get(i), 0);
        }
        System.out.println(teamScores);
    }

    @Override
    public void onDisable() {
        //Cancel any running tasks
        Bukkit.getScheduler().cancelTasks(this);
        for (Team team : this.teams) {
            team.unregister();
        }
        for (Entity entity : this.getWorld().getEntities()) {
            if (entity instanceof Monster && !entity.isDead() && entity.getCustomName() != null && entity.getCustomName().equals("ball")) {
                entity.remove();
            }
        }
    }

    public MessagesConfigManager getMessagesConfigManager() {return messagesConfigManager;}
}
