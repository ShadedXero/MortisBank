package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.accounts.SetAccountActionType;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankListener;
import com.mortisdevelopment.mortisbank.commands.BankCommand;
import com.mortisdevelopment.mortisbank.commands.CommandManager;
import com.mortisdevelopment.mortisbank.config.AccountConfig;
import com.mortisdevelopment.mortisbank.config.MainConfig;
import com.mortisdevelopment.mortisbank.config.MessageConfig;
import com.mortisdevelopment.mortisbank.deposit.DepositActionType;
import com.mortisdevelopment.mortisbank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortisbank.withdrawal.WithdrawActionType;
import com.mortisdevelopment.mortisbank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.placeholder.PlaceholderManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public final class MortisBank extends JavaPlugin {

    private MortisCore core;
    private Economy economy;
    private CommandManager commandManager;
    private PlaceholderManager placeholderManager;
    private DataManager dataManager;
    private AccountManager accountManager;
    private PersonalManager personalManager;
    private DepositManager depositManager;
    private WithdrawalManager withdrawalManager;

    @Override
    public void onLoad() {
        this.core = (MortisCore) getServer().getPluginManager().getPlugin("MortisCore");
        core.register(this);
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        core.getActionManager().getActionTypeManager().getRegistry().register("[bank] deposit", DepositActionType.class);
        core.getActionManager().getActionTypeManager().getRegistry().register("[bank] withdraw", WithdrawActionType.class);
        core.getActionManager().getActionTypeManager().getRegistry().register("[bank] set account", SetAccountActionType.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
//        new File(getDataFolder(), "items").mkdirs();
        try {
            core.getMenuManager().saveAndLoad(this);
            core.getMenuManager().saveAndLoad(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        commandManager = new CommandManager(this);
        placeholderManager = new PlaceholderManager(this);
        new MainConfig(this);
        new AccountConfig(this);
        new MessageConfig(this);
        getServer().getPluginManager().registerEvents(new BankListener(dataManager, accountManager), this);
        new BankCommand(commandManager, personalManager).register();
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
}
