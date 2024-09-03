package com.mortisdevelopment.mortisbank.actions.types;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.MortisCore;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.menus.actions.types.MenuActionType;
import com.mortisdevelopment.mortiscore.menus.executors.MenuExecutor;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.PlaceholderShort;
import com.mortisdevelopment.mortiscore.utils.Executor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class SetAccountActionType extends MenuActionType {

    private MortisBank plugin;
    private PlaceholderShort placeholderPriority;

    public SetAccountActionType(MortisBank plugin, short priority) throws ConfigException {
        this.plugin = plugin;
        this.placeholderPriority = new PlaceholderShort(priority);
    }

    public SetAccountActionType(MortisCore core, JavaPlugin plugin, ConfigurationSection section) throws ConfigException {
        super(core, plugin, section);
        this.plugin = core.getRegisteredPlugin(MortisBank.class);
        this.placeholderPriority = new PlaceholderShort(ConfigException.requireNonNull(section, section.getString("priority")));
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
        plugin.getDataManager().setAccount(offlinePlayer.getUniqueId(), placeholderPriority.getObject(placeholder));
    }

    @Override
    public void setPlaceholder(Placeholder placeholder) {
        placeholderPriority.setPlaceholder(placeholder);
    }

    @Override
    public SetAccountActionType clone() {
        SetAccountActionType clone = (SetAccountActionType) super.clone();
        clone.placeholderPriority = (PlaceholderShort) placeholderPriority.clone();
        return clone;
    }
}
