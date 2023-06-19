package eextr0.mobsoccer.Listeners;

import eextr0.mobsoccer.MobSoccer;
import eextr0.mobsoccer.Utils.SpoonUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.net.http.WebSocket;

public class PlayerLeaveListener implements Listener {

    private final MobSoccer plugin;

    public PlayerLeaveListener(MobSoccer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        PlayerInventory inventory = p.getInventory();
        ItemStack spoon = SpoonUtils.createSpoon();
        inventory.removeItem(spoon);
        ItemStack chestplate = p.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.LEATHER_CHESTPLATE && chestplate.getItemMeta().getDisplayName() == "Jersey") {
            p.getInventory().setChestplate(null);
        }
    }
}
