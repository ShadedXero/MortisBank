package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.menus.executors.MenuExecutor;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.utils.Executor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class DepositActionType extends BankActionType<BankManager.DepositType> {

    public DepositActionType(MortisBank plugin, BankManager.DepositType type) {
        super(BankManager.DepositType.class, plugin, type);
    }

    public DepositActionType(MortisBank plugin, double amount) {
        super(BankManager.DepositType.class, plugin, amount);
    }

    public DepositActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(BankManager.DepositType.class, core, plugin, section);
    }

    @Override
    public void execute(Executor executor, Placeholder placeholder) {
        MenuExecutor menuExecutor = getMenuExecutor(executor);
        if (menuExecutor == null) {
            return;
        }
        OfflinePlayer offlinePlayer = menuExecutor.getOfflinePlayer();
        if (offlinePlayer == null) {
            return;
        }
        if (getPlaceholderAmount() != null) {
            getPlugin().getBankManager().deposit(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            getPlugin().getBankManager().deposit(offlinePlayer, getPlaceholderType().getObject(placeholder));
        }
    }
}