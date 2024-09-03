package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.BalanceActionCommand;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.personal.TransactionType;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.placeholder.methods.PlayerPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

public class BankManager {

    private final AccountManager accountManager;
    private final DataManager dataManager;
    private final Economy economy;
    private final GuiSettings guiSettings;
    private final Messages depositMessages;
    private final Messages withdrawalMessages;

    public BankManager(AccountManager accountManager, DataManager dataManager, Economy economy, GuiSettings guiSettings, MessageManager messageManager) {
        this.accountManager = accountManager;
        this.dataManager = dataManager;
        this.economy = economy;
        this.guiSettings = guiSettings;
        this.depositMessages = messageManager.getMessages("deposit_messages");
        this.withdrawalMessages = messageManager.getMessages("withdrawal_messages");
    }

    private void addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull String amount, TransactionType type) {
        String transactor = Objects.requireNonNullElse(offlinePlayer.getName(), "Unknown");
        PersonalTransaction transaction = new PersonalTransaction(type, amount, transactor);
        dataManager.addTransaction(offlinePlayer.getUniqueId(), transaction);
    }

    private Placeholder getTransactionPlaceholder(double purse) {
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%amount%", NumberUtils.format(purse));
        method.addReplacement("%amount_raw%", String.valueOf(purse));
        method.addReplacement("%amount_formatted%", NumberUtils.getMoney(purse));
        return new Placeholder(method);
    }

    public Messages getMessages(TransactionType type) {
        return switch (type) {
            case DEPOSIT -> depositMessages;
            case WITHDRAW -> withdrawalMessages;
        };
    }

    private void sendTransactionMessage(Player player, double purse, TransactionType type) {
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
            if (player == null) {
                return false;
            }
            guiSettings.open(this, player, TransactionType.DEPOSIT, new Placeholder(player));
            return true;
        }
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            depositMessages.sendPlaceholderMessage(player, "full", new Placeholder(player));
            return false;
        }
        double purse = economy.getBalance(offlinePlayer);
        if (type.equals(DepositType.HALF)) {
            purse = purse / 2;
        }
        if (purse <= 0) {
            depositMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        double bank = balance + purse;
        if (!account.isStorable(bank)) {
            purse = account.getSpace(balance);
            bank = balance + purse;
        }
        economy.withdrawPlayer(offlinePlayer, purse);
        dataManager.setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(purse), TransactionType.DEPOSIT);
        sendTransactionMessage(player, purse, TransactionType.DEPOSIT);
        return true;
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (amount <= 0) {
            depositMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
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
        double purse = economy.getBalance(offlinePlayer);
        if (amount < purse) {
            purse = amount;
        }
        if (purse <= 0) {
            depositMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        double bank = balance + purse;
        if (!account.isStorable(bank)) {
            purse = account.getSpace(balance);
            bank = balance + purse;
        }
        economy.withdrawPlayer(offlinePlayer, purse);
        dataManager.setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(purse), TransactionType.DEPOSIT);
        sendTransactionMessage(player, purse, TransactionType.DEPOSIT);
        return true;
    }

    public double getDepositWhole(@NotNull OfflinePlayer player) {
        Account account = accountManager.getAccount(player);
        if (account == null) {
            return 0;
        }
        double purse = economy.getBalance(player);
        if (purse == 0) {
            return 0;
        }
        double balance = dataManager.getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public double getDepositWhole(@NotNull Account account, double balance, double purse) {
        if (purse == 0 || account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public double getDepositHalf(@NotNull OfflinePlayer player) {
        Account account = accountManager.getAccount(player);
        if (account == null) {
            return 0;
        }
        double purse = economy.getBalance(player) / 2;
        if (purse <= 0) {
            return 0;
        }
        double balance = dataManager.getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public double getDepositHalf(@NotNull Account account, double balance, double purse) {
        purse = purse / 2;
        if (purse == 0 || account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, @NotNull WithdrawalType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(WithdrawalType.SPECIFIC)) {
            if (player == null) {
                return false;
            }
            guiSettings.open(this, player, TransactionType.WITHDRAW, new Placeholder(player));
            return true;
        }
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        double bank = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (bank <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        double purse = 0;
        if (type.equals(WithdrawalType.ALL)) {
            purse = bank;
            bank = 0;
        }
        if (type.equals(WithdrawalType.HALF)) {
            purse = bank / 2;
            bank /= 2;
        }
        if (type.equals(WithdrawalType.TWENTY)) {
            purse = bank * 0.20;
            bank *= 0.80;
        }
        economy.depositPlayer(offlinePlayer, purse);
        dataManager.setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(purse), TransactionType.WITHDRAW);
        sendTransactionMessage(player, purse, TransactionType.WITHDRAW);
        return true;
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (amount <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        double bank = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (bank <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        if (amount > bank) {
            withdrawalMessages.sendPlaceholderMessage(player, "no_money", new Placeholder(player));
            return false;
        }
        bank -= amount;
        economy.depositPlayer(offlinePlayer, amount);
        dataManager.setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(amount), TransactionType.WITHDRAW);
        sendTransactionMessage(player, amount, TransactionType.WITHDRAW);
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

    public boolean setBalance(OfflinePlayer offlinePlayer, double amount, BalanceActionCommand.BalanceAction action) {
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = accountManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (action.equals(BalanceActionCommand.BalanceAction.REMOVE)) {
            if (balance <= 0) {
                return false;
            }
        }else {
            if (account.isFull(balance)) {
                return false;
            }
        }
        switch (action) {
            case ADD -> balance += amount;
            case REMOVE -> balance -= amount;
        }
        if (account.isStorable(balance)) {
            accountManager.getDataManager().setBalance(offlinePlayer.getUniqueId(), balance);
        }else {
            accountManager.getDataManager().setBalance(offlinePlayer.getUniqueId(), account.getSpace(balance));
        }
    }

    public boolean removeBalance() {

    }

    public boolean addBalance() {

    }
}