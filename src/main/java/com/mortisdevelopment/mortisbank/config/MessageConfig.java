package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortiscorespigot.configs.Config;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

public class MessageConfig extends Config {

    private final ConfigManager configManager;

    public MessageConfig(@NotNull ConfigManager configManager) {
        super("messages.yml");
        this.configManager = configManager;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        saveConfig();
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        String prefix = loadPrefix(config);
        if (prefix == null) {
            return;
        }
        loadAccountMessages(config.getConfigurationSection("account-messages"), prefix);
        loadDepositMessages(config.getConfigurationSection("deposit-messages"), prefix);
        loadWithdrawalMessages(config.getConfigurationSection("withdrawal-messages"), prefix);
        loadInterestMessages(config.getConfigurationSection("interest-messages"), prefix);
        loadPlaceholderMessages(config.getConfigurationSection("placeholder-messages"), prefix);
        loadCommandMessages(config.getConfigurationSection("command-messages"), prefix);
    }

    private String loadPrefix(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        return section.getString("prefix");
    }

    private HashMap<String, String> loadMessageList(ConfigurationSection section, String prefix) {
        if (section == null) {
            return new HashMap<>();
        }
        HashMap<String, String> messageById = new HashMap<>();
        for (String key : section.getKeys(false)) {
            String id = key.replace("-", "_").toUpperCase();
            String message = section.getString(key);
            MessageUtils utils = new MessageUtils(message);
            utils.replace("%prefix%", prefix);
            utils.color();
            messageById.put(id, utils.getMessage());
        }
        return messageById;
    }

    private void loadAccountMessages(ConfigurationSection section, String prefix) {
        configManager.getManager().getAccountManager().addMessages(loadMessageList(section, prefix));
    }

    private void loadDepositMessages(ConfigurationSection section, String prefix) {
        configManager.getManager().getDepositManager().addMessages(loadMessageList(section, prefix));
    }

    private void loadWithdrawalMessages(ConfigurationSection section, String prefix) {
        configManager.getManager().getWithdrawalManager().addMessages(loadMessageList(section, prefix));
    }

    private void loadInterestMessages(ConfigurationSection section, String prefix) {
        configManager.getManager().getInterestManager().addMessages(loadMessageList(section, prefix));
    }

    private void loadPlaceholderMessages(ConfigurationSection section, String prefix) {
        configManager.getManager().getPlaceholderManager().addMessages(loadMessageList(section, prefix));
    }

    private void loadCommandMessages(ConfigurationSection section, String prefix) {
        configManager.getManager().getAdminManager().addMessages(loadMessageList(section, prefix));
    }
}
