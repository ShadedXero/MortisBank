package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.actions.types.accounts.SetAccountActionType;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.BankCommand;
import com.mortisdevelopment.mortisbank.config.AccountConfig;
import com.mortisdevelopment.mortisbank.config.MainConfig;
import com.mortisdevelopment.mortisbank.config.MessageConfig;
import com.mortisdevelopment.mortisbank.actions.types.DepositActionType;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortisbank.actions.types.WithdrawActionType;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.placeholders.PlaceholderManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import com.mortisdevelopment.mortiscore.utils.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.Objects;

@Getter @Setter
public final class MortisBank extends CorePlugin {

    private MortisCore core;
    private Economy economy;
    private MessageManager messageManager;
    private PlaceholderManager placeholderManager;
    private DataManager dataManager;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private BankManager bankManager;

    @Override
    public void onEnable() {
        System.out.println("Core Ready");
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
        new MessageConfig(this);
        new MainConfig(this);
        new AccountConfig(this);
        placeholderManager = new PlaceholderManager(this, messageManager);
        placeholderManager.register();
        new BankCommand(messageManager.getMessages("command_messages"), bankManager).register();
    }

    @Override
    public void onStart() {
        new File(getDataFolder(), "items").mkdirs();
        try {
            core.getMenuManager().saveAndLoad(this);
            core.getMenuManager().saveAndLoad(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        } catch (ConfigException exp) {
            throw new RuntimeException(exp);
        }
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
