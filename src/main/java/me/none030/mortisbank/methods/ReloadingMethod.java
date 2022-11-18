package me.none030.mortisbank.methods;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static me.none030.mortisbank.methods.StoringMenuItems.StoreAllMenusItems;
import static me.none030.mortisbank.methods.StoringMessages.StoreMessages;
import static me.none030.mortisbank.MortisBank.SignGui;

public class ReloadingMethod {

    public static void Reload() {

        File file = new File("plugins/MortisBank/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        StoreMessages();
        StoreAllMenusItems();
        SignGui = config.getBoolean("config.use-anvil-instead-sign");
    }
}
