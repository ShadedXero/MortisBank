package com.mortisdevelopment.mortisbank.bank.accounts;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortiscorespigot.menus.Menu;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AccountManager extends Manager {

    private final MortisBank plugin = MortisBank.getInstance();
    private final BankManager bankManager;
    private final Menu menu;
    private final AccountSettings settings;
    private final List<Account> accounts;

    public AccountManager(@NotNull BankManager bankManager, @NotNull Menu menu, @NotNull AccountSettings settings) {
        this.bankManager = bankManager;
        this.menu = menu;
        this.settings = settings;
        this.accounts = new ArrayList<>();
        plugin.getServer().getPluginManager().registerEvents(new AccountListener(), plugin);
    }

    public Account getAccount(@NotNull OfflinePlayer player) {
        short accountPriority = bankManager.getDataManager().getAccount(player.getUniqueId());
        Account account = getAccount(accountPriority);
        if (account != null) {
            return account;
        }
        account = getPreviousAccount(accountPriority);
        if (account != null) {
            bankManager.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
            return account;
        }
        account = getDefaultAccount();
        if (account != null) {
            bankManager.getDataManager().setAccount(player.getUniqueId(), account.getPriority());
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

    public Account getAccount(int slot) {
        for (Account account : accounts) {
            if (account.getIconSlot() == slot) {
                return account;
            }
        }
        return null;
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public Menu getMenu() {
        return menu;
    }

    public AccountSettings getSettings() {
        return settings;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
