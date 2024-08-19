package com.mortisdevelopment.mortisbank.withdrawal;

import com.mortisdevelopment.mortisbank.MortisBank;
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
public class WithdrawActionType extends MenuActionType {

    private MortisBank plugin;
    private PlaceholderEnum<WithdrawalManager.WithdrawalType> placeholderType;

    public WithdrawActionType(MortisBank plugin, WithdrawalManager.WithdrawalType type) throws ConfigException {
        this.plugin = plugin;
        this.placeholderType = new PlaceholderEnum<>(type);
    }

    public WithdrawActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.plugin = core.getRegisteredPlugin(MortisBank.class);
        this.placeholderType = new PlaceholderEnum<>(ConfigException.requireNonNull(section, section.getString("withdrawal-type")), WithdrawalManager.WithdrawalType.class);
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
        plugin.getWithdrawalManager().withdraw(offlinePlayer, placeholderType.getObject(placeholder));
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        placeholderType.setPlaceholder(placeholder);
    }

    @Override
    public WithdrawActionType clone() {
        WithdrawActionType clone = (WithdrawActionType) super.clone();
        clone.plugin = plugin;
        clone.placeholderType = (PlaceholderEnum<WithdrawalManager.WithdrawalType>) placeholderType.clone();
        return clone;
    }
}
