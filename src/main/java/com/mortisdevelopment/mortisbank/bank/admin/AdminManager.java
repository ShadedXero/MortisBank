package com.mortisdevelopment.mortisbank.bank.admin;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortisbank.bank.accounts.Account;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.bank.personal.TransactionType;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminManager extends Manager {

    private final BankManager bankManager;

    public AdminManager(@NotNull BankManager bankManager) {
        this.bankManager = bankManager;
        MortisBank plugin = MortisBank.getInstance();
        plugin.getCommand("bank").setExecutor(new BankCommand(this));
    }

    public boolean setBalance(@NotNull OfflinePlayer player, double amount) {
        Account account = bankManager.getAccountManager().getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = bankManager.getDataManager().getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return false;
        }
        if (account.isStorable(amount)) {
            bankManager.getDataManager().setBalance(player.getUniqueId(), amount);
        }else {
            bankManager.getDataManager().setBalance(player.getUniqueId(), account.getSpace(balance));
        }
        return true;
    }

    public boolean addBalance(@NotNull OfflinePlayer player, double amount) {
        Account account = bankManager.getAccountManager().getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = bankManager.getDataManager().getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return false;
        }
        amount += balance;
        if (account.isStorable(amount)) {
            bankManager.getDataManager().setBalance(player.getUniqueId(), amount);
        }else {
            bankManager.getDataManager().setBalance(player.getUniqueId(), account.getSpace(balance));
        }
        return true;
    }

    public boolean removeBalance(@NotNull OfflinePlayer player, double amount) {
        Account account = bankManager.getAccountManager().getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = bankManager.getDataManager().getBalance(player.getUniqueId());
        if (balance <= 0) {
            return false;
        }
        balance -= amount;
        if (balance > 0) {
            bankManager.getDataManager().setBalance(player.getUniqueId(), balance);
        }else {
            bankManager.getDataManager().setBalance(player.getUniqueId(), 0);
        }
        return true;
    }
    
    public boolean setAccount(@NotNull OfflinePlayer player, short priority) {
        Account account = bankManager.getAccountManager().getAccount(priority);
        if (account == null) {
            account = bankManager.getAccountManager().getPreviousAccount(priority);
            if (account == null) {
                return false;
            }
        }
        bankManager.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }
    
    public boolean upgradeAccount(@NotNull OfflinePlayer player) {
        short priority = bankManager.getDataManager().getAccount(player.getUniqueId());
        Account account = bankManager.getAccountManager().getNextAccount(priority);
        if (account == null) {
            return false;
        }
        bankManager.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }
    
    public boolean downgradeAccount(@NotNull OfflinePlayer player) {
        short priority = bankManager.getDataManager().getAccount(player.getUniqueId());
        Account account = bankManager.getAccountManager().getPreviousAccount(priority);
        if (account == null) {
            return false;
        }
        bankManager.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull TransactionType type, @NotNull String amount, @NotNull String transactor) {
        PersonalTransaction transaction = new PersonalTransaction(type, amount, transactor);
        if (!transaction.isValid()) {
            return false;
        }
        bankManager.getDataManager().addTransaction(offlinePlayer.getUniqueId(), transaction);
        return true;
    }

    public boolean clearTransaction(@NotNull OfflinePlayer offlinePlayer, int position) {
        List<PersonalTransaction> transactions = bankManager.getDataManager().getTransactions(offlinePlayer.getUniqueId());
        if (transactions == null || position >= transactions.size()) {
            return false;
        }
        transactions.remove(position);
        bankManager.getDataManager().setTransactions(offlinePlayer.getUniqueId(), transactions);
        return true;
    }

    public boolean clearTransactions(@NotNull OfflinePlayer offlinePlayer) {
        bankManager.getDataManager().setTransactions(offlinePlayer.getUniqueId(), new ArrayList<>());
        return true;
    }

    public BankManager getBankManager() {
        return bankManager;
    }
}
