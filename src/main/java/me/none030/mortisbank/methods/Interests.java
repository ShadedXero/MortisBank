package me.none030.mortisbank.methods;

import me.none030.mortisbank.utils.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.none030.mortisbank.MortisBank.plugin;
import static me.none030.mortisbank.methods.DataMethods.*;
import static me.none030.mortisbank.methods.StoringMessages.InterestMessage;
import static me.none030.mortisbank.methods.TransactionsRecords.*;

public class Interests {

    public static void GiveInterest(Player player) {

        DecimalFormat value = new DecimalFormat("#,###.#");
        double bankBalance = getBankBalance(player.getUniqueId());
        int bankAccount = getBankAccount(player.getUniqueId());
        LocalDateTime time = getBankInterestData(player.getUniqueId());
        boolean isAbove = isAboveTime(time, LocalDateTime.now());
        if (isAbove) {
            double amount = getInterest(bankBalance, bankAccount);
            if (amount != -1) {
                ChangeBankBalance(player.getUniqueId(), bankBalance + amount);
                ChangeBankInterest(player.getUniqueId(), LocalDateTime.now());
                StoreTransaction(TransactionType.DEPOSIT, amount, player, "Bank Interest");
                player.sendMessage(InterestMessage.replace("%money%", value.format(amount)));
            }
        }
    }

    public static void CheckInterests() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    LocalDateTime time = getBankInterestData(player.getUniqueId());

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

        File file = new File("plugins/MortisBank/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.getBoolean("config.interests.enabled")) {
            return 0;
        }
        LocalDateTime time = getBankInterestData(player.getUniqueId());

        if (time != null) {
            return getInterestDuration(time, LocalDateTime.now());
        }

        return 0;
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
                return Math.min(amount, maxInterest);
            }
        }

        return -1;
    }
}
