package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.utils.Executor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class DepositActionType extends BankActionType<BankManager.DepositType> {

    public DepositActionType(BankManager bankManager, BankManager.DepositType type) {
        super(BankManager.DepositType.class, bankManager, type);
    }

    public DepositActionType(BankManager bankManager, double amount) {
        super(BankManager.DepositType.class, bankManager, amount);
    }

    public DepositActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(BankManager.DepositType.class, core, plugin, section);
    }

    @Override
    public void execute(Executor executor, Placeholder placeholder) {
        OfflinePlayer offlinePlayer = executor.getOfflinePlayer();
        if (offlinePlayer == null) {
            return;
        }
        if (getPlaceholderAmount() != null) {
            getBankManager().deposit(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            getBankManager().deposit(offlinePlayer, getPlaceholderType().getObject(placeholder));
        }
    }
}