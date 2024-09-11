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

    private MortisBank plugin;
    private PlaceholderEnum<BankManager.WithdrawalType> placeholderType;

    public WithdrawActionType(MortisBank plugin, BankManager.WithdrawalType type) {
        this.plugin = plugin;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public WithdrawActionType(MortisBank plugin, double amount) {
        super(amount);
        this.plugin = plugin;
    }

    public WithdrawActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.plugin = core.getRegisteredPlugin(MortisBank.class);
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
            plugin.getBankManager().withdraw(offlinePlayer, getPlaceholderAmount().getObject(placeholder));
        }else {
            plugin.getBankManager().withdraw(offlinePlayer, getPlaceholderType().getObject(placeholder));
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
        clone.plugin = plugin;
        if (placeholderType != null) {
            clone.placeholderType = (PlaceholderEnum<BankManager.WithdrawalType>) placeholderType.clone();
        }
        return clone;
    }
}