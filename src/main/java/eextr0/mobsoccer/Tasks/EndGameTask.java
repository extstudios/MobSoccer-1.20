package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Utils.SpoonUtils;
import eextr0.mobsoccer.Utils.TitleUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Team;

public class EndGameTask {
    private final MobSoccer plugin;
    private final TeamManager teamManager;
    private final TitleUtils titleUtils;


    public EndGameTask(MobSoccer plugin, TeamManager teamManager, TitleUtils titleUtils) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.titleUtils = titleUtils;
    }


    public void endGame(String winningTeamName) {

        World world = plugin.getWorld();
        if (!plugin.gameRunning) {
            return; // No game is currently running
        }
        //Set Game running to false and announce the winner
        Location exitLocation = plugin.getTeamExit();
        for (Player p : plugin.getWorld().getPlayers()) {
            Team playerTeam = plugin.playerTeams.get(p);
            if (playerTeam != null) {

                if(exitLocation != null) {
                    p.teleport(exitLocation);
                }
            }
        }
        plugin.gameRunning = false;
        world.getPlayers().forEach(player -> player.sendMessage(plugin.getMessagesConfigManager().getBroadcastMessages().get("gameOver").replace("%team%", winningTeamName)));
        for (Entity entity : plugin.getWorld().getEntities()) {
            if (entity instanceof Monster && !entity.isDead() && entity.getCustomName() != null && entity.getCustomName().equals("ball")) {
                Bukkit.getScheduler().cancelTasks(plugin);
                entity.remove();
            }
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            PlayerInventory inventory = p.getInventory();
            ItemStack spoon = SpoonUtils.createSpoon();
            inventory.removeItem(spoon);
            ItemStack chestplate = p.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.LEATHER_CHESTPLATE) {
                p.getInventory().setChestplate(null);
            }
        }

        //cancel all runnables
        Bukkit.getScheduler().cancelTasks(plugin);
        Team team = teamManager.getTeamByName(winningTeamName);
        titleUtils.displayTitle(team, winningTeamName + " Has won!!!", "", 0, 100, 20);
        //set off fireworks for the winning team
        GameWinFireWorksTask gameWinFireWorksTask = new GameWinFireWorksTask(plugin, teamManager);
        gameWinFireWorksTask.runTaskTimer(plugin, 0, 20);
    }


    public void cancelGame() {
        if (!plugin.gameRunning) {
            return; // No game is currently running
        }

        World world = plugin.getWorld();
        plugin.gameRunning = false;
        world.getPlayers().forEach(player -> player.sendMessage(plugin.getMessagesConfigManager().getBroadcastMessages().get("gameCancel")));
        for (Entity entity : plugin.getWorld().getEntities()) {
            if (entity instanceof Monster && !entity.isDead() && entity.getCustomName() != null && entity.getCustomName().equals("ball")) {
                Bukkit.getScheduler().cancelTasks(plugin);
                entity.remove();
            }
        }
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            PlayerInventory inventory = p.getInventory();
            ItemStack spoon = SpoonUtils.createSpoon();
            inventory.removeItem(spoon);
            ItemStack chestplate = p.getInventory().getChestplate();
            if (chestplate != null && chestplate.getType() == Material.LEATHER_CHESTPLATE) {
                p.getInventory().setChestplate(null);
            }
        }
    }
}