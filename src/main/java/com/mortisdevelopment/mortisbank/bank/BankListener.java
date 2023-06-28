package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.bank.accounts.Account;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BankListener implements Listener {

    private final BankManager bankManager;

    public BankListener(@NotNull BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (bankManager.getDataManager().hasPlayer(player.getUniqueId())) {
            return;
        }
        Account defaultAccount = bankManager.getAccountManager().getDefaultAccount();
        short accountPriority;
        if (defaultAccount != null) {
            accountPriority = defaultAccount.getPriority();
        }else {
            accountPriority = 0;
        }
        bankManager.getDataManager().addBank(player.getUniqueId(), 0, accountPriority, bankManager.getInterestManager().getNewInterestTime(), new ArrayList<>());
    }
}
