package com.mortisdevelopment.mortisbank.accounts;

import com.mortisdevelopment.mortiscore.databases.Database;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class AccountManager {

    private final JavaPlugin plugin;
    private final Database database;
    private final AccountSettings settings;
    private final HashMap<UUID, Short> accountPriorityByPlayer = new HashMap<>();
    private final HashMap<String, Account> accountById = new HashMap<>();

    public AccountManager(JavaPlugin plugin, Database database, @NotNull AccountSettings settings) {
        this.plugin = plugin;
        this.database = database;
        this.settings = settings;
        Bukkit.getServer().getPluginManager().registerEvents(new AccountListener(this), plugin);
        initialize();
    }

    private void initialize() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            database.execute("CREATE TABLE IF NOT EXISTS BankAccounts(uniqueId varchar(36) primary key, priority smallint)");
            ResultSet result = database.query("SELECT * FROM BankAccounts");
            try {
                while (result.next()) {
                    UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                    short priority = result.getShort("priority");
                    accountPriorityByPlayer.put(uniqueId, priority);
                }
            } catch (SQLException exp) {
                throw new RuntimeException(exp);
            }
        });
    }

    public void setAccount(@NotNull UUID uuid, short priority) {
        accountPriorityByPlayer.put(uuid, priority);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("UPDATE BankAccounts SET priority = ? WHERE uniqueId = ?", priority, uuid.toString()));
    }

    public short getAccount(@NotNull UUID uuid) {
        return accountPriorityByPlayer.get(uuid);
    }

    public Account getAccount(String id) {
        return accountById.get(id);
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accountById.values());
    }

    public Account getAccount(@NotNull OfflinePlayer player) {
        short accountPriority = getAccount(player.getUniqueId());
        Account account = getAccount(accountPriority);
        if (account != null) {
            return account;
        }
        account = getPreviousAccount(accountPriority);
        if (account != null) {
            setAccount(player.getUniqueId(), account.getPriority());
            return account;
        }
        account = getDefaultAccount();
        if (account != null) {
            setAccount(player.getUniqueId(), account.getPriority());
            return account;
        }
        return null;
    }

    public Account getAccount(short priority) {
        for (Account account : getAccounts()) {
            if (account.isPriority(priority)) {
                return account;
            }
        }
        return null;
    }

    public Account getPreviousAccount(short priority) {
        Account newAccount = null;
        for (Account account : getAccounts()) {
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
        for (Account account : getAccounts()) {
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
        for (Account account : getAccounts()) {
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
        setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean upgradeAccount(@NotNull OfflinePlayer player) {
        short priority = getAccount(player.getUniqueId());
        Account account = getNextAccount(priority);
        if (account == null) {
            return false;
        }
        setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean downgradeAccount(@NotNull OfflinePlayer player) {
        short priority = getAccount(player.getUniqueId());
        Account account = getPreviousAccount(priority);
        if (account == null) {
            return false;
        }
        setAccount(player.getUniqueId(), account.getPriority());
        return true;
    }
}