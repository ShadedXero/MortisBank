package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import com.mortisdevelopment.mortiscorespigot.utils.MoneyUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

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

    @Override
    public String getRequirementStatus(@NotNull AccountManager accountManager) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        MessageUtils utils = new MessageUtils(accountManager.getMessage("REQUIRED_MONEY"));
        utils.replace("%amount%", formatter.format(money));
        utils.replace("%amount_raw%", String.valueOf(money));
        utils.replace("%amount_formatted%", MoneyUtils.getMoney(money));
        return utils.getMessage();
    }

    public double getMoney() {
        return money;
    }
}
