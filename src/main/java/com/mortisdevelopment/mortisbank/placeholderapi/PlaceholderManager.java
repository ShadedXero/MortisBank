package com.mortisdevelopment.mortisbank.placeholderapi;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.Manager;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends Manager {

    private final BankManager bankManager;
    private final PlaceholderAddon addon;

    public PlaceholderManager(@NotNull BankManager bankManager) {
        this.bankManager = bankManager;
        this.addon = new PlaceholderAddon(this);
        this.addon.register();
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public PlaceholderAddon getAddon() {
        return addon;
    }
}
