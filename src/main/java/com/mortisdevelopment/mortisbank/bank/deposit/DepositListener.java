package com.mortisdevelopment.mortisbank.bank.deposit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DepositListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof DepositMenu)) {
            return;
        }
        e.setCancelled(true);
        DepositMenu menu = (DepositMenu) e.getClickedInventory().getHolder();
        menu.click(slot);
    }
}
