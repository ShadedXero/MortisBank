package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.placeholders.Placeholder;
import com.mortisdevelopment.mortiscore.placeholders.objects.PlaceholderEnum;
import com.mortisdevelopment.mortiscore.utils.Executor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class DepositActionType extends AmountActionType {

    private MortisBank plugin;
    private PlaceholderEnum<BankManager.DepositType> placeholderType;

    public DepositActionType(MortisBank plugin, BankManager.DepositType type) {
        this.plugin = plugin;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public DepositActionType(MortisBank plugin, double amount) {
        super(amount);
        this.plugin = plugin;
    }

    public DepositActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.plugin = core.getRegisteredPlugin(MortisBank.class);
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
            plugin.getBankManager().deposit(plugin, offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            plugin.getBankManager().deposit(plugin, offlinePlayer, getPlaceholderType().getObject(placeholder));
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
        clone.plugin = plugin;
        if (placeholderType != null) {
            clone.placeholderType = (PlaceholderEnum<BankManager.DepositType>) placeholderType.clone();
        }
        return clone;
    }
}