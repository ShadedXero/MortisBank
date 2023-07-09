package com.mortisdevelopment.mortisbank.bank.personal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PersonalListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        if (e.getClickedInventory() == null || !(e.getClickedInventory().getHolder() instanceof PersonalMenu)) {
            return;
        }
        e.setCancelled(true);
        PersonalMenu menu = (PersonalMenu) e.getClickedInventory().getHolder();
        menu.click(slot);
    }
}
