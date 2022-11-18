package me.none030.mortisbank.methods;

import me.none030.mortisbank.MortisBank;
import me.none030.mortisbank.api.BankTransactionEvent;
import me.none030.mortisbank.utils.DepositType;
import me.none030.mortisbank.utils.TransactionType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.none030.mortisbank.methods.DataMethods.*;
import static me.none030.mortisbank.methods.MoneyFormat.formatMoney;
import static me.none030.mortisbank.methods.StoringMessages.*;
import static me.none030.mortisbank.methods.TransactionsRecords.StoreTransaction;
import static me.none030.mortisbank.methods.Upgrades.getLimit;

public class Depositing {

    public static void onDeposit(Player player, DepositType type) {

        Economy economy = MortisBank.getEconomy();
        int account = getBankAccount(player.getUniqueId());
        double bank = getBankBalance(player.getUniqueId());
        double limit = getLimit(account);
        double purse = getBankPurse(player.getUniqueId());
        double spaceInBank = limit - bank;

        if (purse < 1) {
            player.sendMessage(DepositLittleAmountMessage);
            return;
        }

        if (limit == bank) {
            player.sendMessage(MaxCoinsMessage);
        } else {
            if (type.equals(DepositType.ALL)) {
                if (purse <= spaceInBank) {
                    economy.withdrawPlayer(player.getName(), purse);
                    bank = bank + purse;
                    ChangeBankBalance(player.getUniqueId(), bank);
                    player.sendMessage(DepositMessage.replace("%money%", formatMoney(purse)).replace("%bank_balance%", formatMoney(bank)));
                    StoreTransaction(TransactionType.DEPOSIT, purse, player, player.getName());
                    player.closeInventory();
                    Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.DEPOSIT, purse));
                } else {
                    economy.withdrawPlayer(player.getName(), spaceInBank);
                    bank = limit;
                    ChangeBankBalance(player.getUniqueId(), bank);
                    player.sendMessage(DepositMessage.replace("%money%", formatMoney(spaceInBank)).replace("%bank_balance%", formatMoney(bank)));
                    StoreTransaction(TransactionType.DEPOSIT, spaceInBank, player, player.getName());
                    player.closeInventory();
                    Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.DEPOSIT, spaceInBank));
                }
            }
            if (type.equals(DepositType.HALF)) {
                purse = purse / 2;
                if (purse <= spaceInBank) {
                    economy.withdrawPlayer(player.getName(), purse);
                    bank = bank + purse;
                    ChangeBankBalance(player.getUniqueId(), bank);
                    player.sendMessage(DepositMessage.replace("%money%", formatMoney(purse)).replace("%bank_balance%", formatMoney(bank)));
                    StoreTransaction(TransactionType.DEPOSIT, purse, player, player.getName());
                    player.closeInventory();
                    Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.DEPOSIT, purse));
                } else {
                    economy.withdrawPlayer(player.getName(), spaceInBank);
                    bank = limit;
                    ChangeBankBalance(player.getUniqueId(), bank);
                    player.sendMessage(DepositMessage.replace("%money%", formatMoney(spaceInBank)).replace("%bank_balance%", formatMoney(bank)));
                    StoreTransaction(TransactionType.DEPOSIT, spaceInBank, player, player.getName());
                    player.closeInventory();
                    Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.DEPOSIT, spaceInBank));
                }
            }
        }
    }

    public static void onDeposit(Player player, double purse) {

        Economy economy = MortisBank.getEconomy();
        int account = getBankAccount(player.getUniqueId());
        double bank = getBankBalance(player.getUniqueId());
        double limit = getLimit(account);
        double spaceInBank = limit - bank;

        if (purse < 1) {
            player.sendMessage(DepositLittleAmountMessage);
            return;
        }

        if (limit == bank) {
            player.sendMessage(MaxCoinsMessage);
        } else {
            if (purse <= spaceInBank) {
                economy.withdrawPlayer(player.getName(), purse);
                bank = bank + purse;
                ChangeBankBalance(player.getUniqueId(), bank);
                player.sendMessage(DepositMessage.replace("%money%", formatMoney(purse)).replace("%bank_balance%", formatMoney(bank)));
                StoreTransaction(TransactionType.DEPOSIT, purse, player, player.getName());
                player.closeInventory();
                Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.DEPOSIT, purse));
            } else {
                economy.withdrawPlayer(player.getName(), spaceInBank);
                bank = limit;
                ChangeBankBalance(player.getUniqueId(), bank);
                player.sendMessage(DepositMessage.replace("%money%", formatMoney(spaceInBank)).replace("%bank_balance%", formatMoney(bank)));
                StoreTransaction(TransactionType.DEPOSIT, spaceInBank, player, player.getName());
                player.closeInventory();
                Bukkit.getServer().getPluginManager().callEvent(new BankTransactionEvent(player, TransactionType.DEPOSIT, spaceInBank));
            }
        }
    }
}
