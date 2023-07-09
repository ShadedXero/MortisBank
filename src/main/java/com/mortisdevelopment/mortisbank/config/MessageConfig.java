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
        configManager.getManager().getAccountManager().addMessages(loadMessages(config.getConfigurationSection("account-messages"), prefix));
        configManager.getManager().getPersonalManager().addMessages(loadMessages(config.getConfigurationSection("personal-messages"), prefix));
        configManager.getManager().getDepositManager().addMessages(loadMessages(config.getConfigurationSection("deposit-messages"), prefix));
        configManager.getManager().getWithdrawalManager().addMessages(loadMessages(config.getConfigurationSection("withdrawal-messages"), prefix));
        configManager.getManager().getInterestManager().addMessages(loadMessages(config.getConfigurationSection("interest-messages"), prefix));
        configManager.getManager().getPlaceholderManager().addMessages(loadMessages(config.getConfigurationSection("placeholder-messages"), prefix));
        configManager.getManager().getAdminManager().addMessages(loadMessages(config.getConfigurationSection("command-messages"), prefix));
    }

    private String loadPrefix(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        return section.getString("prefix");
    }

    private HashMap<String, String> loadMessages(ConfigurationSection section, String prefix) {
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
}
