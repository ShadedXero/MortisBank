package me.none030.mortisbank.api;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.MortisBank.getEconomy;
import static me.none030.mortisbank.methods.StoringDatabaseData.*;
import static me.none030.mortisbank.methods.StoringYAMLData.*;

@SuppressWarnings("deprecation")
public abstract class AbstractMortisBankAPI implements MortisBankAPI{

    public double getBalance(UUID player) {
        if (database) {
            return getDatabaseBalance(player);
        } else {
            return getYamlBalance(player);
        }
    }

    public int getAccount(UUID player) {
        if (database) {
            return getDatabaseAccount(player);
        } else {
            return getYamlAccount(player);
        }
    }

    public double getPurse(UUID player) {
        Economy economy = getEconomy();
        Player target = Bukkit.getPlayer(player);
        return economy.getBalance(target.getName());
    }

    public LocalDateTime getInterestData(UUID player) {
        if (database) {
            return getDatabaseInterest(player);
        } else {
            return getYamlInterest(player);
        }
    }

    public void StoreData(UUID player, double bankBalance, int bankAccount) {
        if (database) {
            StoreDatabaseData(player, bankBalance, bankAccount);
        } else {
            StoreYamlData(player, bankBalance, bankAccount);
        }
    }

    public void ChangeBalance(UUID player, double bankBalance) {
        if (database) {
            ChangeDatabaseBalance(player, bankBalance);
        } else {
            ChangeYamlBalance(player, bankBalance);
        }
    }

    public void ChangeAccount(UUID player, int bankAccount) {
        if (database) {
            ChangeDatabaseAccount(player, bankAccount);
        } else {
            ChangeYamlAccount(player, bankAccount);
        }
    }

    public void ChangeInterest(UUID player, LocalDateTime time) {
        if (database) {
            ChangeDatabaseInterest(player, time);
        } else {
            ChangeYamlInterest(player, time);
        }
    }

    public void GiveInterestMoney(String player, double amount) {
        Economy economy = getEconomy();
        economy.depositPlayer(player, amount);
    }
}
