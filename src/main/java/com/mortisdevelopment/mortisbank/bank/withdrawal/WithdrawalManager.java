package com.mortisdevelopment.mortisbank.bank.withdrawal;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.bank.personal.TransactionType;
import com.mortisdevelopment.mortisbank.utils.GuiSettings;
import com.mortisdevelopment.mortiscorespigot.menus.Menu;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class WithdrawalManager extends Manager {

    private final MortisBank plugin = MortisBank.getInstance();
    private final Economy economy = plugin.getEconomy();
    private final BankManager bankManager;
    private final Menu menu;
    private final GuiSettings guiSettings;

    public WithdrawalManager(@NotNull BankManager bankManager, @NotNull Menu menu, @NotNull GuiSettings guiSettings) {
        this.bankManager = bankManager;
        this.menu = menu;
        this.guiSettings = guiSettings;
    }

    private void sendMessage(@NotNull OfflinePlayer offlinePlayer, String message) {
        if (message == null) {
            return;
        }
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

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, @NotNull WithdrawalType type) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        double bank = bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (bank <= 0) {
            sendMessage(offlinePlayer, getMessage("LITTLE"));
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
        bankManager.getDataManager().setBalance(offlinePlayer.getUniqueId(), bank);
        sendMessage(offlinePlayer, getMessage("WITHDRAW", offlinePlayer).replace("%money%", formatter.format(purse)));
        addTransaction(offlinePlayer, formatter.format(purse));
        return true;
    }

    public boolean withdraw(@NotNull OfflinePlayer offlinePlayer, double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (amount <= 0) {
            sendMessage(offlinePlayer, getMessage("GREATER_THAN_ZERO"));
            return false;
        }
        double bank = bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
        if (bank <= 0) {
            sendMessage(offlinePlayer, getMessage("LITTLE"));
            return false;
        }
        if (amount > bank) {
            sendMessage(offlinePlayer, getMessage("NO_MONEY"));
            return false;
        }
        bank -= amount;
        economy.depositPlayer(offlinePlayer, amount);
        bankManager.getDataManager().setBalance(offlinePlayer.getUniqueId(), bank);
        sendMessage(offlinePlayer, getMessage("WITHDRAW", offlinePlayer).replace("%money%", formatter.format(amount)));
        addTransaction(offlinePlayer, formatter.format(amount));
        return true;
    }

    public double getEverything(@NotNull OfflinePlayer offlinePlayer) {
        return bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId());
    }

    public double getHalf(@NotNull OfflinePlayer offlinePlayer) {
        return bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId()) / 2;
    }

    public double getTwentyPercent(@NotNull OfflinePlayer offlinePlayer) {
        return bankManager.getDataManager().getBalance(offlinePlayer.getUniqueId()) * 0.20;
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
