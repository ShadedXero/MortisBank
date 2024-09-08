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
public class WithdrawActionType extends BankActionType<BankManager.WithdrawalType> {

    public WithdrawActionType(MortisBank plugin, BankManager.WithdrawalType type) {
        super(BankManager.WithdrawalType.class, plugin, type);
    }

    public WithdrawActionType(MortisBank plugin, double amount) {
        super(BankManager.WithdrawalType.class, plugin, amount);
    }

    public WithdrawActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(BankManager.WithdrawalType.class, core, plugin, section);
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
            getPlugin().getBankManager().withdraw(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            getPlugin().getBankManager().withdraw(offlinePlayer, getPlaceholderType().getObject(placeholder));
        }
    }
}