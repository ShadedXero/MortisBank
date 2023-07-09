package com.mortisdevelopment.mortisbank.bank.accounts.requirements;

import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class  ItemRequirement extends AccountRequirement {

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

    @Override
    public String getRequirementStatus(@NotNull AccountManager accountManager) {
        MessageUtils utils = new MessageUtils(accountManager.getMessage("REQUIRED_ITEM"));
        utils.replace("%item_material%", item.getType().name());
        utils.replace("%item_amount%", String.valueOf(item.getAmount()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            utils.replace("%item_name%", meta.getDisplayName());
        }
        return utils.getMessage();
    }

    public int getAmount() {
        return item.getAmount();
    }

    public ItemStack getItem() {
        return item;
    }
}
