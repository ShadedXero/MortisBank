package com.mortisdevelopment.mortisbank.accounts;

import com.mortisdevelopment.mortisbank.data.DataManager;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AccountManager {
    
    private final DataManager dataManager;
    private final AccountSettings settings;
    private final List<Account> accounts = new ArrayList<>();

    public AccountManager(DataManager dataManager, @NotNull AccountSettings settings) {
        this.dataManager = dataManager;
        this.settings = settings;
    }

    public Account getAccount(@NotNull OfflinePlayer player) {
        short accountPriority = dataManager.getAccount(player.getUniqueId());
        Account account = getAccount(accountPriority);
        if (account != null) {
            return account;
        }
        account = getPreviousAccount(accountPriority);
        if (account != null) {
            dataManager.setAccount(player.getUniqueId(), account.getPriority());
            return account;
        }
        account = getDefaultAccount();
        if (account != null) {
            dataManager.setAccount(player.getUniqueId(), account.getPriority());
            return account;
        }
        return null;
    }

    public Account getAccount(short priority) {
        for (Account account : accounts) {
            if (account.isPriority(priority)) {
                return account;
            }
        }
        return null;
    }

    public Account getPreviousAccount(short priority) {
        Account newAccount = null;
        for (Account account : accounts) {
            short accountPriority = account.getPriority();
            if (accountPriority >= priority) {
                continue;
            }
            if (newAccount != null) {
                if (accountPriority > newAccount.getPriority()) {
                    newAccount = account;
                }
            } else {
                newAccount = account;
            }
        }
        return newAccount;
    }

    public Account getNextAccount(short priority) {
        Account newAccount = null;
        for (Account account : accounts) {
            short accountPriority = account.getPriority();
            if (accountPriority <= priority) {
                continue;
            }
            if (newAccount != null) {
                if (accountPriority < newAccount.getPriority()) {
                    newAccount = account;
                }
            } else {
                newAccount = account;
            }
        }
        return newAccount;
    }

    public Account getDefaultAccount() {
        Account defaultAccount = getAccount(settings.getDefaultAccount());
        if (defaultAccount != null) {
            return defaultAccount;
        }
        return getFirstAccount();
    }

    public Account getFirstAccount() {
        Account firstAccount = null;
        for (Account account : accounts) {
            if (firstAccount == null) {
                firstAccount = account;
                continue;
            }
            if (account.getPriority() < firstAccount.getPriority()) {
                firstAccount = account;
            }
        }
        return firstAccount;
    }

    public boolean setAccount(@NotNull OfflinePlayer player, short priority) {
        Account account = getAccount(priority);
        if (account == null) {
            account = getPreviousAccount(priority);
            if (account == null) {
                return false;
            }
        }
        dataManager.setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean upgradeAccount(@NotNull OfflinePlayer player) {
        short priority = dataManager.getAccount(player.getUniqueId());
        Account account = getNextAccount(priority);
        if (account == null) {
            return false;
        }
        dataManager.setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean downgradeAccount(@NotNull OfflinePlayer player) {
        short priority = dataManager.getAccount(player.getUniqueId());
        Account account = getPreviousAccount(priority);
        if (account == null) {
            return false;
        }
        dataManager.setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }
}