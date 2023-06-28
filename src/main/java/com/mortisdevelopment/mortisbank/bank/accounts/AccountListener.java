package com.mortisdevelopment.mortisbank.bank.accounts;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AccountListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof AccountMenu)) {
            return;
        }
        AccountMenu menu = (AccountMenu) e.getClickedInventory().getHolder();
        menu.click(e.getRawSlot());
    }
}
