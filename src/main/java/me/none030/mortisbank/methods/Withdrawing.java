package me.none030.mortisbank.methods;

import me.none030.mortisbank.MortisBank;
import me.none030.mortisbank.api.BankTransactionEvent;
import me.none030.mortisbank.utils.TransactionType;
import me.none030.mortisbank.utils.WithdrawType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.none030.mortisbank.methods.DataMethods.ChangeBalance;
import static me.none030.mortisbank.methods.DataMethods.getBankBalance;
import static me.none030.mortisbank.methods.MoneyFormat.formatMoney;
import static me.none030.mortisbank.methods.StoringMessages.DepositMessage;
import static me.none030.mortisbank.methods.StoringMessages.WithdrawLittleAmountMessage;
import static me.none030.mortisbank.methods.TransactionsRecords.StoreTransaction;

public class Withdrawing {

    public static void onWithdraw(Player player, WithdrawType type) {

        Economy economy = MortisBank.getEconomy();
        double bank = getBankBalance(player.getUniqueId());

        if (bank < 1) {
            player.sendMessage(WithdrawLittleAmountMessage);
            return;
        }

        if (type.equals(WithdrawType.ALL)) {
            economy.depositPlayer(player.getName(), bank);
            ChangeBalance(player.getUniqueId(), 0);
            player.sendMessage(DepositMessage.replace("%money%", formatMoney(bank)).replace("%bank_balance%", formatMoney(0)));
            StoreTransaction(TransactionType.WITHDRAW, bank, player, player.getName());
            player.closeInventory();
            Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.WITHDRAW, bank));
        }
        if (type.equals(WithdrawType.HALF)) {
            bank = bank / 2;
            economy.depositPlayer(player.getName(), bank);
            ChangeBalance(player.getUniqueId(), bank);
            player.sendMessage(DepositMessage.replace("%money%", formatMoney(bank)).replace("%bank_balance%", formatMoney(bank)));
            player.sendMessage("§aWithdrew §6" + formatMoney(bank) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
            StoreTransaction(TransactionType.WITHDRAW, bank, player, player.getName());
            player.closeInventory();
            Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.WITHDRAW, bank));
        }
        if (type.equals(WithdrawType.TWENTY)) {
            double amount = bank * 0.20;
            economy.depositPlayer(player.getName(), amount);
            ChangeBalance(player.getUniqueId(), bank - amount);
            player.sendMessage(DepositMessage.replace("%money%", formatMoney(amount)).replace("%bank_balance%", formatMoney(bank - amount)));
            StoreTransaction(TransactionType.WITHDRAW, amount, player, player.getName());
            player.closeInventory();
            Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.WITHDRAW, amount));
        }
    }

    public static void onWithdraw(Player player, double amount) {

        Economy economy = MortisBank.getEconomy();
        double bank = getBankBalance(player.getUniqueId());

        if (bank < 1) {
            player.sendMessage(WithdrawLittleAmountMessage);
            return;
        }

        if (bank >= amount) {
            economy.depositPlayer(player.getName(), amount);
            ChangeBalance(player.getUniqueId(), bank - amount);
            player.sendMessage(DepositMessage.replace("%money%", formatMoney(amount)).replace("%bank_balance%", formatMoney(bank - amount)));
            StoreTransaction(TransactionType.WITHDRAW, amount, player, player.getName());
            player.closeInventory();
            Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.WITHDRAW, amount));
        }
    }
}
