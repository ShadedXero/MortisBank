package com.mortisdevelopment.mortisbank.bank;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BankListener implements Listener {

    private final BankManager bankManager;

    public BankListener(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (bankManager.getBalanceByPlayer().containsKey(player.getUniqueId())) {
            return;
        }
        bankManager.setBalance(player.getUniqueId(), 0);
    }
}