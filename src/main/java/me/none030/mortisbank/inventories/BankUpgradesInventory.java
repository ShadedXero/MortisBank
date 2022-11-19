package me.none030.mortisbank.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BankUpgradesInventory implements InventoryHolder {

    private final Inventory inv;

    public BankUpgradesInventory() {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = upgrades.getConfigurationSection("bank-upgrades.OPTIONS");
        assert section != null;

        inv = Bukkit.createInventory(this, section.getInt("inv-size"), Objects.requireNonNull(section.getString("inv-name"))); //54 max size\
        init();
    }

    private void init() {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = upgrades.getConfigurationSection("bank-upgrades.OPTIONS");
        File file2 = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file2);
        ConfigurationSection menuSection = menus.getConfigurationSection("Misc.Filter");
        assert menuSection != null && section != null;

        int slots = section.getInt("inv-size");

        ItemStack placeholder = new ItemStack(Material.valueOf(menuSection.getString("material")), menuSection.getInt("amount"), (short) menuSection.getInt("data"));
        ItemMeta placeholderMeta = placeholder.getItemMeta();
        if (menuSection.contains("texture")) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", menuSection.getString("texture")));
            try {
                Method mtd = placeholderMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(placeholderMeta, profile);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        if (menuSection.contains("name")) {
            placeholderMeta.setDisplayName(Objects.requireNonNull(menuSection.getString("name")).replace("&", "§"));
        }
        if (menuSection.contains("lore")) {
            List<String> itemLore = menuSection.getStringList("lore");
            itemLore.replaceAll(s -> s.replace("&", "§"));
            placeholderMeta.setLore(itemLore);
        }
        if (menuSection.contains("enchants")) {
            for (String enchant : menuSection.getStringList("enchants")) {
                String[] raw = enchant.split(":");
                placeholderMeta.addEnchant(Objects.requireNonNull(Enchantment.getByName(raw[0])), Integer.parseInt(raw[1]), true);
            }
        }
        if (menuSection.contains("flags")) {
            for (String flag : menuSection.getStringList("flags")) {
                placeholderMeta.addItemFlags(ItemFlag.valueOf(flag));
            }
        }
        placeholder.setItemMeta(placeholderMeta);

        for (int i = 0; i < slots; i++) {
            inv.setItem(i, placeholder);
        }

        ItemStack item = new ItemStack(Material.valueOf(section.getString("back-item.material")), section.getInt("back-item.amount"), (short) section.getInt("back-item.data"));
        ItemMeta meta = item.getItemMeta();
        if (section.contains("back-item.texture")) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", section.getString("back-item.texture")));
            try {
                Method mtd = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(meta, profile);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
        if (section.contains("back-item.name")) {
            meta.setDisplayName(Objects.requireNonNull(section.getString("back-item.name")).replace("&", "§"));
        }
        if (section.contains("back-item.lore")) {
            List<String> itemLore = section.getStringList("back-item.lore");
            itemLore.replaceAll(s -> s.replace("&", "§"));
            meta.setLore(itemLore);
        }
        if (section.contains("back-item.enchants")) {
            for (String enchant : section.getStringList("back-item.enchants")) {
                String[] raw = enchant.split(":");
                meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(raw[0])), Integer.parseInt(raw[1]), true);
            }
        }
        if (section.contains("back-item.flags")) {
            for (String flag : section.getStringList("back-item.flags")) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }
        }
        item.setItemMeta(meta);

        inv.setItem(section.getInt("back-item.slot"), item);

    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
