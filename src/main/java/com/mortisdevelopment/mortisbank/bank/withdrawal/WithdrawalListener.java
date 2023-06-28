package com.mortisdevelopment.mortisbank.bank.withdrawal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WithdrawalListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof WithdrawalMenu)) {
            return;
        }
        WithdrawalMenu menu = (WithdrawalMenu) e.getClickedInventory().getHolder();
        menu.click(slot);
    }
}
