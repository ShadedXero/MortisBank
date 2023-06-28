package com.mortisdevelopment.mortisbank.bank.accounts;

import com.mortisdevelopment.mortisbank.bank.personal.PersonalMenu;
import com.mortisdevelopment.mortiscorespigot.utils.ItemEditor;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class AccountMenu implements InventoryHolder {

    private final AccountManager accountManager;
    private final Player player;
    private final Inventory menu;

    public AccountMenu(@NotNull AccountManager accountManager, @NotNull Player player) {
        this.accountManager = accountManager;
        this.player = player;
        this.menu = getMenu();
        update();
    }

    private Inventory getMenu() {
        Inventory menu = Bukkit.createInventory(this, accountManager.getMenu().getSize(), accountManager.getMenu().getTitle());
        for (int i = 0; i < menu.getSize(); i++) {
            menu.setItem(i, accountManager.getMenu().getItem("FILTER"));
        }
        return menu;
    }

    public void update() {
        menu.setItem(accountManager.getSettings().getBackSlot(), accountManager.getMenu().getItem("BACK"));
        Account playerAccount = accountManager.getAccount(player);
        if (playerAccount == null) {
            return;
        }
        for (Account account : accountManager.getAccounts()) {
            ItemEditor editor = new ItemEditor(account.getIcon());
            editor.setPlaceholders(player);
            menu.setItem(account.getIconSlot(), editor.getItem());
            if (playerAccount.getPriority() == account.getPriority()) {
                editor.setPlaceholder("%account_status%", accountManager.getMessage("YOUR_ACCOUNT"));
                continue;
            }
            if (playerAccount.getPriority() > account.getPriority()) {
                editor.setPlaceholder("%account_status%", accountManager.getMessage("BETTER_ACCOUNT"));
                continue;
            }
            if (playerAccount.getPriority() < account.getPriority()) {
                editor.setPlaceholder("%account_status%", accountManager.getMessage("NEED_PREVIOUS_ACCOUNT"));
            }
        }
    }

    public void click(int slot) {
        if (slot == accountManager.getSettings().getBackSlot()) {
            PersonalMenu menu = new PersonalMenu(accountManager.getBankManager().getPersonalManager(), player);
            menu.open();
        }
        Account account = accountManager.getAccount(slot);
        if (account == null) {
            return;
        }
        Account playerAccount = accountManager.getAccount(player);
        if (playerAccount == null) {
            accountManager.sendMessage("NO_REQUIREMENTS", player);
            return;
        }
        Account nextAccount = accountManager.getNextAccount(playerAccount.getPriority());
        if (nextAccount == null || !nextAccount.equals(account) || !account.hasRequirements(player)) {
            accountManager.sendMessage("NO_REQUIREMENTS", player);
            return;
        }
        account.removeRequirements(player);
        accountManager.getBankManager().getDataManager().setAccount(player.getUniqueId(), account.getPriority());
        MessageUtils utils = new MessageUtils(accountManager.getMessage("UPGRADED", player));
        utils.replace("%account_name%", account.getName());
        player.sendMessage(utils.getMessage());
    }

    public void open() {
        player.openInventory(menu);
    }

    public void close() {
        player.closeInventory();
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return menu;
    }
}
