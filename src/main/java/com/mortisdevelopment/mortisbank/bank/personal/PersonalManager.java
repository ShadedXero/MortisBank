package com.mortisdevelopment.mortisbank.bank.personal;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortiscorespigot.menus.Menu;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PersonalManager extends Manager {

    private final BankManager bankManager;
    private final Menu menu;

    public PersonalManager(@NotNull BankManager bankManager, @NotNull Menu menu) {
        this.bankManager = bankManager;
        this.menu = menu;
        MortisBank plugin = MortisBank.getInstance();
        plugin.getServer().getPluginManager().registerEvents(new PersonalListener(), plugin);
    }

    public PersonalTransaction getTransaction(@NotNull OfflinePlayer player, int position) {
        List<PersonalTransaction> transactions = bankManager.getDataManager().getTransactions(player.getUniqueId());
        return transactions.get(position);
    }

    public BankManager getBankManager() {
        return bankManager;
    }

    public Menu getMenu() {
        return menu;
    }
}
