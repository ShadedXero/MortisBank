package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.configs.FileConfig;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.utils.ColorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Objects;

public class MessageConfig extends FileConfig {

    private final MortisBank plugin;

    public MessageConfig(MortisBank plugin) {
        super("messages.yml");
        this.plugin = plugin;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        File file = getFile(plugin);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String prefix = config.getString("prefix");
        loadMessages(plugin.getAccountManager(), prefix, Objects.requireNonNull(config.getConfigurationSection("account-messages")));
        loadMessages(plugin.getPersonalManager(), prefix, Objects.requireNonNull(config.getConfigurationSection("personal-messages")));
        loadMessages(plugin.getDepositManager(), prefix, Objects.requireNonNull(config.getConfigurationSection("deposit-messages")));
        loadMessages(plugin.getWithdrawalManager(), prefix, Objects.requireNonNull(config.getConfigurationSection("withdrawal-messages")));
        loadMessages(plugin.getPlaceholderManager(), prefix, Objects.requireNonNull(config.getConfigurationSection("placeholder-messages")));
        loadMessages(plugin.getCommandManager(), prefix, Objects.requireNonNull(config.getConfigurationSection("command-messages")));
    }

    private void loadMessages(Manager manager, String prefix, ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String id = key.replaceAll("%prefix%", prefix).replace("-", "_").toLowerCase();
            String message = section.getString(key);
            manager.addMessage(id, ColorUtils.getComponent(message));
        }
    }
}
