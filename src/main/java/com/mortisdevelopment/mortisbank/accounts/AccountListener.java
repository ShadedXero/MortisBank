package com.mortisdevelopment.mortisbank.accounts;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AccountListener implements Listener {

    private final JavaPlugin plugin;
    private final AccountManager accountManager;

    public AccountListener(JavaPlugin plugin, AccountManager accountManager) {
        this.plugin = plugin;
        this.accountManager = accountManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (accountManager.getPriorityByPlayer().containsKey(player.getUniqueId())) {
            return;
        }
        Account defaultAccount = accountManager.getDefaultAccount();
        accountManager.setAccount(plugin, player.getUniqueId(), defaultAccount != null ? defaultAccount.getPriority() : 0);
    }
}
