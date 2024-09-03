package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.DepositType;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.menus.actions.types.MenuActionType;
import com.mortisdevelopment.mortiscore.menus.executors.MenuExecutor;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderEnum;
import com.mortisdevelopment.mortiscore.utils.Executor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class DepositActionType extends MenuActionType {

    private MortisBank plugin;
    private PlaceholderEnum<DepositType> placeholderType;

    public DepositActionType(MortisBank plugin, DepositType type) throws ConfigException {
        this.plugin = plugin;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public DepositActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.plugin = core.getRegisteredPlugin(MortisBank.class);
        this.placeholderType = new PlaceholderEnum<>(ConfigException.requireNonNull(section, section.getString("deposit-type")), DepositType.class);
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
        plugin.getBankManager().deposit(offlinePlayer, placeholderType.getObject(placeholder));
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        placeholderType.setPlaceholder(placeholder);
    }

    @Override
    public DepositActionType clone() {
        DepositActionType clone = (DepositActionType) super.clone();
        clone.plugin = plugin;
        clone.placeholderType = (PlaceholderEnum<DepositType>) placeholderType.clone();
        return clone;
    }
}
