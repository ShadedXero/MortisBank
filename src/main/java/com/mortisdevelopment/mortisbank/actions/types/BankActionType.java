package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.menus.actions.types.MenuActionType;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderDouble;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderEnum;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class BankActionType<T extends Enum<T>> extends MenuActionType {

    private final Class<T> typeClass;
    private MortisBank plugin;
    private PlaceholderDouble placeholderAmount;
    private PlaceholderEnum<T> placeholderType;

    public BankActionType(Class<T> typeClass, MortisBank plugin, T type) {
        this.typeClass = typeClass;
        this.plugin = plugin;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public BankActionType(Class<T> typeClass, MortisBank plugin, double amount) {
        this.typeClass = typeClass;
        this.plugin = plugin;
        this.placeholderAmount = new PlaceholderDouble(amount);
    }

    public BankActionType(Class<T> typeClass, MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.typeClass = typeClass;
        this.plugin = core.getRegisteredPlugin(MortisBank.class);
        if (section.contains("amount")) {
            this.placeholderAmount = new PlaceholderDouble(ConfigException.requireNonNull(section, section.getString("amount")));
        }else {
            this.placeholderType = new PlaceholderEnum<T>(ConfigException.requireNonNull(section, section.getString("deposit-type")), typeClass);
        }
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        if (placeholderAmount != null) {
            placeholderAmount.setPlaceholder(placeholder);
        }
        if (placeholderType != null) {
            placeholderType.setPlaceholder(placeholder);
        }
    }

    @Override
    public BankActionType<T> clone() {
        BankActionType<T> clone = (BankActionType<T>) super.clone();
        clone.plugin = plugin;
        if (placeholderAmount != null) {
            clone.placeholderAmount = (PlaceholderDouble) placeholderAmount.clone();
        }
        if (placeholderType != null) {
            clone.placeholderType = (PlaceholderEnum<T>) placeholderType.clone();
        }
        return clone;
    }
}
