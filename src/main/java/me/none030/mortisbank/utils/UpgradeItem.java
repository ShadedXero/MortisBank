package me.none030.mortisbank.utils;

import org.bukkit.inventory.ItemStack;

public class UpgradeItem {

    private final ItemStack item;
    private int slot;
    private final int bankAccount;

    public UpgradeItem(ItemStack item, int slot, int bankAccount) {
        this.item = item;
        this.slot = slot;
        this.bankAccount = bankAccount;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getBankAccount() {
        return bankAccount;
    }

}
