package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AccountRequirement {

    public abstract boolean hasRequirement(@NotNull Player player);

    public abstract void removeRequirement(@NotNull Player player);

    public abstract String getRequirementStatus(@NotNull AccountManager accountManager);
}
