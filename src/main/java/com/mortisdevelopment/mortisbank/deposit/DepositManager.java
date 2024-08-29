package com.mortisdevelopment.mortisbank.deposit;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.personal.TransactionType;
import com.mortisdevelopment.mortisbank.utils.GuiSettings;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.ColorUtils;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Getter
public class DepositManager extends Manager {

    public enum DepositType {
        ALL,
        HALF,
        SPECIFIC
    }
    private final MortisBank plugin;
    private final Economy economy;
    private final CustomMenu menu;
    private final GuiSettings guiSettings;

    public DepositManager(MortisBank plugin, @NotNull CustomMenu menu, GuiSettings guiSettings) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
        this.menu = menu;
        this.guiSettings = guiSettings;
    }

    private Placeholder getPlaceholder(double purse) {
        Placeholder placeholder = new Placeholder();
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%amount%", NumberUtils.format(purse));
        method.addReplacement("%amount_raw%", String.valueOf(purse));
        method.addReplacement("%amount_formatted%", NumberUtils.getMoney(purse));
        placeholder.addMethod(method);
        return placeholder;
    }

    private void sendTransactionMessage(@NotNull Player player, double purse) {
        player.sendMessage(ColorUtils.getComponent(getPlaceholder(purse).setPlaceholders(getSimplePlaceholderMessage(player, "deposit"))));
    }

    private void addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull String amount) {
        String transactor;
        if (offlinePlayer.getName() != null) {
            transactor = offlinePlayer.getName();
        }else {
            transactor = offlinePlayer.getUniqueId().toString();
        }
        PersonalTransaction transaction = new PersonalTransaction(TransactionType.DEPOSIT, amount, transactor);
        plugin.getDataManager().addTransaction(offlinePlayer.getUniqueId(), transaction);
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, @NotNull DepositType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(DepositType.SPECIFIC)) {
            if (player == null) {
                return false;
            }
            guiSettings.sendGui(this, player, new Placeholder(player));
            return true;
        }
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        Account account = plugin.getAccountManager().getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = plugin.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            if (player != null) {
                sendPlaceholderMessage(player, "full");
            }
            return false;
        }
        double purse = economy.getBalance(offlinePlayer);
        if (type.equals(DepositType.HALF)) {
            purse = purse / 2;
        }
        if (purse <= 0) {
            if (player != null) {
                sendPlaceholderMessage(player, "little");
            }
            return false;
        }
        double bank = balance + purse;
        if (!account.isStorable(bank)) {
            purse = account.getSpace(balance);
            bank = balance + purse;
        }
        economy.withdrawPlayer(offlinePlayer, purse);
        plugin.getDataManager().setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(purse));
        if (player != null) {
            sendTransactionMessage(player, purse);
        }
        return true;
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (amount <= 0) {
            if (player != null) {
                sendPlaceholderMessage(player, "greater_than_zero");
            }
            return false;
        }
        Account account = plugin.getAccountManager().getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = plugin.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            if (player != null) {
                sendPlaceholderMessage(player, "full");
            }
            return false;
        }
        double purse = economy.getBalance(offlinePlayer);
        if (amount < purse) {
            purse = amount;
        }
        if (purse <= 0) {
            if (player != null) {
                sendPlaceholderMessage(player, "little");
            }
            return false;
        }
        double bank = balance + purse;
        if (!account.isStorable(bank)) {
            purse = account.getSpace(balance);
            bank = balance + purse;
        }
        economy.withdrawPlayer(offlinePlayer, purse);
        plugin.getDataManager().setBalance(offlinePlayer.getUniqueId(), bank);
        addTransaction(offlinePlayer, formatter.format(purse));
        if (player != null) {
            sendTransactionMessage(player, purse);
        }
        return true;
    }

    public double getWhole(@NotNull OfflinePlayer player) {
        Account account = plugin.getAccountManager().getAccount(player);
        if (account == null) {
            return 0;
        }
        double purse = economy.getBalance(player);
        if (purse == 0) {
            return 0;
        }
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public double getWhole(@NotNull Account account, double balance, double purse) {
        if (purse == 0 || account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public double getHalf(@NotNull OfflinePlayer player) {
        Account account = plugin.getAccountManager().getAccount(player);
        if (account == null) {
            return 0;
        }
        double purse = economy.getBalance(player) / 2;
        if (purse == 0) {
            return 0;
        }
        double balance = plugin.getDataManager().getBalance(player.getUniqueId());
        if (account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }

    public double getHalf(@NotNull Account account, double balance, double purse) {
        purse = purse / 2;
        if (purse == 0 || account.isFull(balance)) {
            return 0;
        }
        double space = account.getSpace(balance);
        return Math.min(purse, space);
    }
}
