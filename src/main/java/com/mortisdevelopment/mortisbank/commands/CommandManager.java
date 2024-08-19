package com.mortisdevelopment.mortisbank.commands;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.personal.TransactionType;
import com.mortisdevelopment.mortiscore.managers.Manager;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager extends Manager {

    private final MortisBank plugin;

    public CommandManager(MortisBank plugin) {
        this.plugin = plugin;
    }

    public boolean setBalance(@NotNull OfflinePlayer player, double amount) {
        Account account = plugin.getAccountManager().getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return false;
        }
        if (account.isStorable(amount)) {
            plugin.getDataManager().setBalance(player.getUniqueId(), amount);
        }else {
            plugin.getDataManager().setBalance(player.getUniqueId(), account.getSpace(balance));
        }
        return true;
    }

    public boolean addBalance(@NotNull OfflinePlayer player, double amount) {
        Account account = plugin.getAccountManager().getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return false;
        }
        amount += balance;
        if (account.isStorable(amount)) {
            plugin.getDataManager().setBalance(player.getUniqueId(), amount);
        }else {
            plugin.getDataManager().setBalance(player.getUniqueId(), account.getSpace(balance));
        }
        return true;
    }

    public boolean removeBalance(@NotNull OfflinePlayer player, double amount) {
        Account account = plugin.getAccountManager().getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        if (balance <= 0) {
            return false;
        }
        balance -= amount;
        if (balance > 0) {
            plugin.getDataManager().setBalance(player.getUniqueId(), balance);
        }else {
            plugin.getDataManager().setBalance(player.getUniqueId(), 0);
        }
        return true;
    }
    
    public boolean setAccount(@NotNull OfflinePlayer player, short priority) {
        Account account = plugin.getAccountManager().getAccount(priority);
        if (account == null) {
            account = plugin.getAccountManager().getPreviousAccount(priority);
            if (account == null) {
                return false;
            }
        }
        plugin.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }
    
    public boolean upgradeAccount(@NotNull OfflinePlayer player) {
        short priority = plugin.getDataManager().getAccount(player.getUniqueId());
        Account account = plugin.getAccountManager().getNextAccount(priority);
        if (account == null) {
            return false;
        }
        plugin.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }
    
    public boolean downgradeAccount(@NotNull OfflinePlayer player) {
        short priority = plugin.getDataManager().getAccount(player.getUniqueId());
        Account account = plugin.getAccountManager().getPreviousAccount(priority);
        if (account == null) {
            return false;
        }
        plugin.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull TransactionType type, @NotNull String amount, @NotNull String transactor) {
        PersonalTransaction transaction = new PersonalTransaction(type, amount, transactor);
        if (!transaction.isValid()) {
            return false;
        }
        plugin.getDataManager().addTransaction(offlinePlayer.getUniqueId(), transaction);
        return true;
    }

    public boolean clearTransaction(@NotNull OfflinePlayer offlinePlayer, int position) {
        List<PersonalTransaction> transactions = plugin.getDataManager().getTransactions(offlinePlayer.getUniqueId());
        if (transactions == null || position >= transactions.size()) {
            return false;
        }
        transactions.remove(position);
        plugin.getDataManager().setTransactions(offlinePlayer.getUniqueId(), transactions);
        return true;
    }

    public boolean clearTransactions(@NotNull OfflinePlayer offlinePlayer) {
        plugin.getDataManager().setTransactions(offlinePlayer.getUniqueId(), new ArrayList<>());
        return true;
    }
}
