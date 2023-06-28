package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

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

    public String getPermission() {
        return permission;
    }
}
