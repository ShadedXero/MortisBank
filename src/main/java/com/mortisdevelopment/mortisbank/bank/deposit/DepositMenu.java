package com.mortisdevelopment.mortisbank.bank.deposit;

import com.mortisdevelopment.mortisbank.bank.personal.PersonalMenu;
import com.mortisdevelopment.mortiscorespigot.utils.ItemEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class DepositMenu implements InventoryHolder {

    private final DepositManager depositManager;
    private final Player player;
    private final Inventory menu;
    private final int wholeSlot = 11;
    private final int halfSlot = 13;
    private final int specificAmountSlot = 15;
    private final int backSlot = 31;

    public DepositMenu(@NotNull DepositManager depositManager, @NotNull Player player) {
        this.depositManager = depositManager;
        this.player = player;
        this.menu = getMenu();
        update();
    }

    private Inventory getMenu() {
        Inventory menu = Bukkit.createInventory(this, 36, depositManager.getMenu().getTitle());
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, depositManager.getMenu().getItem("FILTER"));
        }
        return menu;
    }

    public void update() {
        ItemEditor whole = new ItemEditor(depositManager.getMenu().getItem("WHOLE"));
        whole.setPlaceholders(player);
        menu.setItem(wholeSlot, whole.getItem());
        ItemEditor half = new ItemEditor(depositManager.getMenu().getItem("HALF"));
        half.setPlaceholders(player);
        menu.setItem(halfSlot, half.getItem());
        ItemEditor specificAmount = new ItemEditor(depositManager.getMenu().getItem("SPECIFIC_AMOUNT"));
        specificAmount.setPlaceholders(player);
        menu.setItem(specificAmountSlot, specificAmount.getItem());
        menu.setItem(backSlot, ItemEditor.setPlaceholders(depositManager.getMenu().getItem("BACK"), player));
    }

    public void click(int slot) {
        if (slot == wholeSlot) {
            if (depositManager.deposit(player, DepositType.ALL)) {
                PersonalMenu menu = new PersonalMenu(depositManager.getBankManager().getPersonalManager(), player);
                menu.open();
            }
            update();
        }
        if (slot == halfSlot) {
            if (depositManager.deposit(player, DepositType.HALF)) {
                PersonalMenu menu = new PersonalMenu(depositManager.getBankManager().getPersonalManager(), player);
                menu.open();
            }
            update();
        }
        if (slot == specificAmountSlot) {
            depositManager.getGuiSettings().sendGui(player, depositManager);
        }
        if (slot == backSlot) {
            PersonalMenu menu = new PersonalMenu(depositManager.getBankManager().getPersonalManager(), player);
            menu.open();
        }
    }

    public void open() {
        player.openInventory(menu);
    }

    public void close() {
        player.closeInventory();
    }

    public DepositManager getDepositManager() {
        return depositManager;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    public int getWholeSlot() {
        return wholeSlot;
    }

    public int getHalfSlot() {
        return halfSlot;
    }

    public int getSpecificAmountSlot() {
        return specificAmountSlot;
    }

    public int getBackSlot() {
        return backSlot;
    }
}
