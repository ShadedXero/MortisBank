package com.mortisdevelopment.mortisbank.accounts;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.databases.Database;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.utils.ColorUtils;
import com.mortisdevelopment.mortiscore.utils.ConfigUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class AccountManager extends Manager<MortisBank> {

    private final Database database;
    private AccountSettings settings;
    private final HashMap<UUID, Short> priorityByPlayer = new HashMap<>();
    private final HashMap<String, Account> accountById = new HashMap<>();
    private boolean initialized;

    public AccountManager(Database database) {
        this.database = database;
    }

    @Override
    public void reload(MortisBank plugin) {
        accountById.clear();
        File file = ConfigUtils.getFile(plugin, "accounts.yml");
        FileConfiguration accountsConfig = YamlConfiguration.loadConfiguration(file);
        this.settings = getSettings(accountsConfig);
        try {
            loadAccounts(ConfigException.requireNonNull(accountsConfig, accountsConfig.getConfigurationSection("accounts")));
        } catch (ConfigException e) {
            e.setFile(file);
            e.setPath(accountsConfig);
            throw new RuntimeException(e);
        }
        if (!initialized) {
            initialize(plugin);
            initialized = true;
        }
    }

    private AccountSettings getSettings(ConfigurationSection section) {
        return new AccountSettings((short) section.getInt("default-account"));
    }

    private void loadAccounts(ConfigurationSection accounts) throws ConfigException {
        for (String id : accounts.getKeys(false)) {
            ConfigurationSection section = ConfigException.requireNonNull(accounts, accounts.getConfigurationSection(id));
            short priority = (short) section.getInt("priority");
            String name = ColorUtils.color(ConfigException.requireNonNull(accounts, section.getString("name")));
            double maxBalance = section.getDouble("max-balance");
            Account account = new Account(id, priority, name, maxBalance);
            accountById.put(account.getId(), account);
        }
    }

    private void initialize(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            database.execute("CREATE TABLE IF NOT EXISTS BankAccounts(uniqueId varchar(36) primary key, priority smallint)");
            ResultSet result = database.query("SELECT * FROM BankAccounts");
            HashMap<UUID, Short> cache = new HashMap<>();
            try {
                while (result.next()) {
                    UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                    short priority = result.getShort("priority");
                    cache.put(uniqueId, priority);
                }
            } catch (SQLException exp) {
                throw new RuntimeException(exp);
            }
            Bukkit.getScheduler().runTask(plugin, () -> priorityByPlayer.putAll(cache));
        });
    }

    public void setAccount(JavaPlugin plugin, @NotNull UUID uuid, short priority) {
        boolean contains = priorityByPlayer.containsKey(uuid);
        priorityByPlayer.put(uuid, priority);
        if (contains) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("UPDATE BankAccounts SET priority = ? WHERE uniqueId = ?", priority, uuid.toString()));
        }else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("INSERT INTO BankAccounts(uniqueId, priority) VALUES (?, ?)", uuid.toString(), priority));
        }
    }

    public short getAccount(@NotNull UUID uuid) {
        return priorityByPlayer.get(uuid);
    }

    public Account getAccount(String id) {
        return accountById.get(id);
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accountById.values());
    }

    public Account getAccount(JavaPlugin plugin, @NotNull OfflinePlayer player) {
        short accountPriority = getAccount(player.getUniqueId());
        Account account = getAccount(accountPriority);
        if (account != null) {
            return account;
        }
        account = getPreviousAccount(accountPriority);
        if (account != null) {
            setAccount(plugin, player.getUniqueId(), account.getPriority());
            return account;
        }
        account = getDefaultAccount();
        if (account != null) {
            setAccount(plugin, player.getUniqueId(), account.getPriority());
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

    public boolean setAccount(JavaPlugin plugin, @NotNull OfflinePlayer player, short priority) {
        Account account = getAccount(priority);
        if (account == null) {
            account = getPreviousAccount(priority);
            if (account == null) {
                return false;
            }
        }
        setAccount(plugin, player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean upgradeAccount(JavaPlugin plugin, @NotNull OfflinePlayer player) {
        short priority = getAccount(player.getUniqueId());
        Account account = getNextAccount(priority);
        if (account == null) {
            return false;
        }
        setAccount(plugin, player.getUniqueId(), account.getPriority());
        return true;
    }

    public boolean downgradeAccount(JavaPlugin plugin, @NotNull OfflinePlayer player) {
        short priority = getAccount(player.getUniqueId());
        Account account = getPreviousAccount(priority);
        if (account == null) {
            return false;
        }
        setAccount(plugin, player.getUniqueId(), account.getPriority());
        return true;
    }
}