package me.none030.mortisbank.inventories;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.none030.mortisbank.utils.MenuItem;
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

import static me.none030.mortisbank.methods.StoringMenuItems.BankWithdrawItems;

public class WithdrawInventory implements InventoryHolder {

    private final Inventory inv;

    public WithdrawInventory() {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = menus.getConfigurationSection("Bank-Withdrawal");
        assert section != null;

        inv = Bukkit.createInventory(this, section.getInt("inv-size"), Objects.requireNonNull(section.getString("inv-name"))); //54 max size\
        init();
    }

    private void init() {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection menuSection = menus.getConfigurationSection("Misc.Filter");
        assert menuSection != null;

        int slots = menus.getInt("Bank-Withdrawal.inv-size");

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

        for (MenuItem menuItem : BankWithdrawItems) {
            inv.setItem(menuItem.getSlot(), menuItem.getItem());
        }
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
