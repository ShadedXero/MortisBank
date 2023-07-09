package com.mortisdevelopment.mortisbank.bank.personal;

import com.mortisdevelopment.mortisbank.bank.accounts.AccountMenu;
import com.mortisdevelopment.mortisbank.bank.deposit.DepositMenu;
import com.mortisdevelopment.mortisbank.bank.withdrawal.WithdrawalMenu;
import com.mortisdevelopment.mortiscorespigot.utils.ItemEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PersonalMenu implements InventoryHolder {

    private final PersonalManager personalManager;
    private final Player player;
    private final Inventory menu;
    private final int depositSlot = 11;
    private final int withdrawalSlot = 13;
    private final int transactionSlot = 15;
    private final int closeSlot = 31;
    private final int infoSlot = 32;
    private final int accountSlot = 35;

    public PersonalMenu(@NotNull PersonalManager personalManager, @NotNull Player player) {
        this.personalManager = personalManager;
        this.player = player;
        this.menu = getMenu();
        update();
    }

    private Inventory getMenu() {
        Inventory menu = Bukkit.createInventory(this, 36, personalManager.getMenu().getTitle());
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, personalManager.getMenu().getItem("FILTER"));
        }
        return menu;
    }

    public void update() {
        menu.setItem(depositSlot, ItemEditor.setPlaceholders(personalManager.getMenu().getItem("DEPOSIT"), player));
        menu.setItem(withdrawalSlot, ItemEditor.setPlaceholders(personalManager.getMenu().getItem("WITHDRAWAL"), player));
        menu.setItem(closeSlot, ItemEditor.setPlaceholders(personalManager.getMenu().getItem("CLOSE"), player));
        menu.setItem(infoSlot, ItemEditor.setPlaceholders(personalManager.getMenu().getItem("INFORMATION"), player));
        menu.setItem(accountSlot, ItemEditor.setPlaceholders(personalManager.getMenu().getItem("ACCOUNTS"), player));
        ItemEditor editor = new ItemEditor(personalManager.getMenu().getItem("TRANSACTIONS"));
        editor.setPlaceholders(player);
        List<PersonalTransaction> transactions = personalManager.getBankManager().getDataManager().getTransactions(player.getUniqueId());
        if (transactions != null && transactions.size() != 0) {
            for (PersonalTransaction transaction : transactions) {
                if (!transaction.isValid()) {
                    continue;
                }
                editor.addLore(transaction.getTransaction(personalManager));
            }
            menu.setItem(transactionSlot, editor.getItem());
        }else {
            menu.setItem(transactionSlot, ItemEditor.setPlaceholders(personalManager.getMenu().getItem("NO_TRANSACTIONS"), player));
        }
    }

    public void click(int slot) {
        if (slot == depositSlot) {
            DepositMenu menu = new DepositMenu(personalManager.getBankManager().getDepositManager(), player);
            menu.open();
        }
        if (slot == withdrawalSlot) {
            WithdrawalMenu menu = new WithdrawalMenu(personalManager.getBankManager().getWithdrawalManager(), player);
            menu.open();
        }
        if (slot == accountSlot) {
            AccountMenu menu = new AccountMenu(personalManager.getBankManager().getAccountManager(), player);
            menu.open();
        }
        if (slot == closeSlot) {
            close();
        }
    }

    public void open() {
        player.openInventory(menu);
    }

    public void close() {
        player.closeInventory();
    }

    public PersonalManager getPersonalManager() {
        return personalManager;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }

    public int getDepositSlot() {
        return depositSlot;
    }

    public int getWithdrawalSlot() {
        return withdrawalSlot;
    }

    public int getTransactionSlot() {
        return transactionSlot;
    }

    public int getCloseSlot() {
        return closeSlot;
    }

    public int getInfoSlot() {
        return infoSlot;
    }

    public int getAccountSlot() {
        return accountSlot;
    }
}
