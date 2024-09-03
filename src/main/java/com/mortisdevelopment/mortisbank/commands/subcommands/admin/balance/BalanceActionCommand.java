package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class BalanceActionCommand extends PermissionCommand {

    public enum BalanceAction {SET,ADD,REMOVE}
    private final AccountManager accountManager;

    public BalanceActionCommand(String name, String permission, Messages messages, AccountManager accountManager) {
        super(name, permission, messages);
        this.accountManager = accountManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return List.of();
    }

    public boolean setBalance(OfflinePlayer offlinePlayer, double amount, BalanceAction action) {
        Account account = accountManager.getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = accountManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (action.equals(BalanceAction.REMOVE)) {
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
}
