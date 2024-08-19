package com.mortisdevelopment.mortisbank.withdrawal;

import com.mortisdevelopment.mortisbank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.personal.TransactionType;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.utils.GuiSettings;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Getter
public class WithdrawalManager extends Manager {

    public enum WithdrawalType {
        ALL,
        HALF,
        TWENTY,
        SPECIFIC
    }
    private final Economy economy;
    private final DataManager dataManager;
    private final CustomMenu menu;
    private final GuiSettings guiSettings;

    public WithdrawalManager(Economy economy, DataManager dataManager, @NotNull CustomMenu menu, @NotNull GuiSettings guiSettings) {
        this.economy = economy;
        this.dataManager = dataManager;
        this.menu = menu;
        this.guiSettings = guiSettings;
    }

    private Placeholder getTransactionPlaceholder(double purse) {
        Placeholder placeholder = new Placeholder();
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%amount%", NumberUtils.format(purse));
        method.addReplacement("%amount_raw%", String.valueOf(purse));
        method.addReplacement("%amount_formatted%", NumberUtils.getMoney(purse));
        placeholder.addMethod(method);
        return placeholder;
    }

    private void sendTransactionMessage(@NotNull Player player, double purse) {
        player.sendMessage(getTransactionPlaceholder(purse).setPlaceholders(getSimplePlaceholderMessage(player, "withdraw")));
    }

    private void addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull String amount) {
        String transactor;
        if (offlinePlayer.getName() != null) {
            transactor = offlinePlayer.getName();
        }else {
            transactor = offlinePlayer.getUniqueId().toString();
        }
        PersonalTransaction transaction = new PersonalTransaction(TransactionType.WITHDRAW, amount, transactor);
        dataManager.addTransaction(offlinePlayer.getUniqueId(), transaction);
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, @NotNull WithdrawalType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(WithdrawalType.SPECIFIC)) {
            if (player == null) {
                return false;
            }
            guiSettings.sendGui(this, player, new Placeholder(player));
            return true;
        }
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        double bank = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (bank <= 0) {
            if (player != null) {
                sendPlaceholderMessage(player, "little");
            }
            return false;
        }
        double purse = 0;
        if (type.equals(WithdrawalType.ALL)) {
            purse = bank;
            bank = 0;
        }
        if (type.equals(WithdrawalType.HALF)) {
            purse = bank / 2;
            bank /= 2;
        }
        if (type.equals(WithdrawalType.TWENTY)) {
            purse = bank * 0.20;
            bank *= 0.80;
        }
        economy.depositPlayer(offlinePlayer, purse);
        dataManager.setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(purse));
        if (player != null) {
            sendTransactionMessage(player, purse);
        }
        return true;
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (amount <= 0) {
            if (player != null) {
                sendPlaceholderMessage(player, "greater_than_zero");
            }
            return false;
        }
        double bank = dataManager.getBalance(offlinePlayer.getUniqueId());
        if (bank <= 0) {
            if (player != null) {
                sendPlaceholderMessage(player, "little");
            }
            return false;
        }
        if (amount > bank) {
            if (player != null) {
                sendPlaceholderMessage(player, "no_money");
            }
            return false;
        }
        bank -= amount;
        economy.depositPlayer(offlinePlayer, amount);
        dataManager.setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(amount));
        if (player != null) {
            sendTransactionMessage(player, amount);
        }
        return true;
    }

    public double getEverything(@NotNull OfflinePlayer offlinePlayer) {
        return dataManager.getBalance(offlinePlayer.getUniqueId());
    }

    public double getHalf(@NotNull OfflinePlayer offlinePlayer) {
        return dataManager.getBalance(offlinePlayer.getUniqueId()) / 2;
    }

    public double getTwentyPercent(@NotNull OfflinePlayer offlinePlayer) {
        return dataManager.getBalance(offlinePlayer.getUniqueId()) * 0.20;
    }
}
