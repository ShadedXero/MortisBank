package me.none030.mortisbank.methods;

import me.none030.mortisbank.MortisBank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.none030.mortisbank.methods.DataMethods.*;

public class Upgrades {

    public static int getMaxAccount() {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = upgrades.getConfigurationSection("bank-upgrades");
        List<String> keys = new ArrayList<>(section.getKeys(false));
        keys.remove("OPTIONS");

        return keys.size() - 1;
    }

    public static double getLimit(int bankAccount) {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section;
        if (bankAccount > 0) {
            section = upgrades.getConfigurationSection("bank-upgrades." + bankAccount + ".upgrades");
        } else {
            section = upgrades.getConfigurationSection("bank-upgrades.DEFAULT.upgrades");
        }
        assert section != null;

        return section.getDouble("max-balance");
    }

    public static String getAccountName(int bankAccount) {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section;
        if (bankAccount > 0) {
            section = upgrades.getConfigurationSection("bank-upgrades." + bankAccount);
        } else {
            section = upgrades.getConfigurationSection("bank-upgrades.DEFAULT");
        }
        assert section != null;

        return Objects.requireNonNull(section.getString("name")).replace("&", "§");
    }

    public static ItemStack AddRequirements(Player player, ItemStack item, int account) {

        DecimalFormat value = new DecimalFormat("#,###.#");
        int bankAccount = getBankAccount(player.getUniqueId());
        double purse = getBankPurse(player.getUniqueId());
        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section;
        if (account > 0) {
            section = upgrades.getConfigurationSection("bank-upgrades." + account);
        } else {
            section = upgrades.getConfigurationSection("bank-upgrades.DEFAULT");
        }
        assert section != null;
        ConfigurationSection interestSection = section.getConfigurationSection("interests");
        ConfigurationSection upgradeSection = section.getConfigurationSection("upgrades");
        ConfigurationSection item2Section = section.getConfigurationSection("item");
        assert interestSection != null && upgradeSection != null && item2Section != null;

        //int slot = item2Section.getInt("slot");
        ItemMeta meta = item.getItemMeta();
        if (meta.getLore() != null) {
            List<String> lore = new ArrayList<>(meta.getLore());
            String account_status = null;

            if (bankAccount != account) {
                if (account > bankAccount) {
                    if (bankAccount + 1 == account) {
                        if (account != 0) {
                            if (section.contains("requirements")) {
                                ConfigurationSection requireSection = section.getConfigurationSection("requirements");
                                assert requireSection != null;
                                boolean money = true;
                                boolean items = true;
                                boolean permissions = true;

                                if (requireSection.contains("permissions")) {
                                    for (String permission : requireSection.getStringList("permissions")) {
                                        if (!player.hasPermission(permission)) {
                                            lore.add(lore.size() - 2, "§cRequires " + permission + " permission!");
                                            lore.add(lore.size() - 2, "");
                                            permissions = false;
                                        }
                                    }
                                }

                                if (requireSection.contains("item")) {
                                    ConfigurationSection itemSection = requireSection.getConfigurationSection("item");
                                    assert itemSection != null;

                                    ItemStack itemStack = new ItemStack(Material.valueOf(itemSection.getString("material")));
                                    ItemMeta itemMeta = itemStack.getItemMeta();
                                    if (itemSection.contains("name")) {
                                        itemMeta.setDisplayName(Objects.requireNonNull(itemSection.getString("name")).replace("&", "§"));
                                    }
                                    if (itemSection.contains("lore")) {
                                        List<String> itemLore = new ArrayList<>(itemSection.getStringList("lore"));
                                        itemLore.replaceAll(s -> s.replace("&", "§"));
                                        itemMeta.setLore(itemLore);
                                    }
                                    if (itemSection.contains("enchants")) {
                                        for (String enchant : itemSection.getStringList("enchants")) {
                                            String[] raw = enchant.split(":");
                                            itemMeta.addEnchant(Objects.requireNonNull(Enchantment.getByName(raw[0])), Integer.parseInt(raw[1]), true);
                                        }
                                    }
                                    if (itemSection.contains("flags")) {
                                        for (String flag : itemSection.getStringList("flags")) {
                                            itemMeta.addItemFlags(ItemFlag.valueOf(flag));
                                        }
                                    }
                                    itemStack.setItemMeta(itemMeta);
                                    int amount = itemSection.getInt("amount");

                                    if (!player.getInventory().containsAtLeast(itemStack, amount)) {
                                        items = false;
                                    }
                                }

                                if (requireSection.contains("money")) {
                                    if (purse < requireSection.getDouble("money")) {
                                        money = false;
                                    }
                                }

                                if (!items) {
                                    account_status = "§cNot enough items!";
                                }
                                if (!money) {
                                    account_status = "§cNot enough coins!";
                                }
                                if (items && money && permissions) {
                                    account_status = "§eClick to upgrade!";
                                }
                            }
                        }
                    } else {
                        account_status = "§cNeed previous upgrade!";
                    }
                } else {
                    account_status = "§cYou have a better account";
                }
            } else {
                account_status = "§aThis is your account";
            }

            for (int i = 0; i < lore.size(); i++) {
                if (lore.get(i).contains("%max_interest%")) {
                    lore.set(i, lore.get(i).replace("%max_interest%", String.valueOf(interestSection.getDouble("max-interest"))));
                }
                if (lore.get(i).contains("%max_interest_bank%")) {
                    List<String> keys = new ArrayList<>(interestSection.getKeys(false));
                    keys.remove("max-interest");
                    lore.set(i, lore.get(i).replace("%max_interest_bank%", value.format(interestSection.getDouble(keys.get(keys.size() - 1) + ".to"))));
                }
                if (lore.get(i).contains("%max_balance%")) {
                    lore.set(i, lore.get(i).replace("%max_balance%", value.format(upgradeSection.getDouble("max-balance"))));
                }
                if (lore.get(i).contains("%account_status%")) {
                    if (account_status != null) {
                        lore.set(i, lore.get(i).replace("%account_status%", account_status));
                    }
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void onUpgrade(Player player, int slot) {

        int bankAccount = getBankAccount(player.getUniqueId());
        Economy economy = MortisBank.getEconomy();
        double purse = getBankPurse(player.getUniqueId());
        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection upgradesSection = upgrades.getConfigurationSection("bank-upgrades");
        assert upgradesSection != null;
        ConfigurationSection section = null;
        int account = 0;
        List<String> keys = new ArrayList<>(upgradesSection.getKeys(false));
        keys.remove("OPTIONS");
        keys.remove("DEFAULT");

        for (String key : keys) {
            ConfigurationSection keysSection = upgradesSection.getConfigurationSection(key + ".item");
            assert keysSection != null;

            if (keysSection.contains("slot")) {
                if (keysSection.getInt("slot") == slot) {
                    section = upgradesSection.getConfigurationSection(key);
                    account = Integer.parseInt(key);
                    break;
                }
            }
        }

        if (section != null) {
            if (bankAccount + 1 == account) {
                if (section.contains("requirements")) {
                    ConfigurationSection requireSection = section.getConfigurationSection("requirements");
                    assert requireSection != null;
                    boolean money = true;
                    boolean items = true;
                    boolean permissions = true;

                    if (requireSection.contains("permissions")) {
                        for (String permission : requireSection.getStringList("permissions")) {
                            if (!player.hasPermission(permission)) {
                                permissions = false;
                            }
                        }
                    }

                    ItemStack itemStack = null;
                    int amount = 0;
                    if (requireSection.contains("item")) {
                        ConfigurationSection itemSection = requireSection.getConfigurationSection("item");
                        assert itemSection != null;

                        itemStack = new ItemStack(Material.valueOf(itemSection.getString("material")));
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        if (itemSection.contains("name")) {
                            itemMeta.setDisplayName(Objects.requireNonNull(itemSection.getString("name")).replace("&", "§"));
                        }
                        if (itemSection.contains("lore")) {
                            List<String> itemLore = new ArrayList<>(itemSection.getStringList("lore"));
                            itemLore.replaceAll(s -> s.replace("&", "§"));
                            itemMeta.setLore(itemLore);
                        }
                        if (itemSection.contains("enchants")) {
                            for (String enchant : itemSection.getStringList("enchants")) {
                                String[] raw = enchant.split(":");
                                itemMeta.addEnchant(Objects.requireNonNull(Enchantment.getByName(raw[0])), Integer.parseInt(raw[1]), true);
                            }
                        }
                        if (itemSection.contains("flags")) {
                            for (String flag : itemSection.getStringList("flags")) {
                                itemMeta.addItemFlags(ItemFlag.valueOf(flag));
                            }
                        }
                        itemStack.setItemMeta(itemMeta);
                        amount = itemSection.getInt("amount");

                        if (!player.getInventory().containsAtLeast(itemStack, amount)) {
                            items = false;
                        }
                    }

                    if (requireSection.contains("money")) {
                        if (purse < requireSection.getDouble("money")) {
                            money = false;
                        }
                    }

                    if (permissions && items && money) {
                        ChangeBankAccount(player.getUniqueId(), bankAccount + 1);
                        if (requireSection.contains("money")) {
                            economy.withdrawPlayer(player.getName(), requireSection.getDouble("money"));
                        }
                        if (requireSection.contains("item")) {
                            assert itemStack != null;
                            for (int i = 0; i < amount; i++) {
                                player.getInventory().removeItem(itemStack);
                            }
                        }
                        player.closeInventory();
                    } else {
                        player.sendMessage("§cYou don't have the requirements to upgrade this!");
                    }
                } else {
                    player.sendMessage("§cYou don't have the requirements to upgrade this!");
                }
            } else {
                player.sendMessage("§cYou don't have the requirements to upgrade this!");
            }
        } else {
            player.sendMessage("§cYou don't have the requirements to upgrade this!");
        }
    }
}
