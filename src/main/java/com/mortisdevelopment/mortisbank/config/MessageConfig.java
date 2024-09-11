package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.configs.FileConfig;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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
        try {
            plugin.getMessageManager().loadMessages(config);
        } catch (ConfigException e) {
            e.setFile(file);
            e.setPath(config);
            throw new RuntimeException(e);
        }
    }
}