package me.none030.mortisbank.methods;

import me.none030.mortisbank.utils.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.MortisBank.plugin;
import static me.none030.mortisbank.methods.StoringDatabaseData.*;
import static me.none030.mortisbank.methods.StoringYAMLData.*;
import static me.none030.mortisbank.methods.TransactionsRecords.*;

public class Interests {

    public static void GiveInterest(Player player) {

        double bankBalance;
        int bankAccount;
        LocalDateTime time;
        if (database) {
            bankBalance = getDatabaseBalance(player.getUniqueId());
            bankAccount = getDatabaseAccount(player.getUniqueId());
            time = getDatabaseInterest(player.getUniqueId());
        } else {
            bankBalance = getYamlBalance(player.getUniqueId());
            bankAccount = getYamlAccount(player.getUniqueId());
            time = getYamlInterest(player.getUniqueId());
        }
        boolean isAbove = isAboveTime(time, LocalDateTime.now());
        if (isAbove) {
            double amount = getInterest(bankBalance, bankAccount);
            if (amount != -1) {
                if (database) {
                    ChangeDatabaseBalance(player.getUniqueId(), bankBalance + amount);
                    ChangeDatabaseInterest(player.getUniqueId(), LocalDateTime.now());
                } else {
                    ChangeYamlBalance(player.getUniqueId(), bankBalance + amount);
                    ChangeYamlInterest(player.getUniqueId(), LocalDateTime.now());
                }
                StoreTransaction(TransactionType.DEPOSIT, amount, player, "Bank Interest");
            }
        }
    }

    public static void CheckInterests() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    LocalDateTime time;
                    if (database) {
                        time = getDatabaseInterest(player.getUniqueId());
                    } else {
                        time = getYamlInterest(player.getUniqueId());
                    }

                    if (time != null) {
                        if (isAboveTime(time, LocalDateTime.now())) {
                            GiveInterest(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 500L, 500L);
    }

    public static long getInterestTime(Player player) {

        LocalDateTime time;
        if (database) {
            time = getDatabaseInterest(player.getUniqueId());
        } else {
            time = getYamlInterest(player.getUniqueId());
        }

        if (time != null) {
            return getInterestDuration(time, LocalDateTime.now());
        }

        return 31;
    }



    public static double getInterest(double bankBalance, int bankAccount) {

        File file = new File("plugins/MortisBank/", "upgrades.yml");
        FileConfiguration upgrades = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section;
        if (bankAccount > 0) {
            section = upgrades.getConfigurationSection("bank-upgrades." + bankAccount + ".interests");
        } else {
            section = upgrades.getConfigurationSection("bank-upgrades.DEFAULT.interests");
        }
        assert section != null;

        double maxInterest = section.getDouble("max-interest");
        List<String> keys = new ArrayList<>(section.getKeys(false));
        keys.remove("max-interest");
        Collections.reverse(keys);

        for (String key : keys) {
            double min = section.getDouble(key + ".from");
            double max = section.getDouble(key + ".to");
            double percent = section.getDouble(key + ".percent");

            if (bankBalance > min && bankBalance < max) {
                double amount = bankBalance / 100 * percent;
                if (amount > maxInterest) {
                    return maxInterest;
                } else {
                    return amount;
                }
            }
        }

        return -1;
    }
}
