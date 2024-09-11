package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.accounts.AccountSettings;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.BankSettings;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortisbank.transactions.TransactionSettings;
import com.mortisdevelopment.mortiscore.configs.ConfigManager;
import com.mortisdevelopment.mortiscore.databases.Database;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.items.CustomItem;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.utils.ColorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class BankConfigManager extends ConfigManager {

    private final MortisBank plugin;

    public BankConfigManager(MortisBank plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    @Override
    public void loadConfigs() {
        new MessageConfig(plugin);

        File configFile = getFile(plugin, "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        Database database = plugin.getCore().getDatabaseManager().getDatabase(plugin, config.getConfigurationSection("database"));
        plugin.setDatabase(database);

        File accountsFile = getFile(plugin, "accounts.yml");
        FileConfiguration accounts = YamlConfiguration.loadConfiguration(accountsFile);

        AccountManager accountManager = new AccountManager(plugin, database, getSettings(accounts));
        plugin.setAccountManager(accountManager);
        loadAccounts(config.getConfigurationSection("accounts"));

        TransactionManager transactionManager = new TransactionManager(plugin, database, getTransactionSettings(config), plugin.getMessageManager().getMessages("transaction_messages"));
        plugin.setTransactionManager(transactionManager);

        try {
            CustomMenu personalMenu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("personal-menu"));
            BankManager bankManager = new BankManager(plugin, plugin.getAccountManager(), plugin.getTransactionManager(), database, plugin.getEconomy(), getBankSettings(config), personalMenu, plugin.getMessageManager());
            plugin.setBankManager(bankManager);
        } catch (ConfigException e) {
            e.setFile(configFile);
            e.setPath(config);
            throw new RuntimeException(e);
        }
    }

    private TransactionSettings getTransactionSettings(ConfigurationSection section) {
        return new TransactionSettings(section.getInt("transaction-limit"));
    }

    private BankSettings getBankSettings(ConfigurationSection section) throws ConfigException {
        boolean leaderboard = section.getBoolean("leaderboard");
        BankSettings.InputMode mode;
        try {
            mode = BankSettings.InputMode.valueOf(section.getString("input-mode"));
        }catch (IllegalArgumentException exp) {
            throw new ConfigException(section);
        }
        int inputSlot = section.getInt("sign-input-slot");
        CustomItem customItem = plugin.getCore().getItemManager().getObject(plugin, section.getConfigurationSection("anvil-item"));
        return new BankSettings(leaderboard, mode, inputSlot, customItem);
    }

    private AccountSettings getSettings(ConfigurationSection section) {
        return new AccountSettings((short) section.getInt("default-account"));
    }

    private void loadAccounts(ConfigurationSection accounts) {
        if (accounts == null) {
            return;
        }
        for (String id : accounts.getKeys(false)) {
            ConfigurationSection section = accounts.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            short priority = (short) section.getInt("priority");
            String name = Objects.requireNonNull(ColorUtils.color(section.getString("name")));
            double maxBalance = section.getDouble("max-balance");
            Account account = new Account(id, priority, name, maxBalance);
            plugin.getAccountManager().getAccounts().add(account);
        }
    }
}
