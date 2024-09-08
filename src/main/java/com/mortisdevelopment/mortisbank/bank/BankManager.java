package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.transactions.Transaction;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.placeholder.methods.PlayerPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

@Getter
public class BankManager {

    public enum DepositType {
        ALL,
        HALF,
        SPECIFIC
    }
    public enum WithdrawalType {
        ALL,
        HALF,
        TWENTY,
        SPECIFIC
    }
    private final AccountManager accountManager;
    private final DataManager dataManager;
    private final Economy economy;
    private final CustomMenu personalMenu;
    private final GuiSettings guiSettings;
    private final Messages depositMessages;
    private final Messages withdrawalMessages;

    public BankManager(AccountManager accountManager, DataManager dataManager, Economy economy, CustomMenu personalMenu, GuiSettings guiSettings, MessageManager messageManager) {
        this.accountManager = accountManager;
        this.dataManager = dataManager;
        this.economy = economy;
        this.personalMenu = personalMenu;
        this.guiSettings = guiSettings;
        this.depositMessages = messageManager.getMessages("deposit_messages");
        this.withdrawalMessages = messageManager.getMessages("withdrawal_messages");
    }

    private void addTransaction(@NotNull OfflinePlayer offlinePlayer, double amount, Transaction.TransactionType type) {
        String transactor = Objects.requireNonNullElse(offlinePlayer.getName(), "Unknown");
        Transaction transaction = new Transaction(type, NumberUtils.format(amount), transactor);
        dataManager.addTransaction(offlinePlayer.getUniqueId(), transaction);
    }

    private Placeholder getTransactionPlaceholder(double purse) {
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%amount%", NumberUtils.format(purse));
        method.addReplacement("%amount_raw%", String.valueOf(purse));
        method.addReplacement("%amount_formatted%", NumberUtils.getMoney(purse));
        return new Placeholder(method);
    }

    public Messages getMessages(Transaction.TransactionType type) {
        return switch (type) {
            case DEPOSIT -> depositMessages;
            case WITHDRAW -> withdrawalMessages;
        };
    }

    private void sendTransactionMessage(Player player, double purse, Transaction.TransactionType type) {
        if (player == null) {
            return;
        }
        Placeholder placeholder = getTransactionPlaceholder(purse);
        placeholder.addMethod(new PlayerPlaceholderMethod(player));
        getMessages(type).sendPlaceholderMessage(player, type.name().toLowerCase(Locale.ROOT), placeholder);
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, @NotNull DepositType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(DepositType.SPECIFIC)) {
            guiSettings.open(this, player, Transaction.TransactionType.DEPOSIT, new Placeholder(player));
            return true;
        }
        double purse = economy.getBalance(offlinePlayer);
        if (type.equals(DepositType.HALF)) {
            purse = purse / 2;
        }
        return deposit(purse, offlinePlayer, purse);
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, double amount) {
        return deposit(economy.getBalance(offlinePlayer), offlinePlayer, amount);
    }

    private boolean deposit(double purse, OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        if (amount <= 0) {
            depositMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        amount = Math.min(amount, purse);
        if (amount <= 0) {
            depositMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            depositMessages.sendPlaceholderMessage(player, "full", new Placeholder(player));
            return false;
        }
        double newBalance = balance + amount;
        if (!account.isStorable(newBalance)) {
            amount = account.getSpace(balance);
            newBalance = balance + amount;
        }
        economy.withdrawPlayer(offlinePlayer, amount);
        dataManager.setBalance(offlinePlayer.getUniqueId(), newBalance);
        addTransaction(offlinePlayer, purse, Transaction.TransactionType.DEPOSIT);
        sendTransactionMessage(player, purse, Transaction.TransactionType.DEPOSIT);
        return true;
    }

    public double getDepositWhole(@NotNull OfflinePlayer offlinePlayer) {
        return getDeposit(economy.getBalance(offlinePlayer), offlinePlayer);
    }

    public double getDepositHalf(@NotNull OfflinePlayer offlinePlayer) {
        return getDeposit(economy.getBalance(offlinePlayer) / 2, offlinePlayer);
    }

    private double getDeposit(double purse, OfflinePlayer offlinePlayer) {
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return 0;
        }
        if (purse <= 0) {
            return 0;
        }
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            return 0;
        }
        return Math.min(purse, account.getSpace(balance));
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, @NotNull WithdrawalType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(WithdrawalType.SPECIFIC)) {
            return guiSettings.open(this, player, Transaction.TransactionType.WITHDRAW, new Placeholder(player));
        }
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (balance <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        double amount = 0;
        switch (type) {
            case ALL -> amount = balance;
            case HALF -> amount = balance / 2;
            case TWENTY -> amount = balance * 0.20;
        }
        return withdraw(balance, offlinePlayer, amount);
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (balance <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        return withdraw(balance, offlinePlayer, amount);
    }

    private boolean withdraw(double balance, OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        if (amount <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        if (amount > balance) {
            withdrawalMessages.sendPlaceholderMessage(player, "no_money", new Placeholder(player));
            return false;
        }
        economy.depositPlayer(offlinePlayer, amount);
        dataManager.setBalance(offlinePlayer.getUniqueId(), balance - amount);
        addTransaction(offlinePlayer, amount, Transaction.TransactionType.WITHDRAW);
        sendTransactionMessage(player, amount, Transaction.TransactionType.WITHDRAW);
        return true;
    }

    public double getWithdrawEverything(@NotNull OfflinePlayer offlinePlayer) {
        return dataManager.getBalance(offlinePlayer.getUniqueId());
    }

    public double getWithdrawHalf(@NotNull OfflinePlayer offlinePlayer) {
        return dataManager.getBalance(offlinePlayer.getUniqueId()) / 2;
    }

    public double getWithdrawTwentyPercent(@NotNull OfflinePlayer offlinePlayer) {
        return dataManager.getBalance(offlinePlayer.getUniqueId()) * 0.20;
    }

    private void setBalance(Account account, OfflinePlayer offlinePlayer, double amount) {
        dataManager.setBalance(offlinePlayer.getUniqueId(), Math.min(amount, account.getMaxBalance()));
    }

    public boolean setBalance(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        setBalance(account, offlinePlayer, amount);
        return true;
    }

    public boolean addBalance(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            return false;
        }
        setBalance(account, offlinePlayer, balance + amount);
        return true;
    }

    public boolean removeBalance(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (balance <= 0) {
            return false;
        }
        setBalance(account, offlinePlayer, balance - amount);
        return true;
    }
}