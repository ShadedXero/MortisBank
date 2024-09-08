package com.mortisdevelopment.mortisbank.transactions;

import com.mortisdevelopment.mortisbank.data.DataManager;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TransactionManager {

    private final DataManager dataManager;

    public TransactionManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Transaction getTransaction(@NotNull OfflinePlayer player, int position) {
        List<Transaction> transactions = dataManager.getTransactions(player.getUniqueId());
        if (position >= transactions.size()) {
            return null;
        }
        position = dataManager.getTransactionLimit() - (position + 1);
        return transactions.get(position);
    }

    public boolean addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull Transaction.TransactionType type, @NotNull String amount, @NotNull String transactor) {
        Transaction transaction = new Transaction(type, amount, transactor);
        if (!transaction.isValid()) {
            return false;
        }
        dataManager.addTransaction(offlinePlayer.getUniqueId(), transaction);
        return true;
    }

    public boolean clearTransaction(@NotNull OfflinePlayer offlinePlayer, int position) {
        List<Transaction> transactions = dataManager.getTransactions(offlinePlayer.getUniqueId());
        if (transactions == null || position >= transactions.size()) {
            return false;
        }
        transactions.remove(position);
        dataManager.setTransactions(offlinePlayer.getUniqueId(), transactions);
        return true;
    }

    public boolean clearTransactions(@NotNull OfflinePlayer offlinePlayer) {
        dataManager.setTransactions(offlinePlayer.getUniqueId(), new ArrayList<>());
        return true;
    }
}