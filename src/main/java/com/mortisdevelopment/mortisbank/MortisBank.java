package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.BankCommand;
import com.mortisdevelopment.mortisbank.actions.types.DepositActionType;
import com.mortisdevelopment.mortisbank.config.BankConfigManager;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortisbank.actions.types.WithdrawActionType;
import com.mortisdevelopment.mortisbank.placeholders.PlaceholderManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.configs.ConfigManager;
import com.mortisdevelopment.mortiscore.databases.Database;
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
    private ConfigManager configManager;
    private Database database;
    private MessageManager messageManager;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private BankManager bankManager;
    private PlaceholderManager placeholderManager;
    private BankCommand command;

    @Override
    public void onEnable() {
        this.core = (MortisCore) Objects.requireNonNull(getServer().getPluginManager().getPlugin("MortisCore"));
        core.register(this);
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        core.getActionManager().getActionTypeManager().getRegistry().register("[bank] deposit", DepositActionType.class);
        core.getActionManager().getActionTypeManager().getRegistry().register("[bank] withdraw", WithdrawActionType.class);
        enable();
    }

    private void enable() {
        configManager = new BankConfigManager(this);
        placeholderManager = new PlaceholderManager(this, messageManager.getMessages("placeholder_messages"));
        placeholderManager.register();
        command = new BankCommand(messageManager.getMessages("command_messages"), this, bankManager, accountManager, transactionManager);
        command.register();
    }

    @Override
    public void onStart() {
        new File(getDataFolder(), "items").mkdirs();
        try {
            core.getItemManager().saveAndLoad(this);
            core.getMenuManager().saveAndLoad(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        } catch (ConfigException exp) {
            throw new RuntimeException(exp);
        }
    }

    public void reload() {
        core.reload(this);
        placeholderManager.unregister();
        command.unregister();
        enable();
        onStart();
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
