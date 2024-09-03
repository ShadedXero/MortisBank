package com.mortisdevelopment.mortisbank.accounts;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class AccountListener implements Listener {

    private final AccountManager accountManager;

    public AccountListener(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (accountManager.getDataManager().hasPlayer(player.getUniqueId())) {
            return;
        }
        Account defaultAccount = accountManager.getDefaultAccount();
        short accountPriority;
        if (defaultAccount != null) {
            accountPriority = defaultAccount.getPriority();
        }else {
            accountPriority = 0;
        }
        accountManager.getDataManager().addBank(player.getUniqueId(), 0, accountPriority, new ArrayList<>());
    }
}
