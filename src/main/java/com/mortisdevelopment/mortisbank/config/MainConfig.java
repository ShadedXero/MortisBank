package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.bank.BankSettings;
import com.mortisdevelopment.mortiscore.configs.FileConfig;
import com.mortisdevelopment.mortiscore.databases.*;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.items.CustomItem;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MainConfig extends FileConfig {

    private final MortisBank plugin;

    public MainConfig(MortisBank plugin) {
        super("config.yml");
        this.plugin = plugin;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        File file = getFile(plugin);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Database database = plugin.getCore().getDatabaseManager().getDatabase(plugin, config.getConfigurationSection("database"));
        plugin.setDataManager(new DataManager(plugin, database, config.getBoolean("leaderboard"), config.getInt("transaction-limit")));
        BankSettings guiSettings = loadGuiSettings(config);
        if (guiSettings == null) {
            return;
        }
        try {
            CustomMenu personalMenu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("personal-menu"));
            plugin.setBankManager(new BankManager(plugin.getAccountManager(), plugin.getDataManager(), plugin.getEconomy(), personalMenu, guiSettings, plugin.getMessageManager()));
            plugin.setTransactionManager(new TransactionManager(plugin.getDataManager()));
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    private BankSettings loadGuiSettings(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        BankSettings.InputMode mode;
        try {
            mode = BankSettings.InputMode.valueOf(section.getString("input-mode"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        int inputSlot = section.getInt("sign-input-slot");
        CustomItem customItem;
        try {
            customItem = plugin.getCore().getItemManager().getObject(plugin, section.getConfigurationSection("anvil-item"));
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        return new BankSettings(plugin, mode, inputSlot, customItem);
    }
}
