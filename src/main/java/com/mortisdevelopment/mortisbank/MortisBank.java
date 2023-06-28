package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscorespigot.MortisCoreSpigot;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MortisBank extends JavaPlugin {

    private static MortisBank Instance;
    private BankManager bankManager;
    private Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        MortisCoreSpigot.register(this);
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getServer().getPluginManager().disablePlugin(this);
        }
        bankManager = new BankManager();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    public static MortisBank getInstance() {
        return Instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public BankManager getBankManager() {
        return bankManager;
    }
}
