package me.none030.mortisbank.methods;

import java.io.File;

import static me.none030.mortisbank.MortisBank.plugin;

public class StoringFiles {

    public static void StoreFiles() {

        File file = new File("plugins/MortisBank/", "config.yml");
        File file2 = new File("plugins/MortisBank/", "upgrades.yml");
        File file3 = new File("plugins/MortisBank/", "data.yml");
        File file4 = new File("plugins/MortisBank/", "menus.yml");

        if (!file.exists()) {
            plugin.saveResource("config.yml", true);
        }
        if (!file2.exists()) {
            plugin.saveResource("upgrades.yml", true);
        }
        if (!file3.exists()) {
            plugin.saveResource("data.yml", true);
        }
        if (!file4.exists()) {
            plugin.saveResource("menus.yml", true);
        }

    }
}
