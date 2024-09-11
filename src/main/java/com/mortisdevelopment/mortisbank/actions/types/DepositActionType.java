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
public class DepositActionType extends AmountActionType {

    private BankManager bankManager;
    private PlaceholderEnum<BankManager.DepositType> placeholderType;

    public DepositActionType(BankManager bankManager, BankManager.DepositType type) {
        this.bankManager = bankManager;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public DepositActionType(BankManager bankManager, double amount) {
        super(amount);
        this.bankManager = bankManager;
    }

    public DepositActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.bankManager = core.getRegisteredPlugin(MortisBank.class).getBankManager();
        if (!section.contains("amount")) {
            this.placeholderType = new PlaceholderEnum<>(ConfigException.requireNonNull(section, section.getString("deposit-type")), BankManager.DepositType.class);
        }
    }

    @Override
    public void execute(Executor executor, Placeholder placeholder) {
        OfflinePlayer offlinePlayer = executor.getOfflinePlayer();
        if (offlinePlayer == null) {
            return;
        }
        if (getPlaceholderAmount() != null) {
            bankManager.deposit(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            bankManager.deposit(offlinePlayer, getPlaceholderType().getObject(placeholder));
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
    public DepositActionType clone() {
        DepositActionType clone = (DepositActionType) super.clone();
        clone.bankManager = bankManager;
        if (placeholderType != null) {
            clone.placeholderType = (PlaceholderEnum<BankManager.DepositType>) placeholderType.clone();
        }
        return clone;
    }
}