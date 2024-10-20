package com.mortisdevelopment.mortisbank;

import com.mortisdevelopment.mortisbank.accounts.AccountListener;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankListener;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;

@Getter @Setter
public final class MortisBank extends CorePlugin {

    private MortisCore core;
    private BankMessageManager messageManager;
    private Database database;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private BankManager bankManager;
    private PlaceholderManager placeholderManager;
    private BankCommand command;

    @Override
    public void onEnable() {
        core = (MortisCore) Objects.requireNonNull(getServer().getPluginManager().getPlugin("MortisCore"));
        core.register(this);

        core.getActionContainerManager().getActionManager().getRegistry().register("[bank] deposit", DepositActionType.class);
        core.getActionContainerManager().getActionManager().getRegistry().register("[bank] withdraw", WithdrawActionType.class);

        messageManager = new BankMessageManager();
        messageManager.reload(this);

        database = getDatabase();

        accountManager = new AccountManager(database);
        accountManager.reload(this);
        getServer().getPluginManager().registerEvents(new AccountListener(this, accountManager), this);

        transactionManager = new TransactionManager(database, messageManager.getMessages("transaction-messages"));
        transactionManager.reload(this);

        bankManager = new BankManager(accountManager, transactionManager, database, messageManager);
        bankManager.reload(this, false);
        getServer().getPluginManager().registerEvents(new BankListener(this, bankManager), this);

        placeholderManager = new PlaceholderManager(this, accountManager, transactionManager, bankManager, messageManager.getMessages("placeholder-messages"));
        placeholderManager.register();

        command = new BankCommand(messageManager.getMessages("command-messages"), this, bankManager, accountManager, transactionManager);
        command.register(this);
    }

    private Database getDatabase() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(ConfigUtils.getFile(this, "config.yml"));
        return core.getDatabaseManager().getDatabase(this, config.getConfigurationSection("database"));
    }

    @Override
    public void onStart() {
        core.getMenuManager().saveAndLoad(this, "menus", "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        bankManager.onStart(this);
    }

    public void reload() {
        core.getMenuManager().save(this, "personal.yml", "deposit.yml", "withdrawal.yml", "accounts.yml");
        core.getMenuManager().getRegistry().unregister(this);
        core.getMenuManager().load(this, "menus");
        messageManager.reload(this);
        accountManager.reload(this);
        transactionManager.reload(this);
        bankManager.reload(this);
    }
}
