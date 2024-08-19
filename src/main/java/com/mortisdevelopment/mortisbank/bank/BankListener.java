package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class BankListener implements Listener {

    private final DataManager dataManager;
    private final AccountManager accountManager;

    public BankListener(DataManager dataManager, AccountManager accountManager) {
        this.dataManager = dataManager;
        this.accountManager = accountManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (dataManager.hasPlayer(player.getUniqueId())) {
            return;
        }
        Account defaultAccount = accountManager.getDefaultAccount();
        short accountPriority;
        if (defaultAccount != null) {
            accountPriority = defaultAccount.getPriority();
        }else {
            accountPriority = 0;
        }
        dataManager.addBank(player.getUniqueId(), 0, accountPriority, new ArrayList<>());
    }
}
