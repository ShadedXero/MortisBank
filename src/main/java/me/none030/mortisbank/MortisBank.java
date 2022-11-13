package me.none030.mortisbank;

import me.none030.mortisbank.commands.BankCommand;
import me.none030.mortisbank.events.BankEvents;
import me.none030.mortisbank.events.SignEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

import static me.none030.mortisbank.methods.Interests.CheckInterests;
import static me.none030.mortisbank.methods.StoringDatabaseData.initializeDatabase;
import static me.none030.mortisbank.methods.StoringFiles.StoreFiles;

public final class MortisBank extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public static Plugin plugin;
    public static boolean database = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        StoreFiles();
        File file = new File("plugins/MortisBank/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (Objects.requireNonNull(config.getString("config.data")).equalsIgnoreCase("DATABASE")) {
            database = true;
        }

        if (database) {
            initializeDatabase();
        }

        CheckInterests();
        getServer().getPluginManager().registerEvents(new BankEvents(), this);
        getServer().getPluginManager().registerEvents(new SignEvents(), this);
        getServer().getPluginCommand("bank").setExecutor(new BankCommand());

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
