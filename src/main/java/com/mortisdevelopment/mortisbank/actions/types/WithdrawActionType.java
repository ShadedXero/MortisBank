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
public class WithdrawActionType extends BankActionType<BankManager.WithdrawalType> {

    public WithdrawActionType(BankManager bankManager, BankManager.WithdrawalType type) {
        super(BankManager.WithdrawalType.class, bankManager, type);
    }

    public WithdrawActionType(BankManager bankManager, double amount) {
        super(BankManager.WithdrawalType.class, bankManager, amount);
    }

    public WithdrawActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(BankManager.WithdrawalType.class, core, plugin, section);
    }

    @Override
    public void execute(Executor executor, Placeholder placeholder) {
        OfflinePlayer offlinePlayer = executor.getOfflinePlayer();
        if (offlinePlayer == null) {
            return;
        }
        if (getPlaceholderAmount() != null) {
            getBankManager().withdraw(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            getBankManager().withdraw(offlinePlayer, getPlaceholderType().getObject(placeholder));
        }
    }
}