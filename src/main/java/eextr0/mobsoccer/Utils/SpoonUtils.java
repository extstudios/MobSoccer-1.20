package eextr0.mobsoccer.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpoonUtils {

    public static ItemStack createSpoon(){
        ItemStack spoon = new ItemStack(Material.STICK, 1);
        ItemMeta spoonMeta = spoon.getItemMeta();
        spoonMeta.setDisplayName(ChatColor.DARK_RED + "Spoon!");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "CAUTION: " + ChatColor.GREEN + "Keep away from the Dish");
        spoonMeta.setLore(lore);
        spoonMeta.addEnchant(Enchantment.KNOCKBACK, 2, false);
        spoon.setItemMeta(spoonMeta);
        return spoon;
    }

}
