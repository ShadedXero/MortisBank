package com.mortisdevelopment.mortisbank.bank.deposit;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.accounts.Account;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.bank.personal.TransactionType;
import com.mortisdevelopment.mortisbank.utils.GuiSettings;
import com.mortisdevelopment.mortiscorespigot.menus.Menu;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import com.mortisdevelopment.mortiscorespigot.utils.MoneyUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class DepositManager extends Manager {

    private final MortisBank plugin = MortisBank.getInstance();
    private final Economy economy = plugin.getEconomy();
    private final BankManager bankManager;
    private final Menu menu;
    private final GuiSettings guiSettings;

    public DepositManager(@NotNull BankManager bankManager, @NotNull Menu menu, GuiSettings guiSettings) {
        this.bankManager = bankManager;
        this.menu = menu;
        this.guiSettings = guiSettings;
        plugin.getServer().getPluginManager().registerEvents(new DepositListener(), plugin);
    }

    private void sendMessage(@NotNull OfflinePlayer offlinePlayer, String message) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(message);
    }

    private void addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull String amount) {
        String transactor;
        if (offlinePlayer.getName() != null) {
            transactor = offlinePlayer.getName();
        }else {
            transactor = offlinePlayer.getUniqueId().toString();
        }
        PersonalTransaction transaction = new PersonalTransaction(TransactionType.DEPOSIT, amount, transactor);
        bankManager.getDataManager().addTransaction(offlinePlayer.getUniqueId(), transaction);
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, @NotNull DepositType type) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        Account account = bankManager.getAccountManager().getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            sendMessage(offlinePlayer, getMessage("FULL"));
            return false;
        }
        double purse = economy.getBalance(offlinePlayer);
        if (type.equals(DepositType.HALF)) {
            purse = purse / 2;
        }
        if (purse <= 0) {
            sendMessage(offlinePlayer, getMessage("LITTLE"));
            return false;
        }
        double bank = balance + purse;
        if (!account.isStorable(bank)) {
            purse = account.getSpace(balance);
            bank = balance + purse;
        }
        economy.withdrawPlayer(offlinePlayer, purse);
        bankManager.getDataManager().setBalance(offlinePlayer.getUniqueId(), bank);
        MessageUtils utils = new MessageUtils(getMessage("DEPOSIT", offlinePlayer));
        utils.replace("%amount%", formatter.format(purse));
        utils.replace("%amount_raw%", String.valueOf(purse));
        utils.replace("%amount_formatted%", MoneyUtils.getMoney(purse));
        sendMessage(offlinePlayer, utils.getMessage());
        addTransaction(offlinePlayer, formatter.format(purse));
        return true;
    }

    public boolean deposit(@NotNull OfflinePlayer offlinePlayer, double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (amount <= 0) {
            sendMessage(offlinePlayer, getMessage("GREATER_THAN_ZERO"));
            return false;
        }
        Account account = bankManager.getAccountManager().getAccount(offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            sendMessage(offlinePlayer, getMessage("FULL"));
            return false;
        }
        double purse = economy.getBalance(offlinePlayer);
        if (amount < purse) {
            purse = amount;
        }
        if (purse <= 0) {
            sendMessage(offlinePlayer, getMessage("LITTLE"));
            return false;
        }
        double bank = balance + purse;
        if (!account.isStorable(bank)) {
            purse = account.getSpace(balance);
            bank = balance + purse;
        }
        economy.withdrawPlayer(offlinePlayer, purse);
        bankManager.getDataManager().setBalance(offlinePlayer.getUniqueId(), bank);
        MessageUtils utils = new MessageUtils(getMessage("DEPOSIT", offlinePlayer));
        utils.replace("%amount%", formatter.format(purse));
        utils.replace("%amount_raw%", String.valueOf(purse));
        utils.replace("%amount_formatted%", MoneyUtils.getMoney(purse));
        sendMessage(offlinePlayer, utils.getMessage());
        addTransaction(offlinePlayer, formatter.format(purse));
        return true;
    }

    public double getWhole(@NotNull OfflinePlayer player) {
        Account account = bankManager.getAccountManager().getAccount(player);
        if (account == null) {
            return 0;
        }
        double purse = economy.getBalance(player);
        if (purse == 0) {
            return 0;
        }
        double balance = bankManager.getDataManager().getBalance(player.getUniqueId());
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
        Account account = bankManager.getAccountManager().getAccount(player);
        if (account == null) {
            return 0;
        }
        double purse = economy.getBalance(player) / 2;
        if (purse == 0) {
            return 0;
        }
        double balance = bankManager.getDataManager().getBalance(player.getUniqueId());
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

    public BankManager getBankManager() {
        return bankManager;
    }

    public Menu getMenu() {
        return menu;
    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }
}
