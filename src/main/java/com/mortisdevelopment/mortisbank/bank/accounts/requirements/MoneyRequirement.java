package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

import com.mortisdevelopment.mortisbank.MortisBank;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyRequirement extends AccountRequirement {

    private final MortisBank plugin = MortisBank.getInstance();
    private final Economy economy = plugin.getEconomy();
    private final double money;

    public MoneyRequirement(double money) {
        this.money = money;
    }

    @Override
    public boolean hasRequirement(@NotNull Player player) {
        return economy.getBalance(player) >= money;
    }

    @Override
    public void removeRequirement(@NotNull Player player) {
        economy.withdrawPlayer(player, money);
    }

    public double getMoney() {
        return money;
    }
}
