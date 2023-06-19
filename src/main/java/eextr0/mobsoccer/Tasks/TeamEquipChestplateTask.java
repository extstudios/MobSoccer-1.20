package eextr0.mobsoccer.Tasks;

import eextr0.mobsoccer.Managers.TeamManager;
import eextr0.mobsoccer.MobSoccer;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

public class TeamEquipChestplateTask extends BukkitRunnable {

    private final MobSoccer plugin;
    private final TeamManager teamManager;

    public TeamEquipChestplateTask(MobSoccer plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
    }

    public void run() {
        for (Player p : plugin.getWorld().getPlayers()) {
            Team playerTeam = plugin.playerTeams.get(p);
            if (playerTeam != null) {
                String teamName = playerTeam.getName();
                Team team = teamManager.getTeamByName(teamName);
                ChatColor teamColor = teamManager.getTeamColor(team);
                ItemStack chestplate = createColoredChestplate(teamColor);
                p.getInventory().setChestplate(chestplate);
            }
        }
    }

    private ItemStack createColoredChestplate(ChatColor color) {
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        if (armorMeta != null) {
            Color leatherColor = plugin.ChatColorToColor(color);
            armorMeta.setColor(leatherColor);
            armorMeta.setDisplayName("Jersey");
            chestplate.setItemMeta(armorMeta);
        }
        return chestplate;
    }
}

