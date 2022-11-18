package me.none030.mortisbank;

import me.none030.mortisbank.api.MortisBankAPI;
import me.none030.mortisbank.commands.BankCommand;
import me.none030.mortisbank.events.BankEvents;
import me.none030.mortisbank.methods.PlaceholderAPI;
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
import static me.none030.mortisbank.methods.StoringMenuItems.StoreAllMenusItems;
import static me.none030.mortisbank.methods.StoringMessages.StoreMessages;

public final class MortisBank extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;
    public static Plugin plugin;
    private static MortisBankAPI API;
    public static boolean database = false;
    public static boolean SignGui = false;
    public static boolean ProtocolLib = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLib = true;
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

        getServer().getPluginManager().registerEvents(new BankEvents(), this);
        getServer().getPluginCommand("bank").setExecutor(new BankCommand());
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPI().register();

        } else {
            log.warning("Could not find PlaceholderAPI! This plugin is required.");
        }
        StoreAllMenusItems();
        StoreMessages();
        if (config.getBoolean("config.interests.enabled")) {
            CheckInterests();
        }
        SignGui = config.getBoolean("config.use-anvil-instead-sign");
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

    public static MortisBankAPI getAPI() {
        return API;
    }

}
