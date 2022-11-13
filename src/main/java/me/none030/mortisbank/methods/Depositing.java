package me.none030.mortisbank.methods;

import me.none030.mortisbank.MortisBank;
import me.none030.mortisbank.utils.DepositType;
import me.none030.mortisbank.utils.TransactionType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.methods.MoneyFormat.formatMoney;
import static me.none030.mortisbank.methods.StoringDatabaseData.*;
import static me.none030.mortisbank.methods.StoringYAMLData.*;
import static me.none030.mortisbank.methods.TransactionsRecords.StoreTransaction;
import static me.none030.mortisbank.methods.Upgrades.getLimit;

public class Depositing {

    public static void onDeposit(Player player, DepositType type) {

        Economy economy = MortisBank.getEconomy();
        int account;
        double bank;
        if (database) {
            account = getDatabaseAccount(player.getUniqueId());
            bank = getDatabaseBalance(player.getUniqueId());
        } else {
            account = getYamlAccount(player.getUniqueId());
            bank = getYamlBalance(player.getUniqueId());
        }
        double limit = getLimit(account);
        double purse = economy.getBalance(player.getName());
        double spaceInBank = limit - bank;

        if (purse < 1) {
            player.sendMessage("§cYou cannot deposit this little amount");
            return;
        }

        if (limit == bank) {
            player.sendMessage("§cYou already have the maximum amount of Coins in your bank!");
        } else {
            if (type.equals(DepositType.ALL)) {
                if (purse <= spaceInBank) {
                    economy.withdrawPlayer(player.getName(), purse);
                    bank = bank + purse;
                    if (database) {
                        ChangeDatabaseBalance(player.getUniqueId(), bank);
                    } else {
                        ChangeYamlBalance(player.getUniqueId(), bank);
                    }
                    player.sendMessage("§aDeposited §6" + formatMoney(purse) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
                    StoreTransaction(TransactionType.DEPOSIT, purse, player, player.getName());
                    player.closeInventory();
                } else {
                    economy.withdrawPlayer(player.getName(), spaceInBank);
                    bank = limit;
                    if (database) {
                        ChangeDatabaseBalance(player.getUniqueId(), bank);
                    } else {
                        ChangeYamlBalance(player.getUniqueId(), bank);
                    }
                    player.sendMessage("§aDeposited §6" + formatMoney(spaceInBank) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
                    StoreTransaction(TransactionType.DEPOSIT, spaceInBank, player, player.getName());
                    player.closeInventory();
                }
            }
            if (type.equals(DepositType.HALF)) {
                purse = purse / 2;
                if (purse <= spaceInBank) {
                    economy.withdrawPlayer(player.getName(), purse);
                    bank = bank + purse;
                    if (database) {
                        ChangeDatabaseBalance(player.getUniqueId(), bank);
                    } else {
                        ChangeYamlBalance(player.getUniqueId(), bank);
                    }
                    player.sendMessage("§aDeposited §6" + formatMoney(purse) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
                    StoreTransaction(TransactionType.DEPOSIT, purse, player, player.getName());
                    player.closeInventory();
                } else {
                    economy.withdrawPlayer(player.getName(), spaceInBank);
                    bank = limit;
                    if (database) {
                        ChangeDatabaseBalance(player.getUniqueId(), bank);
                    } else {
                        ChangeYamlBalance(player.getUniqueId(), bank);
                    }
                    player.sendMessage("§aDeposited §6" + formatMoney(spaceInBank) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
                    StoreTransaction(TransactionType.DEPOSIT, spaceInBank, player, player.getName());
                    player.closeInventory();
                }
            }
        }
    }

    public static void onDeposit(Player player, double purse) {

        Economy economy = MortisBank.getEconomy();
        int account;
        double bank;
        if (database) {
            account = getDatabaseAccount(player.getUniqueId());
            bank = getDatabaseBalance(player.getUniqueId());
        } else {
            account = getYamlAccount(player.getUniqueId());
            bank = getYamlBalance(player.getUniqueId());
        }
        double limit = getLimit(account);
        double spaceInBank = limit - bank;

        if (purse < 1) {
            player.sendMessage("§cYou cannot deposit this little amount");
            return;
        }

        if (limit == bank) {
            player.sendMessage("§cYou already have the maximum amount of Coins in your bank!");
        } else {
            if (purse <= spaceInBank) {
                economy.withdrawPlayer(player.getName(), purse);
                bank = bank + purse;
                if (database) {
                    ChangeDatabaseBalance(player.getUniqueId(), bank);
                } else {
                    ChangeYamlBalance(player.getUniqueId(), bank);
                }
                player.sendMessage("§aDeposited §6" + formatMoney(purse) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
                StoreTransaction(TransactionType.DEPOSIT, purse, player, player.getName());
                player.closeInventory();
            } else {
                economy.withdrawPlayer(player.getName(), spaceInBank);
                bank = limit;
                if (database) {
                    ChangeDatabaseBalance(player.getUniqueId(), bank);
                } else {
                    ChangeYamlBalance(player.getUniqueId(), bank);
                }
                player.sendMessage("§aDeposited §6" + formatMoney(spaceInBank) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
                StoreTransaction(TransactionType.DEPOSIT, spaceInBank, player, player.getName());
                player.closeInventory();
            }
        }
    }
}
