package me.none030.mortisbank.methods;

import me.none030.mortisbank.MortisBank;
import me.none030.mortisbank.utils.TransactionType;
import me.none030.mortisbank.utils.WithdrawType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.methods.MoneyFormat.formatMoney;
import static me.none030.mortisbank.methods.StoringDatabaseData.ChangeDatabaseBalance;
import static me.none030.mortisbank.methods.StoringDatabaseData.getDatabaseBalance;
import static me.none030.mortisbank.methods.StoringYAMLData.ChangeYamlBalance;
import static me.none030.mortisbank.methods.StoringYAMLData.getYamlBalance;
import static me.none030.mortisbank.methods.TransactionsRecords.StoreTransaction;

public class Withdrawing {

    public static void onWithdraw(Player player, WithdrawType type) {

        Economy economy = MortisBank.getEconomy();
        double bank;
        if (database) {
            bank = getDatabaseBalance(player.getUniqueId());
        } else {
            bank = getYamlBalance(player.getUniqueId());
        }

        if (bank < 1) {
            player.sendMessage("§cYou cannot withdraw this little amount");
            return;
        }

        if (type.equals(WithdrawType.ALL)) {
            economy.depositPlayer(player.getName(), bank);
            if (database) {
                ChangeDatabaseBalance(player.getUniqueId(), 0);
            } else {
                ChangeYamlBalance(player.getUniqueId(), 0);
            }
            player.sendMessage("§aWithdrew §6" + formatMoney(bank) + " coins§a! There's now §6" + formatMoney(0) + " coins §ain the account!");
            StoreTransaction(TransactionType.WITHDRAW, bank, player, player.getName());
            player.closeInventory();
        }
        if (type.equals(WithdrawType.HALF)) {
            bank = bank / 2;
            economy.depositPlayer(player.getName(), bank);
            if (database) {
                ChangeDatabaseBalance(player.getUniqueId(), bank);
            } else {
                ChangeYamlBalance(player.getUniqueId(), bank);
            }
            player.sendMessage("§aWithdrew §6" + formatMoney(bank) + " coins§a! There's now §6" + formatMoney(bank) + " coins §ain the account!");
            StoreTransaction(TransactionType.WITHDRAW, bank, player, player.getName());
            player.closeInventory();
        }
        if (type.equals(WithdrawType.TWENTY)) {
            double amount = bank * 0.20;
            economy.depositPlayer(player.getName(), amount);
            if (database) {
                ChangeDatabaseBalance(player.getUniqueId(), bank - amount);
            } else {
                ChangeYamlBalance(player.getUniqueId(), bank - amount);
            }
            player.sendMessage("§aWithdrew §6" + formatMoney(amount) + " coins§a! There's now §6" + formatMoney(bank - amount) + " coins §ain the account!");
            StoreTransaction(TransactionType.WITHDRAW, amount, player, player.getName());
            player.closeInventory();
        }
    }

    public static void onWithdraw(Player player, double amount) {

        Economy economy = MortisBank.getEconomy();
        double bank;
        if (database) {
            bank = getDatabaseBalance(player.getUniqueId());
        } else {
            bank = getYamlBalance(player.getUniqueId());
        }

        if (bank < 1) {
            player.sendMessage("§cYou cannot withdraw this little amount");
            return;
        }

        if (bank >= amount) {
            economy.depositPlayer(player.getName(), amount);
            if (database) {
                ChangeDatabaseBalance(player.getUniqueId(), bank - amount);
            } else {
                ChangeYamlBalance(player.getUniqueId(), bank - amount);
            }
            player.sendMessage("§aWithdrew §6" + formatMoney(amount) + " coins§a! There's now §6" + formatMoney(bank - amount) + " coins §ain the account!");
            StoreTransaction(TransactionType.WITHDRAW, amount, player, player.getName());
            player.closeInventory();
        }
    }
}
