package eextr0.mobsoccer.Commands;
import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.Managers.TeamScoreManager;
import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Tasks.CreateScoreboardDisplayTask;
import eextr0.mobsoccer.Tasks.EndGameTask;
import eextr0.mobsoccer.Tasks.StartGameTask;
import eextr0.mobsoccer.Utils.*;
import eextr0.mobsoccer.Watchers.TeamScoreWatcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

public class MobSoccerCommands implements CommandExecutor {

    public Location mobSpawnLocation;
    public Location potionSpawnArea;
    private final MobSoccer plugin;
    private final TeamManager teamManager;
    private final TeamScoreManager teamScoreManager;
    private final TeamScoreWatcher teamScoreWatcher;
    private final EndGameTask endGameTask;
    private final List<Team> teams;
    private final Scoreboard scoreboard;
    private final CreateScoreboardDisplayTask createScoreboardDisplayTask;

    public MobSoccerCommands(MobSoccer plugin, EndGameTask endGameTask,TeamManager teamManager, TeamScoreManager teamScoreManager, TeamScoreWatcher teamScoreWatcher,
                             CreateScoreboardDisplayTask createScoreboardDisplayTask) {
        this.plugin = plugin;
        this.endGameTask = endGameTask;
        this.teamManager = teamManager;
        this.teamScoreManager = teamScoreManager;
        this.teamScoreWatcher = teamScoreWatcher;
        this.teams = teamManager.getTeams();
        this.scoreboard = plugin.scoreboard;
        this.createScoreboardDisplayTask = createScoreboardDisplayTask;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (args.length == 0 ) {
            commandSender.sendMessage("Plugin: MobSoccer");
            commandSender.sendMessage("CommandList:");
            commandSender.sendMessage("/ms start - Start the game");
            commandSender.sendMessage("/ms cancel - cancel the running game");
            commandSender.sendMessage("/ms spoon - give yourself a spoon");
            commandSender.sendMessage("/ms setgoal <team> - set the goal location for a team");
            commandSender.sendMessage("/ms setmobspawn - set the spawn location for the mobs during the game");
            commandSender.sendMessage("/ms setpotionarea - set the center of the area for potions to spawn during the game");
            commandSender.sendMessage("/ms setteamstart <team> - set the area for the designated team to be teleported to when the game starts");
            commandSender.sendMessage("/ms setteamexit - set the area that teams are teleported to after the game ends");
            commandSender.sendMessage("/ms teams - provide a list of the current team Names and colors");
            commandSender.sendMessage("/ms setteam <player> <team> - assign player to the specified team");
            commandSender.sendMessage("/ms clearteams - removes players from all teams");
            commandSender.sendMessage("/ms changeteam <teamName> <newTeamName> <newTeamColor> - change a team's name and color");
            commandSender.sendMessage("/ms clearscoreboard - remove scoreboard from all players");

            return true;
        }

        switch (args[0]) {
            case "start" -> {

                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.start")) {

                    if (plugin.gameRunning) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("gameInProgress"));
                        return true;
                    }

                    if (plugin.getMobSpawnLocation() == null) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("mobSpawnLocationNotSet"));
                        System.out.println(plugin.getMobSpawnLocation());
                        return true;
                    }

                    if (plugin.goalLocation.size() < 4) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("goalLocationsNotSet"));
                        return true;
                    }
                    if (plugin.teamStartLocation.size() < 4) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("teamStartLocationsNotSet"));
                        return true;
                    }

                    if (plugin.getTeamExit() == null) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("teamExitNotSet"));
                        System.out.println(plugin.getTeamExit());
                    }
                    List<String> teamNames = teamManager.getTeamNames();
                    for (int i = 0; i <= teamNames.size() - 1; i++) {
                        System.out.println(teamNames.get(i));
                        teamScoreManager.setTeamScore(teamNames.get(i), 0);
                    }

                    Location scoreboardLocation = plugin.getScoreboardLocation();
                    createScoreboardDisplayTask.createDisplay(plugin.teamScores, scoreboardLocation);

                    plugin.gameRunning = true;
                    new StartGameTask(plugin, teamManager, teamScoreManager, teamScoreWatcher, createScoreboardDisplayTask).runTaskTimer(plugin, 0L, 20L);
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
                return true;
            }
            case "spoon" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.spoon")) {
                    if (args.length == 1) {
                        ItemStack spoon = SpoonUtils.createSpoon();
                        p.getInventory().addItem(spoon);
                        p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("giveSpoon"));
                    }

                    if (args.length == 2) {
                        String playerName = args[1];
                        ItemStack spoon = SpoonUtils.createSpoon();
                        Player checkPlayer = Bukkit.getPlayer(playerName);
                        if (checkPlayer == null) {
                            p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("playerNotFound").replace("%player%", playerName));
                            return true;
                        }
                        checkPlayer.getInventory().addItem(spoon);
                        checkPlayer.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("giveSpoon"));
                    }
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                } return true;

            }
            case "setgoal" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setgoal")) {

                    if (args.length != 2) {
                        p.sendMessage("Usage: /ms setgoal <team>");
                        return true;
                    }

                    String teamName = args[1];
                    if (teamManager.getTeamByName(teamName) != null) {
                        Location location = p.getLocation();
                        plugin.goalLocation.put(teamName, location);
                        plugin.setGoalLocationConfig(plugin.goalLocation);
                        p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("goalLocationSet").replace("%team%", teamName));
                    } else {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("invalidTeam").replace("%team%", teamName));
                    }
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }

            }
            case "setteamstart" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setteamstart")) {
                    if (args.length != 2) {
                        p.sendMessage("Usage: /setteamstart <team>");
                        return true;
                    }

                    String teamName = args[1];
                    if (teamManager.getTeamByName(teamName) != null) {
                        Location location = p.getLocation();
                        plugin.teamStartLocation.put(teamName, location);
                        plugin.setTeamStartLocationConfig(plugin.teamStartLocation);
                        p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("teamStartLocationSet").replace("%team%", teamName));
                    } else {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("invalidTeam").replace("%team%", teamName));
                    }

                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                } return true;
            }
            case "setteamexit" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setteamexit")) {
                    if (args.length != 1) {
                        p.sendMessage("Usage: /setteamexit");
                        return true;
                    }

                    Location teamExit = p.getLocation();
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("teamExitLocationSet"));
                    plugin.setTeamExit(teamExit);

                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                } return true;
            }
            case "setmobspawn" -> {

                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setmobspawn")) {
                    if (args.length != 1) {
                        p.sendMessage("Usage: /ms setmobspawn");
                        return true;
                    }

                    mobSpawnLocation = p.getLocation();
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("mobSpawnLocationSet"));
                    plugin.setMobSpawnLocation(mobSpawnLocation);
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
            }
            case "setteam" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setteam")) {
                    if (args.length != 3) {
                        p.sendMessage("Usage: /ms setteam <player> <team>");
                        return true;
                    }
                    String playerName = args[1];
                    String teamName = args[2];

                    //Fetch the team from the TeamManager
                    Team team = teamManager.getTeamByName(teamName);

                    if (team == null) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("invalidTeam").replace("%team%", teamName));
                        return true;
                    }

                    Player checkPlayer = Bukkit.getPlayer(playerName);

                    if (checkPlayer == null) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("playerNotFound").replace("%player%", playerName));
                        return true;
                    }

                    team.addEntry(playerName);
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("confirmPlayerAddedToTeam").replace("%player%", playerName)
                            .replace("%team%", teamName));
                    checkPlayer.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("tellPlayerAddedToTeam").replace("%team%", teamName));
                    plugin.playerTeams.put(checkPlayer, team);
                    return true;

                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }

            }
            case "removeteam" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.removeteam")) {
                    if (args.length != 3) {
                        p.sendMessage("Usage: /ms removeteam <player> <team>");
                        return true;
                    }
                    String playerName = args[1];
                    Team team = plugin.playerTeams.get(playerName);
                    String teamName = String.valueOf((team));

                    Player checkPlayer = Bukkit.getPlayer(playerName);

                    if (checkPlayer == null) {
                        p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("playerNotFound").replace("%player%", playerName));
                        return true;
                    }
                    team.removeEntry(playerName);
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("confirmPlayerRemovedFromTeam").replace("%player%", playerName)
                            .replace("%team%", teamName));
                    checkPlayer.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("tellPlayerRemovedFromTeam").replace("%team%", teamName));
                    plugin.playerTeams.remove(checkPlayer, team);
                    return true;
                }

            }
            case "cancel" -> {
                if (commandSender.hasPermission("mobsoccer.cancel")) {
                    endGameTask.cancelGame();
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
            }
            case "clearteams" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.clearteams")) {

                    for (Team team : scoreboard.getTeams()) {
                        team.getEntries().forEach(entry -> team.removeEntry(entry));
                    }
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("teamsCleared"));
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
            }
            case "setpotionarea" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setpotionarea")) {
                    potionSpawnArea = p.getLocation();
                    int halfSize = 10;
                    Location corner1 = new Location(potionSpawnArea.getWorld(), potionSpawnArea.getX() - halfSize, potionSpawnArea.getY() - halfSize + 10, potionSpawnArea.getZ() - halfSize);
                    Location corner2 = new Location(potionSpawnArea.getWorld(), potionSpawnArea.getX() + halfSize, potionSpawnArea.getY() - halfSize + 10, potionSpawnArea.getZ() + halfSize);
                    plugin.setPotionSpawnArea(corner1, corner2);
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("potionSpawnAreaSet"));
                }
            }
            case "setscoreboardlocation" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.setscoreboardlocation")) {
                    if (args.length > 1) {
                        p.sendMessage("Usage: /ms setscoreboardlocation");
                    }

                    Location location = p.getLocation();
                    createScoreboardDisplayTask.createDisplay(plugin.teamScores, location);
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("scoreboardLocationSet"));
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
            }
            case "rotatescoreboard" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.rotatescoreboard")) {
                    if (args.length > 1) {
                        p.sendMessage("Usage: /ms rotatescoreboard");
                    }

                    Location location = plugin.getScoreboardLocation();
                    createScoreboardDisplayTask.rotateScoreboard(location, plugin.teamScores);
                    p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("scoreboardRotated"));
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
            }
            case "changeteam" -> {
                if (commandSender instanceof Player p && p.hasPermission("mobsoccer.changeteam")) {
                    if (args.length < 4) {
                        p.sendMessage("Usage: /ms changteam <teamName> <newTeamName> <newColor>");
                        return true;
                    }
                    String teamName = args[1];
                    String newTeamName = args[2];
                    Team team = teamManager.getTeamByName(teamName);

                    if (args.length == 4) {
                        ChatColor newColor = ChatColor.valueOf(args[3].toUpperCase());

                        if (team == null) {
                            p.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("invalidTeam").replace("%team%", teamName));
                            return true;
                        }
                        teamManager.setTeamName(teamName, newTeamName, newColor);
                        p.sendMessage(plugin.getMessagesConfigManager().getCommandMessages().get("teamNameChanged").replace("%team%",teamName)
                                .replace("%newTeam%", newTeamName).replace("%color", newColor.toString()));
                    }
                    if (plugin.goalLocation.get(teamName) != null) {
                        Location goalLocation = plugin.goalLocation.get(teamName);
                        plugin.goalLocation.remove(teamName, goalLocation);
                        plugin.goalLocation.put(newTeamName, goalLocation);
                    }
                    if (plugin.teamStartLocation.get(teamName) != null) {
                        Location teamStartLocation = plugin.teamStartLocation.get(teamName);
                        plugin.teamStartLocation.remove(teamName, teamStartLocation);
                        plugin.teamStartLocation.put(newTeamName, teamStartLocation);
                    }
                    if(plugin.teamScores.containsKey(teamName)) {
                        Integer score = plugin.teamScores.get(teamName);
                        plugin.teamScores.remove(teamName, score);
                        plugin.teamScores.put(newTeamName, score);
                    }
                    Location scoreboardLocation = plugin.getScoreboardLocation();
                    scoreboardLocation.subtract(0,2,0);
                    createScoreboardDisplayTask.createDisplay(plugin.teamScores, scoreboardLocation);
                    return true;
                } else {
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("noPermission"));
                }
            }
            case "teams" -> {
                if (commandSender.hasPermission("mobsoccer.teams")) {

                    for (Team team : teams) {
                        commandSender.sendMessage(team.getColor() + team.getName());
                    }

                }
            }
            default ->
                    commandSender.sendMessage(plugin.getMessagesConfigManager().getErrorMessages().get("unknownCommand"));
        }
        return true;
    }
}