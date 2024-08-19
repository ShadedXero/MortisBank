package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortisbank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.utils.GuiSettings;
import com.mortisdevelopment.mortisbank.utils.InputMode;
import com.mortisdevelopment.mortiscore.configs.FileConfig;
import com.mortisdevelopment.mortiscore.databases.*;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.items.CustomItem;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
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
        GuiSettings guiSettings = loadGuiSettings(config);
        if (guiSettings == null) {
            return;
        }
        try {
            CustomMenu personalMenu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("personal-menu"), false);
            plugin.setPersonalManager(new PersonalManager(plugin, personalMenu));
            CustomMenu depositMenu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("deposit-menu"), false);
            plugin.setDepositManager(new DepositManager(plugin, depositMenu, guiSettings));
            CustomMenu withdrawMenu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("withdrawal-menu"), false);
            plugin.setWithdrawalManager(new WithdrawalManager(plugin.getEconomy(), plugin.getDataManager(), withdrawMenu, guiSettings));
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    private GuiSettings loadGuiSettings(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        InputMode mode;
        try {
            mode = InputMode.valueOf(section.getString("input-mode"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        int inputSlot = section.getInt("sign-input-slot");
        CustomItem customItem;
        try {
            customItem = plugin.getCore().getItemManager().getObject(plugin, section.getConfigurationSection("anvil-item"), false);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        return new GuiSettings(plugin, mode, inputSlot, customItem.getItem(new Placeholder()));
    }
}
