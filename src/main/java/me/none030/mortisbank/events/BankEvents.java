package me.none030.mortisbank.events;

import me.none030.mortisbank.inventories.BankUpgradesInventory;
import me.none030.mortisbank.inventories.DepositInventory;
import me.none030.mortisbank.inventories.PersonalBankInventory;
import me.none030.mortisbank.inventories.WithdrawInventory;
import me.none030.mortisbank.utils.MenuItem;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.methods.Interests.*;
import static me.none030.mortisbank.methods.ItemsCreation.CreateUpgradeItem;
import static me.none030.mortisbank.methods.Menus.*;
import static me.none030.mortisbank.methods.StoringDatabaseData.*;
import static me.none030.mortisbank.methods.StoringYAMLData.*;
import static me.none030.mortisbank.methods.Upgrades.*;

public class BankEvents implements Listener {

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {

        Player player = (Player) e.getPlayer();
        Inventory inv = e.getInventory();

        if (inv.getHolder() instanceof PersonalBankInventory || inv.getHolder() instanceof DepositInventory || inv.getHolder() instanceof WithdrawInventory) {
            if (database) {
                if (getDatabaseBalance(player.getUniqueId()) == -1) {
                    StoreDatabaseData(player.getUniqueId(), 0, 0);
                }
            } else {
                if (getYamlBalance(player.getUniqueId()) == -1) {
                    StoreYamlData(player.getUniqueId(), 0, 0);
                }
            }
        }

        if (inv.getHolder() instanceof BankUpgradesInventory) {

            File file = new File("plugins/MortisBank/", "upgrades.yml");
            FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = upgrades.getConfigurationSection("bank-upgrades");
            assert section != null;
            List<String> keys = new ArrayList<>(section.getKeys(false));
            keys.remove("OPTIONS");
            keys.remove("DEFAULT");
            keys.add("0");

            for (String key : keys) {
                ConfigurationSection upgradeSection = section.getConfigurationSection(key);
                assert upgradeSection != null;

                ItemStack item = CreateUpgradeItem(Integer.parseInt(key));
                MenuItem upgradeItem = AddRequirements(player, item, Integer.parseInt(key));

                inv.setItem(upgradeItem.getSlot(), upgradeItem.getItem());
            }

            File file2 = new File("plugins/MortisBank/", "menus.yml");
            FileConfiguration menus = YamlConfiguration.loadConfiguration(file2);
            ConfigurationSection menuSection = menus.getConfigurationSection("Misc.Sounds");
            assert menuSection != null;

            if (menuSection.getBoolean("enabled")) {
                player.playSound(player, Sound.valueOf(menuSection.getString("sound")), menuSection.getInt("volume"), menuSection.getInt("pitch"));
            }
        }

        if (inv.getHolder() instanceof PersonalBankInventory) {

            CreateMenuItems(player, inv, "Personal-Bank");

            File file = new File("plugins/MortisBank/", "menus.yml");
            FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection menuSection = menus.getConfigurationSection("Misc.Sounds");
            assert menuSection != null;

            if (menuSection.getBoolean("enabled")) {
                player.playSound(player, Sound.valueOf(menuSection.getString("sound")), menuSection.getInt("volume"), menuSection.getInt("pitch"));
            }

        }

        if (inv.getHolder() instanceof DepositInventory) {

            CreateMenuItems(player, inv, "Bank-Deposit");

            File file = new File("plugins/MortisBank/", "menus.yml");
            FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection menuSection = menus.getConfigurationSection("Misc.Sounds");
            assert menuSection != null;

            if (menuSection.getBoolean("enabled")) {
                player.playSound(player, Sound.valueOf(menuSection.getString("sound")), menuSection.getInt("volume"), menuSection.getInt("pitch"));
            }

        }

        if (inv.getHolder() instanceof WithdrawInventory) {

            CreateMenuItems(player, inv, "Bank-Withdrawal");

            File file = new File("plugins/MortisBank/", "menus.yml");
            FileConfiguration menus = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection menuSection = menus.getConfigurationSection("Misc.Sounds");
            assert menuSection != null;

            if (menuSection.getBoolean("enabled")) {
                player.playSound(player, Sound.valueOf(menuSection.getString("sound")), menuSection.getInt("volume"), menuSection.getInt("pitch"));
            }

        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory() == null) {
            return;
        }

        if (e.getClickedInventory() instanceof PlayerInventory && e.getInventory().getHolder() instanceof PersonalBankInventory || e.getInventory().getHolder() instanceof DepositInventory || e.getInventory().getHolder() instanceof WithdrawInventory || e.getInventory().getHolder() instanceof BankUpgradesInventory) {
            e.setCancelled(true);
        }

        if (e.getClickedInventory().getHolder() instanceof PersonalBankInventory) {
            e.setCancelled(true);

            CheckPersonalBankItem(player, e.getRawSlot());

        }
        if (e.getClickedInventory().getHolder() instanceof DepositInventory) {
            e.setCancelled(true);

            CheckDepositItem(player, e.getRawSlot());

        }
        if (e.getClickedInventory().getHolder() instanceof WithdrawInventory) {
            e.setCancelled(true);

            CheckWithdrawItem(player, e.getRawSlot());

        }
        if (e.getClickedInventory().getHolder() instanceof BankUpgradesInventory) {
            e.setCancelled(true);

            File file = new File("plugins/MortisBank/", "upgrades.yml");
            FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = upgrades.getConfigurationSection("bank-upgrades.OPTIONS");
            assert section != null;

            if (section.contains("back-item.slot")) {
                if (e.getRawSlot() == section.getInt("back-item.slot")) {
                    PersonalBankInventory holder = new PersonalBankInventory();
                    player.openInventory(holder.getInventory());
                    return;
                }
            }

            onUpgrade(player, e.getRawSlot());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {

        if (e.getInventory().getHolder() instanceof BankUpgradesInventory || e.getInventory().getHolder() instanceof PersonalBankInventory || e.getInventory().getHolder() instanceof DepositInventory || e.getInventory().getHolder() instanceof WithdrawInventory) {
            e.setCancelled(true);
        }
        if (e.getInventory() instanceof PlayerInventory && e.getInventory().getHolder() instanceof BankUpgradesInventory || e.getInventory().getHolder() instanceof PersonalBankInventory || e.getInventory().getHolder() instanceof DepositInventory || e.getInventory().getHolder() instanceof WithdrawInventory) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (database) {
            if (getDatabaseInterest(player.getUniqueId()) != null) {
                GiveInterest(player);
            }
        } else {
            if (getYamlInterest(player.getUniqueId()) != null) {
                GiveInterest(player);
            }
        }
    }
}