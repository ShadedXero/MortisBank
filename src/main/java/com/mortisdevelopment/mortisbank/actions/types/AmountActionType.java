package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.actions.types.ActionType;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderDouble;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public abstract class AmountActionType extends ActionType {

    private PlaceholderDouble placeholderAmount;

    protected AmountActionType() {
    }

    protected AmountActionType(double amount) {
        this.placeholderAmount = new PlaceholderDouble(amount);
    }

    protected AmountActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        if (section.contains("amount")) {
            this.placeholderAmount = new PlaceholderDouble(ConfigException.requireNonNull(section, section.getString("amount")));
        }
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        if (placeholderAmount != null) {
            placeholderAmount.setPlaceholder(placeholder);
        }
    }

    @Override
    public AmountActionType clone() {
        AmountActionType clone = (AmountActionType) super.clone();
        if (placeholderAmount != null) {
            clone.placeholderAmount = (PlaceholderDouble) placeholderAmount.clone();
        }
        return clone;
    }
}