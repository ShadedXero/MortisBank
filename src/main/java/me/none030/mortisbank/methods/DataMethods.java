package me.none030.mortisbank.methods;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.MortisBank.getEconomy;
import static me.none030.mortisbank.methods.StoringDatabaseData.*;
import static me.none030.mortisbank.methods.StoringYAMLData.*;

public class DataMethods {

    public static double getBankBalance(UUID player) {
        if (database) {
            return getDatabaseBalance(player);
        } else {
            return getYamlBalance(player);
        }
    }

    public static int getBankAccount(UUID player) {
        if (database) {
            return getDatabaseAccount(player);
        } else {
            return getYamlAccount(player);
        }
    }

    public static double getBankPurse(UUID player) {
        Economy economy = getEconomy();
        Player target = Bukkit.getPlayer(player);
        return economy.getBalance(target.getName());
    }

    public static LocalDateTime getBankInterestData(UUID player) {
        if (database) {
            return getDatabaseInterest(player);
        } else {
            return getYamlInterest(player);
        }
    }

    public static void StoreBankData(UUID player, double bankBalance, int bankAccount) {
        if (database) {
            StoreDatabaseData(player, bankBalance, bankAccount);
        } else {
            StoreYamlData(player, bankBalance, bankAccount);
        }
    }

    public static void ChangeBankBalance(UUID player, double bankBalance) {
        if (database) {
            ChangeDatabaseBalance(player, bankBalance);
        } else {
            ChangeYamlBalance(player, bankBalance);
        }
    }

    public static void ChangeBankAccount(UUID player, int bankAccount) {
        if (database) {
            ChangeDatabaseAccount(player, bankAccount);
        } else {
            ChangeYamlAccount(player, bankAccount);
        }
    }

    public static void ChangeBankInterest(UUID player, LocalDateTime time) {
        if (database) {
            ChangeDatabaseInterest(player, time);
        } else {
            ChangeYamlInterest(player, time);
        }
    }

    public static void GiveBankInterestMoney(String player, double amount) {
        Economy economy = getEconomy();
        economy.depositPlayer(player, amount);
    }

}
