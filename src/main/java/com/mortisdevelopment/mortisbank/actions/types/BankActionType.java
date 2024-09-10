package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderDouble;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderEnum;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public abstract class BankActionType<T extends Enum<T>> extends AmountActionType {

    private final Class<T> typeClass;
    private BankManager bankManager;
    private PlaceholderEnum<T> placeholderType;

    public BankActionType(Class<T> typeClass, BankManager bankManager, T type) {
        this.typeClass = typeClass;
        this.bankManager = bankManager;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public BankActionType(Class<T> typeClass, BankManager bankManager, double amount) {
        super(amount);
        this.typeClass = typeClass;
        this.bankManager = bankManager;
    }

    public BankActionType(Class<T> typeClass, MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.typeClass = typeClass;
        this.bankManager = core.getRegisteredPlugin(MortisBank.class).getBankManager();
        if (!section.contains("amount")) {
            this.placeholderType = new PlaceholderEnum<T>(ConfigException.requireNonNull(section, section.getString("deposit-type")), typeClass);
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
    public BankActionType<T> clone() {
        BankActionType<T> clone = (BankActionType<T>) super.clone();
        clone.bankManager = bankManager;
        if (getPlaceholderAmount() != null) {
            clone.setPlaceholderAmount((PlaceholderDouble) getPlaceholderAmount().clone());
        }
        if (placeholderType != null) {
            clone.placeholderType = (PlaceholderEnum<T>) placeholderType.clone();
        }
        return clone;
    }
}
