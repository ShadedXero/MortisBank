package com.mortisdevelopment.mortisbank.bank.interest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class InterestListener implements Listener {

    private final InterestManager interestManager;

    public InterestListener(@NotNull InterestManager interestManager) {
        this.interestManager = interestManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (interestManager.getAccountManager().getBankManager().getDataManager().getBalanceByUUID().containsKey(player.getUniqueId())) {
            interestManager.check(player);
        }
    }
}
