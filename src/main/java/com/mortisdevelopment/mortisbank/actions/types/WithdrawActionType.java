package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderEnum;
import com.mortisdevelopment.mortiscore.utils.Executor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class WithdrawActionType extends AmountActionType {

    private BankManager bankManager;
    private PlaceholderEnum<BankManager.WithdrawalType> placeholderType;

    public WithdrawActionType(BankManager bankManager, BankManager.WithdrawalType type) {
        this.bankManager = bankManager;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public WithdrawActionType(BankManager bankManager, double amount) {
        super(amount);
        this.bankManager = bankManager;
    }

    public WithdrawActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.bankManager = core.getRegisteredPlugin(MortisBank.class).getBankManager();
        if (!section.contains("amount")) {
            this.placeholderType = new PlaceholderEnum<>(ConfigException.requireNonNull(section, section.getString("withdrawal-type")), BankManager.WithdrawalType.class);
        }
    }

    @Override
    public void execute(Executor executor, Placeholder placeholder) {
        OfflinePlayer offlinePlayer = executor.getOfflinePlayer();
        if (offlinePlayer == null) {
            return;
        }
        if (getPlaceholderAmount() != null) {
            bankManager.withdraw(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            bankManager.withdraw(offlinePlayer, getPlaceholderType().getObject(placeholder));
        }
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        super.setPlaceholder(placeholder);
        if (placeholderType != null) {
            placeholderType.setPlaceholder(placeholder);
        }
    }

    @Override
    public WithdrawActionType clone() {
        WithdrawActionType clone = (WithdrawActionType) super.clone();
        clone.bankManager = bankManager;
        if (placeholderType != null) {
            clone.placeholderType = (PlaceholderEnum<BankManager.WithdrawalType>) placeholderType.clone();
        }
        return clone;
    }
}