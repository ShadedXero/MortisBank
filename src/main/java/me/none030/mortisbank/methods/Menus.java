package me.none030.mortisbank.methods;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.none030.mortisbank.inventories.BankUpgradesInventory;
import me.none030.mortisbank.inventories.DepositInventory;
import me.none030.mortisbank.inventories.PersonalBankInventory;
import me.none030.mortisbank.inventories.WithdrawInventory;
import me.none030.mortisbank.utils.DepositType;
import me.none030.mortisbank.utils.TransactionType;
import me.none030.mortisbank.utils.WithdrawType;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.MortisBank.getEconomy;
import static me.none030.mortisbank.methods.Depositing.onDeposit;
import static me.none030.mortisbank.methods.Interests.getInterestTime;
import static me.none030.mortisbank.methods.SignGUI.SendSignGUI;
import static me.none030.mortisbank.methods.StoringDatabaseData.getDatabaseAccount;
import static me.none030.mortisbank.methods.StoringDatabaseData.getDatabaseBalance;
import static me.none030.mortisbank.methods.StoringYAMLData.getYamlAccount;
import static me.none030.mortisbank.methods.StoringYAMLData.getYamlBalance;
import static me.none030.mortisbank.methods.TransactionsRecords.Transactions;
import static me.none030.mortisbank.methods.TransactionsRecords.getDuration;
import static me.none030.mortisbank.methods.Upgrades.getAccountName;
import static me.none030.mortisbank.methods.Upgrades.getLimit;
import static me.none030.mortisbank.methods.Withdrawing.onWithdraw;

public class Menus {

    public static void CreateMenuItems(Player player, Inventory inv, String invName) {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = menus.getConfigurationSection(invName);
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

                    ItemStack listedItem = AddPlaceholderValues(player, item);

                    inv.setItem(slot, listedItem);
                }
            }
        }
    }

    public static ItemStack AddPlaceholderValues(Player player, ItemStack item) {

        DecimalFormat value = new DecimalFormat("#,###.#");
        Economy economy = getEconomy();
        double balance;
        int account;
        if (database) {
            balance = getDatabaseBalance(player.getUniqueId());
            account = getDatabaseAccount(player.getUniqueId());
        } else {
            balance = getYamlBalance(player.getUniqueId());
            account = getYamlAccount(player.getUniqueId());
        }
        double purse = economy.getBalance(player.getName());
        long interest = getInterestTime(player);
        double limit = getLimit(account);
        String name = getAccountName(account);

        ItemMeta meta = item.getItemMeta();
        if (meta.getLore() != null) {
            List<String> lore = new ArrayList<>(meta.getLore());
            for (int i = 0; i < lore.size(); i++) {
                String line = lore.get(i);

                if (line.contains("%recent_transactions%")) {
                    lore.remove(line);
                    if (!Transactions.containsKey(player.getUniqueId())) {
                        lore.add("§7There are no recent");
                        lore.add("§7transactions!");
                    } else {
                        List<String> transactions = new ArrayList<>();
                        for (String line2 : Transactions.get(player.getUniqueId())) {
                            String timeInString = StringUtils.substringBetween(line2, "%", "%");
                            LocalDateTime time = LocalDateTime.parse(timeInString);
                            String duration = getDuration(time, LocalDateTime.now());
                            assert duration != null;

                            String transaction = line2.replace(timeInString, "").replace("%%", duration);

                            transactions.add(transaction);
                        }
                        Collections.reverse(transactions);
                        lore.addAll(transactions);
                    }
                }

                if (line.contains("%bank_balance%")) {
                    lore.set(i, line.replace("%bank_balance%", value.format(balance)));
                }

                if (line.contains("%interest_time%")) {
                    lore.set(i, line.replace("%interest_time%", interest + "h"));
                }

                if (line.contains("%bank_limit%")) {
                    lore.set(i, line.replace("%bank_limit%", value.format(limit)));
                }

                if (line.contains("%bank_account%")) {
                    lore.set(i, line.replace("%bank_account%", name));
                }

                if (line.contains("%deposit_whole_purse%")) {
                    lore.set(i, line.replace("%deposit_whole_purse%", value.format(purse)));
                }

                if (line.contains("%deposit_half_purse%")) {
                    lore.set(i, line.replace("%deposit_half_purse%", value.format(purse / 2)));
                }

                if (line.contains("%withdraw_everything_in_account%")) {
                    lore.set(i, line.replace("%withdraw_everything_in_account%", value.format(balance)));
                }

                if (line.contains("%withdraw_half_the_account%")) {
                    lore.set(i, line.replace("%withdraw_half_the_account%", value.format(balance / 2)));
                }

                if (line.contains("%withdraw_20_the_account%")) {
                    lore.set(i, line.replace("%withdraw_20_the_account%", value.format(balance * 0.20)));
                }
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    public static void CheckPersonalBankItem(Player player, int slot) {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = menus.getConfigurationSection("Personal-Bank");
        assert section != null;

        if (slot == section.getInt("Deposit-Coins.slot")) {
            DepositInventory holder = new DepositInventory();
            player.openInventory(holder.getInventory());
            return;
        }

        if (slot == section.getInt("Withdraw-Coins.slot")) {
            WithdrawInventory holder = new WithdrawInventory();
            player.openInventory(holder.getInventory());
            return;
        }

        if (slot == section.getInt("Bank-Upgrades.slot")) {
            BankUpgradesInventory holder = new BankUpgradesInventory();
            player.openInventory(holder.getInventory());
            return;
        }

        if (slot == section.getInt("Close.slot")) {
            player.closeInventory();
        }
    }

    public static void CheckDepositItem(Player player, int slot) {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = menus.getConfigurationSection("Bank-Deposit");
        assert section != null;

        if (slot == section.getInt("Your-Whole-Purse.slot")) {
            onDeposit(player, DepositType.ALL);
            return;
        }

        if (slot == section.getInt("Half-Your-Purse.slot")) {
            onDeposit(player, DepositType.HALF);
            return;
        }

        if (slot == section.getInt("Specific-Amount.slot")) {
            SendSignGUI(player, TransactionType.DEPOSIT);
            return;
        }

        if (slot == section.getInt("Go-Back.slot")) {
            PersonalBankInventory holder = new PersonalBankInventory();
            player.openInventory(holder.getInventory());
        }
    }

    public static void CheckWithdrawItem(Player player, int slot) {

        File file = new File("plugins/MortisBank/", "menus.yml");
        FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = menus.getConfigurationSection("Bank-Withdrawal");
        assert section != null;

        if (slot == section.getInt("Everything-in-Account.slot")) {
            onWithdraw(player, WithdrawType.ALL);
            return;
        }

        if (slot == section.getInt("Half-The-Account.slot")) {
            onWithdraw(player, WithdrawType.HALF);
            return;
        }

        if (slot == section.getInt("Withdraw-20.slot")) {
            onWithdraw(player, WithdrawType.TWENTY);
            return;
        }

        if (slot == section.getInt("Specific-Amount.slot")) {
            SendSignGUI(player, TransactionType.WITHDRAW);
            return;
        }

        if (slot == section.getInt("Go-Back.slot")) {
            PersonalBankInventory holder = new PersonalBankInventory();
            player.openInventory(holder.getInventory());
        }
    }
}
