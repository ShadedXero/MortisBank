package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.actions.types.SetAccountActionType;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.accounts.AccountListener;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.BankCommand;
import com.mortisdevelopment.mortisbank.config.AccountConfig;
import com.mortisdevelopment.mortisbank.config.MainConfig;
import com.mortisdevelopment.mortisbank.config.MessageConfig;
import com.mortisdevelopment.mortisbank.actions.types.DepositActionType;
import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortisbank.actions.types.WithdrawActionType;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.placeholders.PlaceholderManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter @Setter
public final class MortisBank extends JavaPlugin {

    private MortisCore core;
    private Economy economy;
    private MessageManager messageManager;
    private PlaceholderManager placeholderManager;
    private DataManager dataManager;
    private AccountManager accountManager;
    private PersonalManager personalManager;
    private BankManager bankManager;

    @Override
    public void onLoad() {
        this.core = (MortisCore) Objects.requireNonNull(getServer().getPluginManager().getPlugin("MortisCore"));
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
        new File(getDataFolder(), "items").mkdirs();
        try {
            core.getMenuManager().saveAndLoad(this);
            core.getMenuManager().saveAndLoad(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        new MessageConfig(this);
        new MainConfig(this);
        new AccountConfig(this);
        placeholderManager = new PlaceholderManager(this, messageManager);
        placeholderManager.register();
        getServer().getPluginManager().registerEvents(new AccountListener(accountManager), this);
        new BankCommand(messageManager.getMessages("command_messages"), personalManager).register();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
