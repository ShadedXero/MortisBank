package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemRequirement extends AccountRequirement {

    private final ItemStack item;

    public ItemRequirement(@NotNull ItemStack item, int amount) {
        this.item = item;
        this.item.setAmount(amount);
    }

    @Override
    public boolean hasRequirement(@NotNull Player player) {
        return player.getInventory().containsAtLeast(item, item.getAmount());
    }

    @Override
    public void removeRequirement(@NotNull Player player) {
        player.getInventory().removeItem(item);
    }

    public int getAmount() {
        return item.getAmount();
    }

    public ItemStack getItem() {
        return item;
    }
}
