package me.none030.mortisbank.utils;

import org.bukkit.inventory.ItemStack;

public class MenuItem {

    private ItemStack item;
    private int slot;

    public MenuItem(ItemStack item, int slot) {
        this.item = item;
        this.slot = slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }
}
