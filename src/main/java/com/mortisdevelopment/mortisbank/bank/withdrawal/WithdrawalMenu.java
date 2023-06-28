package com.mortisdevelopment.mortisbank.bank.withdrawal;

import com.mortisdevelopment.mortisbank.bank.personal.PersonalMenu;
import com.mortisdevelopment.mortiscorespigot.utils.ItemEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class WithdrawalMenu implements InventoryHolder {

    private final WithdrawalManager withdrawalManager;
    private final Player player;
    private final Inventory menu;
    private final int everythingSlot = 10;
    private final int halfSlot = 12;
    private final int twentyPercentSlot = 14;
    private final int specificAmountSlot = 16;
    private final int backSlot = 31;

    public WithdrawalMenu(@NotNull WithdrawalManager withdrawalManager, @NotNull Player player) {
        this.withdrawalManager = withdrawalManager;
        this.player = player;
        this.menu = getMenu();
        update();
    }

    private Inventory getMenu() {
        Inventory menu = Bukkit.createInventory(this, 36, withdrawalManager.getMenu().getTitle());
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, withdrawalManager.getMenu().getItem("FILTER"));
        }
        return menu;
    }

    public void update() {
        ItemEditor whole = new ItemEditor(withdrawalManager.getMenu().getItem("EVERYTHING"));
        whole.setPlaceholders(player);
        menu.setItem(everythingSlot, whole.getItem());
        ItemEditor half = new ItemEditor(withdrawalManager.getMenu().getItem("HALF"));
        half.setPlaceholders(player);
        menu.setItem(halfSlot, half.getItem());
        ItemEditor twentyPercent = new ItemEditor(withdrawalManager.getMenu().getItem("TWENTY_PERCENT"));
        twentyPercent.setPlaceholders(player);
        menu.setItem(twentyPercentSlot, twentyPercent.getItem());
        ItemEditor specificAmount = new ItemEditor(withdrawalManager.getMenu().getItem("SPECIFIC_AMOUNT"));
        specificAmount.setPlaceholders(player);
        menu.setItem(specificAmountSlot, specificAmount.getItem());
        menu.setItem(backSlot, withdrawalManager.getMenu().getItem("BACK"));
    }

    public void click(int slot) {
        if (slot == everythingSlot) {
            withdrawalManager.withdraw(player, WithdrawalType.ALL);
            update();
        }
        if (slot == halfSlot) {
            withdrawalManager.withdraw(player, WithdrawalType.ALL);
            update();
        }
        if (slot == twentyPercentSlot) {
            withdrawalManager.withdraw(player, WithdrawalType.TWENTY);
            update();
        }
        if (slot == specificAmountSlot) {
            withdrawalManager.getGuiSettings().sendGui(player, withdrawalManager);
        }
        if (slot == backSlot) {
            PersonalMenu menu = new PersonalMenu(withdrawalManager.getBankManager().getPersonalManager(), player);
            menu.open();
        }
    }

    public void open() {
        player.openInventory(menu);
    }

    public void close() {
        player.closeInventory();
    }

    public WithdrawalManager getWithdrawalManager() {
        return withdrawalManager;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    public int getEverythingSlot() {
        return everythingSlot;
    }

    public int getHalfSlot() {
        return halfSlot;
    }

    public int getTwentyPercentSlot() {
        return twentyPercentSlot;
    }

    public int getSpecificAmountSlot() {
        return specificAmountSlot;
    }

    public int getBackSlot() {
        return backSlot;
    }
}
