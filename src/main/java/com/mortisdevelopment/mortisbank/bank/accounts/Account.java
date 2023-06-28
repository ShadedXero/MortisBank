package com.mortisdevelopment.mortisbank.bank.accounts;

import com.mortisdevelopment.mortisbank.bank.accounts.requirements.AccountRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.MoneyRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.upgrades.InterestUpgrade;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.ItemRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.PermissionRequirement;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class Account {

    private final String id;
    private final short priority;
    private final String name;
    private final ItemStack icon;
    private final int iconSlot;
    private final double maxBalance;
    private final List<InterestUpgrade> interests;
    private final List<AccountRequirement> requirements;

    public Account(@NotNull String id, short priority, @NotNull String name, @NotNull ItemStack icon, int iconSlot, double maxBalance, @NotNull List<InterestUpgrade> interests, @NotNull List<AccountRequirement> requirements) {
        this.id = id;
        this.priority = priority;
        this.name = name;
        this.icon = icon;
        this.iconSlot = iconSlot;
        this.maxBalance = maxBalance;
        this.interests = interests;
        this.requirements = requirements;
    }

    public boolean isPriority(short priority) {
        return this.priority == priority;
    }

    public boolean isStorable(double amount) {
        return amount <= maxBalance;
    }

    public boolean isFull(double amount) {
        return amount >= maxBalance;
    }

    public double getSpace(double amount) {
        return maxBalance - amount;
    }

    public double getInterest(double balance) {
        double interest = 0;
        for (InterestUpgrade upgrade : interests) {
            if (upgrade.hasRequirement(balance)) {
                interest += upgrade.getInterest();
            }
        }
        return interest;
    }

    public boolean hasRequirements(@NotNull Player player) {
        for (AccountRequirement requirement : requirements) {
            if (!requirement.hasRequirement(player)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasRequirements(@NotNull AccountManager accountManager,@NotNull Player player) {
        DecimalFormat formatter = new DecimalFormat("###,###.#");
        for (AccountRequirement requirement : requirements) {
            if (!requirement.hasRequirement(player)) {
                if (requirement instanceof MoneyRequirement) {
                    double money = ((MoneyRequirement) requirement).getMoney();
                    player.sendMessage(accountManager.getMessage("REQUIRE_MONEY").replace("%amount%", formatter.format(money)));
                    return false;
                }
                if (requirement instanceof PermissionRequirement) {
                    String permission = ((PermissionRequirement) requirement).getPermission();
                    player.sendMessage(accountManager.getMessage("REQUIRE_PERMISSION").replace("%permission%", permission));
                    return false;
                }
                if (requirement instanceof ItemRequirement) {
                    ItemStack item = ((ItemRequirement) requirement).getItem();
                    int amount = ((ItemRequirement) requirement).getAmount();
                    MessageUtils utils = new MessageUtils(accountManager.getMessage("REQUIRE_ITEM"));
                    utils.replace("%material%", item.getType().name());
                    utils.replace("%amount%", String.valueOf(amount));
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        utils.replace("%name%", meta.getDisplayName());
                    }
                    player.sendMessage(utils.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public void removeRequirements(@NotNull Player player) {
        for (AccountRequirement requirement : requirements) {
            requirement.removeRequirement(player);
        }
    }

    public void removeRequirements(@NotNull AccountManager accountManager, @NotNull Player player) {
        DecimalFormat formatter = new DecimalFormat("###,###.#");
        for (AccountRequirement requirement : requirements) {
            requirement.removeRequirement(player);
            if (requirement instanceof MoneyRequirement) {
                double money = ((MoneyRequirement) requirement).getMoney();
                player.sendMessage(accountManager.getMessage("REMOVED_MONEY").replace("%amount%", formatter.format(money)));
            }
            if (requirement instanceof ItemRequirement) {
                ItemStack item = ((ItemRequirement) requirement).getItem();
                int amount = ((ItemRequirement) requirement).getAmount();
                MessageUtils utils = new MessageUtils(accountManager.getMessage("REMOVED_ITEM"));
                utils.replace("%material%", item.getType().name());
                utils.replace("%amount%", String.valueOf(amount));
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    utils.replace("%name%", meta.getDisplayName());
                }
                player.sendMessage(utils.getMessage());
            }
        }
    }

    public String getId() {
        return id;
    }

    public short getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public int getIconSlot() {
        return iconSlot;
    }

    public double getMaxBalance() {
        return maxBalance;
    }

    public List<InterestUpgrade> getInterests() {
        return interests;
    }

    public List<AccountRequirement> getRequirements() {
        return requirements;
    }
}
