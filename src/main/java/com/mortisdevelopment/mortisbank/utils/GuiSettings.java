package com.mortisdevelopment.mortisbank.utils;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.bank.deposit.DepositMenu;
import com.mortisdevelopment.mortisbank.bank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortisbank.bank.withdrawal.WithdrawalMenu;
import com.mortisdevelopment.mortiscorespigot.managers.CoreManager;
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

    public void sendGui(@NotNull Player player, @NotNull CoreManager manager) {
        if (mode.equals(InputMode.SIGN)) {
            sendSignGui(player, manager);
        }else {
            sendAnvilGui(player, manager);
        }
    }

    private void sendSignGui(@NotNull Player player, @NotNull CoreManager manager) {
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
                        ((DepositManager) manager).deposit(player, amount);
                        DepositMenu menu = new DepositMenu((DepositManager) manager, player);
                        menu.open();
                    }
                    if (manager instanceof WithdrawalManager) {
                        ((WithdrawalManager) manager).withdraw(player, amount);
                        WithdrawalMenu menu = new WithdrawalMenu((WithdrawalManager) manager, player);
                        menu.open();
                    }
                })
                .withLines(Arrays.asList(manager.getMessage("SIGN_LINE_1"), manager.getMessage("SIGN_LINE_2"), manager.getMessage("SIGN_LINE_3"), manager.getMessage("SIGN_LINE_4")))
                .uuid(player.getUniqueId())
                .plugin(plugin)
                .build()
                .open();
    }

    private void sendAnvilGui(@NotNull Player player, @NotNull CoreManager manager) {
        new AnvilGUI.Builder()
                .onClose(state -> {
                    double amount = MoneyUtils.getMoney(state.getText());
                    if (amount == 0) {
                        return;
                    }
                    if (manager instanceof DepositManager) {
                        ((DepositManager) manager).deposit(player, amount);
                        DepositMenu menu = new DepositMenu((DepositManager) manager, player);
                        menu.open();
                    }
                    if (manager instanceof WithdrawalManager) {
                        ((WithdrawalManager) manager).withdraw(player, amount);
                        WithdrawalMenu menu = new WithdrawalMenu((WithdrawalManager) manager, player);
                        menu.open();
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
