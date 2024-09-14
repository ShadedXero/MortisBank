package com.mortisdevelopment.mortisbank.messages;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import com.mortisdevelopment.mortiscore.utils.ConfigUtils;
import com.mortisdevelopment.mortiscore.utils.Reloadable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BankMessageManager extends MessageManager implements Reloadable {

    private final MortisBank plugin;

    public BankMessageManager(MortisBank plugin) {
        this.plugin = plugin;
        reload();
    }

    @Override
    public void reload() {
        File file = ConfigUtils.getFile(plugin, "messages.yml");
        FileConfiguration messages = YamlConfiguration.loadConfiguration(file);
        try {
            loadMessages(messages);
        } catch (ConfigException e) {
            e.setFile(file);
            e.setPath(messages);
            throw new RuntimeException(e);
        }
    }
}
