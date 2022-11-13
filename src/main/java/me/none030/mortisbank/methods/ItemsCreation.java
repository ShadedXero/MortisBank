package me.none030.mortisbank.methods;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemsCreation {

    public static ItemStack CreateUpgradeItem(int bankAccount) {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section;
        if (bankAccount > 0) {
            section = upgrades.getConfigurationSection("bank-upgrades." + bankAccount + ".item");
        } else {
            section = upgrades.getConfigurationSection("bank-upgrades.DEFAULT.item");
        }
        assert section != null;
        ItemStack item = new ItemStack(Material.valueOf(section.getString("material")), section.getInt("amount"), (short) section.getInt("data"));
        ItemMeta meta = item.getItemMeta();
        if (section.contains("texture")) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", section.getString("texture")));
            try {
                Method mtd = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(meta, profile);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        if (section.contains("name")) {
            meta.setDisplayName(Objects.requireNonNull(section.getString("name")).replace("&", "§"));
        }
        if (section.contains("lore")) {
            List<String> itemLore = section.getStringList("lore");
            itemLore.replaceAll(s -> s.replace("&", "§"));
            meta.setLore(itemLore);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return item;
    }
}
