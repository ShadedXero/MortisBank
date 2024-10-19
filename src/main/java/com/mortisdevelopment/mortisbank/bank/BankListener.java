package com.mortisdevelopment.mortisbank.bank;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BankListener implements Listener {

    private final JavaPlugin plugin;
    private final BankManager bankManager;

    public BankListener(JavaPlugin plugin, BankManager bankManager) {
        this.plugin = plugin;
        this.bankManager = bankManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (bankManager.getBalanceByPlayer().containsKey(player.getUniqueId())) {
            return;
        }
        bankManager.setBalance(plugin, player.getUniqueId(), 0);
    }
}