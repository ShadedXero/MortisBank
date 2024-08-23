package com.mortisdevelopment.mortisbank.utils;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import de.rapha149.signgui.SignGUI;
import lombok.Getter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

@Getter
public class GuiSettings {

    private final MortisBank plugin;
    private final InputMode mode;
    private final int inputSlot;
    private final ItemStack anvilItem;

    public GuiSettings(MortisBank plugin, @NotNull InputMode mode, int inputSlot, ItemStack anvilItem) {
        this.plugin = plugin;
        this.mode = mode;
        this.inputSlot = inputSlot;
        this.anvilItem = anvilItem;
    }

    public void sendGui(@NotNull Manager manager, @NotNull Player player, Placeholder placeholder) {
        if (mode.equals(InputMode.SIGN)) {
            sendSignGui(manager, player, placeholder);
        }else {
            sendAnvilGui(manager, player, placeholder);
        }
    }

    private void sendSignGui(@NotNull Manager manager, @NotNull Player player, Placeholder placeholder) {
        SignGUI.builder()
                .setLines(manager.getSimpleMessage("sign_1"), manager.getSimpleMessage("sign_2"), manager.getSimpleMessage("sign_3"), manager.getSimpleMessage("sign_4"))
                .callHandlerSynchronously(plugin)
                .setHandler((p, result) -> {
                    String line = result.getLine(0);
                    if (line == null) {
                        return null;
                    }
                    double amount = NumberUtils.getMoney(line);
                    if (amount == 0) {
                        return null;
                    }
                    if (manager instanceof DepositManager) {
                        if (((DepositManager) manager).deposit(player, amount)) {
                            plugin.getPersonalManager().getMenu().open(player, placeholder);
                        }
                    }
                    if (manager instanceof WithdrawalManager) {
                        if (((WithdrawalManager) manager).withdraw(player, amount)) {
                            plugin.getPersonalManager().getMenu().open(player, placeholder);
                        }
                    }
                    return null;
                })
                .build()
                .open(player);
    }

    private void sendAnvilGui(@NotNull Manager manager, @NotNull Player player, Placeholder placeholder) {
        new AnvilGUI.Builder()
                .onClose(state -> {
                    double amount = NumberUtils.getMoney(state.getText());
                    if (amount == 0) {
                        return;
                    }
                    if (manager instanceof DepositManager) {
                        if (((DepositManager) manager).deposit(player, amount)) {
                            plugin.getPersonalManager().getMenu().open(player, placeholder);
                        }
                    }
                    if (manager instanceof WithdrawalManager) {
                        if (((WithdrawalManager) manager).withdraw(player, amount)) {
                            plugin.getPersonalManager().getMenu().open(player, placeholder);
                        }
                    }
                })
                .onClick((slot, state) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    double amount = NumberUtils.getMoney(state.getText());
                    if (amount == 0) {
                        return Collections.emptyList();
                    }
                    if (manager instanceof DepositManager) {
                        if (((DepositManager) manager).deposit(player, amount)) {
                            plugin.getPersonalManager().getMenu().open(player, new Placeholder(player));
                        }
                    }
                    if (manager instanceof WithdrawalManager) {
                        if (((WithdrawalManager) manager).withdraw(player, amount)) {
                            plugin.getPersonalManager().getMenu().open(player, new Placeholder(player));
                        }
                    }
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .text(manager.getSimpleMessage("anvil_text"))
                .itemLeft(anvilItem)
                .title(manager.getSimpleMessage("anvil_title"))
                .plugin(plugin)
                .open(player);
    }
}
