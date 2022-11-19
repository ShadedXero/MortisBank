package me.none030.mortisbank.methods;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.none030.mortisbank.utils.MenuItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StoringMenuItems {

    public static List<MenuItem> PersonalBankItems = new ArrayList<>();
    public static List<MenuItem> BankDepositItems = new ArrayList<>();
    public static List<MenuItem> BankWithdrawItems = new ArrayList<>();

    public static void StoreMenuItems(String menu) {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = menus.getConfigurationSection(menu);
        assert section != null;

        List<String> keys = new ArrayList<>(section.getKeys(false));
        keys.remove("inv-name");
        keys.remove("inv-size");

        for (String key : keys) {
            ConfigurationSection itemSection = section.getConfigurationSection(key);
            assert itemSection != null;
            if (itemSection.contains("slot")) {
                int slot = itemSection.getInt("slot");
                if (slot != -1) {
                    ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("material")), itemSection.getInt("amount"), (short) itemSection.getInt("data"));
                    ItemMeta meta = item.getItemMeta();
                    if (itemSection.contains("texture")) {
                        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                        profile.getProperties().put("textures", new Property("textures", itemSection.getString("texture")));
                        try {
                            Method mtd = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                            mtd.setAccessible(true);
                            mtd.invoke(meta, profile);
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (itemSection.contains("name")) {
                        meta.setDisplayName(Objects.requireNonNull(itemSection.getString("name")).replace("&", "§"));
                    }
                    if (itemSection.contains("lore")) {
                        List<String> itemLore = itemSection.getStringList("lore");
                        itemLore.replaceAll(s -> s.replace("&", "§"));
                        meta.setLore(itemLore);
                    }
                    if (itemSection.contains("enchants")) {
                        for (String enchant : itemSection.getStringList("enchants")) {
                            String[] raw = enchant.split(":");
                            meta.addEnchant(Objects.requireNonNull(Enchantment.getByName(raw[0])), Integer.parseInt(raw[1]), true);
                        }
                    }
                    if (itemSection.contains("flags")) {
                        for (String flag : itemSection.getStringList("flags")) {
                            meta.addItemFlags(ItemFlag.valueOf(flag));
                        }
                    }
                    item.setItemMeta(meta);

                    if (menu.equalsIgnoreCase("Personal-Bank")) {
                        PersonalBankItems.add(new MenuItem(item, slot));
                    }
                    if (menu.equalsIgnoreCase("Bank-Deposit")) {
                        BankDepositItems.add(new MenuItem(item, slot));
                    }
                    if (menu.equalsIgnoreCase("Bank-Withdrawal")) {
                        BankWithdrawItems.add(new MenuItem(item, slot));
                    }
                }
            }
        }
    }

    public static void StoreAllMenusItems() {

        PersonalBankItems.clear();
        BankDepositItems.clear();
        BankWithdrawItems.clear();
        StoreMenuItems("Personal-Bank");
        StoreMenuItems("Bank-Deposit");
        StoreMenuItems("Bank-Withdrawal");

    }
}
