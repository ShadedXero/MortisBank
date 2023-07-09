package com.mortisdevelopment.mortisbank.utils;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortisbank.bank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalMenu;
import com.mortisdevelopment.mortisbank.bank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortiscorespigot.menus.signgui.SignGui;
import com.mortisdevelopment.mortiscorespigot.utils.MoneyUtils;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GuiSettings {

    private final MortisBank plugin = MortisBank.getInstance();
    private final InputMode mode;
    private final int inputSlot;
    private final ItemStack anvilItem;

    public GuiSettings(@NotNull InputMode mode, int inputSlot, ItemStack anvilItem) {
        this.mode = mode;
        this.inputSlot = inputSlot;
        this.anvilItem = anvilItem;
    }

    public void sendGui(@NotNull Player player, @NotNull Manager manager) {
        if (mode.equals(InputMode.SIGN)) {
            sendSignGui(player, manager);
        }else {
            sendAnvilGui(player, manager);
        }
    }

    private void sendSignGui(@NotNull Player player, @NotNull Manager manager) {
        SignGui.builder()
                .action(e -> {
                    List<String> lines = e.getLines();
                    if (lines == null) {
                        return;
                    }
                    double amount = MoneyUtils.getMoney(lines.get(inputSlot));
                    if (amount == 0) {
                        return;
                    }
                    if (manager instanceof DepositManager) {
                        if (((DepositManager) manager).deposit(player, amount)) {
                            PersonalMenu menu = new PersonalMenu(((DepositManager) manager).getBankManager().getPersonalManager(), player);
                            menu.open();
                        }
                    }
                    if (manager instanceof WithdrawalManager) {
                        if (((WithdrawalManager) manager).withdraw(player, amount)) {
                            PersonalMenu menu = new PersonalMenu(((WithdrawalManager) manager).getBankManager().getPersonalManager(), player);
                            menu.open();
                        }
                    }
                })
                .withLines(Arrays.asList(manager.getMessage("SIGN_1"), manager.getMessage("SIGN_2"), manager.getMessage("SIGN_3"), manager.getMessage("SIGN_4")))
                .uuid(player.getUniqueId())
                .plugin(plugin)
                .build()
                .open();
    }

    private void sendAnvilGui(@NotNull Player player, @NotNull Manager manager) {
        new AnvilGUI.Builder()
                .onClick((state, handler) -> null)
                .onClose(state -> {
                    double amount = MoneyUtils.getMoney(state.getText());
                    if (amount == 0) {
                        return;
                    }
                    if (manager instanceof DepositManager) {
                        if (((DepositManager) manager).deposit(player, amount)) {
                            PersonalMenu menu = new PersonalMenu(((DepositManager) manager).getBankManager().getPersonalManager(), player);
                            menu.open();
                        }
                    }
                    if (manager instanceof WithdrawalManager) {
                        if (((WithdrawalManager) manager).withdraw(player, amount)) {
                            PersonalMenu menu = new PersonalMenu(((WithdrawalManager) manager).getBankManager().getPersonalManager(), player);
                            menu.open();
                        }
                    }
                })
                .text(manager.getMessage("ANVIL_TEXT"))
                .itemLeft(anvilItem)
                .title(manager.getMessage("ANVIL_TITLE"))
                .plugin(plugin)
                .open(player);
    }

    public InputMode getMode() {
        return mode;
    }

    public int getInputSlot() {
        return inputSlot;
    }

    public ItemStack getAnvilItem() {
        return anvilItem;
    }
}
