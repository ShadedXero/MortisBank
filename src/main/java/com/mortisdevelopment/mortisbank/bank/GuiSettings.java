package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.personal.TransactionType;
import com.mortisdevelopment.mortiscore.items.CustomItem;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import de.rapha149.signgui.SignGUI;
import lombok.Getter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

@Getter
public class GuiSettings {

    private final MortisBank plugin;
    private final InputMode mode;
    private final int inputSlot;
    private final CustomItem anvilItem;

    public GuiSettings(MortisBank plugin, @NotNull InputMode mode, int inputSlot, CustomItem anvilItem) {
        this.plugin = plugin;
        this.mode = mode;
        this.inputSlot = inputSlot - 1;
        this.anvilItem = anvilItem;
    }

    public void open(BankManager bankManager, @NotNull Player player, TransactionType type, Placeholder placeholder) {
        switch (mode) {
            case SIGN -> openSign(bankManager, player, type, placeholder);
            case ANVIL -> openAnvil(bankManager, player, type, placeholder);
        }
    }

    public void openSign(BankManager bankManager, @NotNull Player player, TransactionType type, Placeholder placeholder) {
        Messages messages = bankManager.getMessages(type);
        SignGUI.builder()
                .setLines(messages.getSimpleMessage("sign_1"), messages.getSimpleMessage("sign_2"), messages.getSimpleMessage("sign_3"), messages.getSimpleMessage("sign_4"))
                .callHandlerSynchronously(plugin)
                .setHandler((p, result) -> {
                    String line = result.getLine(inputSlot);
                    if (line == null) {
                        return null;
                    }
                    double amount = NumberUtils.getMoney(line);
                    if (amount == 0) {
                        return null;
                    }
                    switch (type) {
                        case DEPOSIT -> bankManager.deposit(player, amount);
                        case WITHDRAW -> bankManager.withdraw(player, amount);
                    }
                    plugin.getPersonalManager().getMenu().open(player, placeholder);
                    return null;
                })
                .build()
                .open(player);
    }

    public void openAnvil(BankManager bankManager, @NotNull Player player, TransactionType type, Placeholder placeholder) {
        Messages messages = bankManager.getMessages(type);
        new AnvilGUI.Builder()
                .onClose(state -> {
                    double amount = NumberUtils.getMoney(state.getText());
                    if (amount == 0) {
                        return;
                    }
                    switch (type) {
                        case DEPOSIT -> bankManager.deposit(player, amount);
                        case WITHDRAW -> bankManager.withdraw(player, amount);
                    }
                    plugin.getPersonalManager().getMenu().open(player, placeholder);
                })
                .onClick((slot, state) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    double amount = NumberUtils.getMoney(state.getText());
                    if (amount == 0) {
                        return Collections.emptyList();
                    }
                    switch (type) {
                        case DEPOSIT -> bankManager.deposit(player, amount);
                        case WITHDRAW -> bankManager.withdraw(player, amount);
                    }
                    plugin.getPersonalManager().getMenu().open(player, placeholder);
                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                })
                .text(messages.getSimplePlaceholderMessage("anvil_text", placeholder))
                .itemLeft(anvilItem.getItem(placeholder))
                .title(messages.getSimplePlaceholderMessage("anvil_title", placeholder))
                .plugin(plugin)
                .open(player);
    }
}
