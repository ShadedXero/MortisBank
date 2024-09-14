package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.BankCommand;
import com.mortisdevelopment.mortisbank.actions.types.DepositActionType;
import com.mortisdevelopment.mortisbank.messages.BankMessageManager;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortisbank.actions.types.WithdrawActionType;
import com.mortisdevelopment.mortisbank.placeholders.PlaceholderManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.databases.Database;
import com.mortisdevelopment.mortiscore.utils.ConfigUtils;
import com.mortisdevelopment.mortiscore.utils.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.Objects;

@Getter @Setter
public final class MortisBank extends CorePlugin {

    private MortisCore core;
    private Economy economy;
    private BankMessageManager messageManager;
    private Database database;
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
        messageManager = new BankMessageManager(this);
        database = getDatabase();
        accountManager = new AccountManager(this, database);
        transactionManager = new TransactionManager(this, database, messageManager.getMessages("transaction-messages"));
        bankManager = new BankManager(this, accountManager, transactionManager, database, economy, messageManager);
        placeholderManager = new PlaceholderManager(accountManager, transactionManager, bankManager, messageManager.getMessages("placeholder-messages"));
        placeholderManager.register();
        command = new BankCommand(messageManager.getMessages("command-messages"), this, bankManager, accountManager, transactionManager);
        command.register(this);
    }

    private Database getDatabase() {
        File configFile = ConfigUtils.getFile(this, "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return core.getDatabaseManager().getDatabase(this, config.getConfigurationSection("database"));
    }

    @Override
    public void onStart() {
        new File(getDataFolder(), "items").mkdirs();
        core.getItemManager().saveAndLoad(this);
        core.getMenuManager().saveAndLoad(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        bankManager.onStart();
    }

    public void reload() {
        core.getMenuManager().save(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        core.reload(this);
        messageManager.reload();
        accountManager.reload();
        transactionManager.reload();
        bankManager.reload();
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
