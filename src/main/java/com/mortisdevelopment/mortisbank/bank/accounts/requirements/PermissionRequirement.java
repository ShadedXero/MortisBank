package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PermissionRequirement extends AccountRequirement {

    private final String permission;

    public PermissionRequirement(@NotNull String permission) {
        this.permission = permission;
    }

    @Override
    public boolean hasRequirement(@NotNull Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public void removeRequirement(@NotNull Player player) {

    }

    @Override
    public String getRequirementStatus(@NotNull AccountManager accountManager) {
        MessageUtils utils = new MessageUtils(accountManager.getMessage("REQUIRED_PERMISSION"));
        utils.replace("%permission%", permission);
        return utils.getMessage();
    }

    public String getPermission() {
        return permission;
    }
}
