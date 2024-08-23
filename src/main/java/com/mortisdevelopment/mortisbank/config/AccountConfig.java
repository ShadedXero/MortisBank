package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.accounts.AccountSettings;
import com.mortisdevelopment.mortiscore.configs.FileConfig;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.utils.ColorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class AccountConfig extends FileConfig {

    private final MortisBank plugin;

    public AccountConfig(MortisBank plugin) {
        super("accounts.yml");
        this.plugin = plugin;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        File file = getFile(plugin);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        CustomMenu menu;
        try {
            menu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("account-menu"), false);
        } catch (ConfigException exp) {
            throw new RuntimeException(exp);
        }
        if (menu == null) {
            return;
        }
        AccountSettings settings = loadSettings(config);
        if (settings == null) {
            return;
        }
        plugin.setAccountManager(new AccountManager(plugin.getDataManager(), menu, settings));
        loadAccounts(config.getConfigurationSection("accounts"));
    }

    private AccountSettings loadSettings(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        short defaultAccount = (short) section.getInt("default-account");
        return new AccountSettings(defaultAccount);
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
