package com.mortisdevelopment.mortisbank.transactions;

import com.mortisdevelopment.mortiscore.databases.Database;
import com.mortisdevelopment.mortiscore.messages.Messages;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class TransactionManager {

    private final JavaPlugin plugin;
    private final Database database;
    private final TransactionSettings settings;
    private final Messages messages;
    private final HashMap<UUID, List<Transaction>> transactionsByPlayer = new HashMap<>();

    public TransactionManager(JavaPlugin plugin, Database database, TransactionSettings settings, Messages messages) {
        this.plugin = plugin;
        this.database = database;
        this.settings = settings;
        this.messages = messages;
        initialize();
    }

    private void initialize() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            database.execute("CREATE TABLE IF NOT EXISTS BankTransactions(id varchar(36), uniqueId varchar(36), transaction mediumtext)");
            ResultSet result = database.query("SELECT * FROM BankTransactions");
            try {
                while (result.next()) {
                    String id = result.getString("id");
                    UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                    String rawTransaction = result.getString("transaction");
                    transactionsByPlayer.computeIfAbsent(uniqueId, k -> new ArrayList<>()).add(Transaction.deserialize(id, uniqueId, rawTransaction));
                }
            }catch (SQLException exp) {
                throw new RuntimeException(exp);
            }
        });
    }

    public void addTransaction(Transaction transaction) {
        List<Transaction> transactions = transactionsByPlayer.computeIfAbsent(transaction.getUniqueId(), k -> new ArrayList<>());
        transactions.add(transaction);
        if (transactions.size() > 1) {
            transactions.sort(Comparator.comparing(Transaction::getTime));
            List<Transaction> transactionsToRemove = new ArrayList<>();
            while (transactions.size() > settings.getTransactionLimit()) {
                transactionsToRemove.add(transactions.removeLast());
            }
            for (Transaction toRemove : transactionsToRemove) {
                removeTransaction(toRemove, false);
            }
        }
        transactionsByPlayer.put(transaction.getUniqueId(), transactions);
        Bukkit.getScheduler().runTask(plugin, () -> database.update("INSERT INTO BankTransactions(id, player, transaction) (?, ?, ?)", transaction.getId(), transaction.getUniqueId(), transaction.getRawTransaction()));
    }

    public void removeTransaction(Transaction transaction, boolean modifyCache) {
        if (modifyCache) {
            transactionsByPlayer.computeIfAbsent(transaction.getUniqueId(), k -> new ArrayList<>()).remove(transaction);
        }
        Bukkit.getScheduler().runTask(plugin, () -> database.update("DELETE FROM BankTransactions WHERE id = ?", transaction.getId()));
    }

    public void removeTransaction(Transaction transaction) {
        removeTransaction(transaction, true);
    }

    public List<Transaction> getTransactions(@NotNull UUID uuid) {
        return transactionsByPlayer.getOrDefault(uuid, new ArrayList<>());
    }

    public Transaction getTransaction(@NotNull OfflinePlayer player, int position) {
        if (position < 1 || position > settings.getTransactionLimit()) {
            return null;
        }
        position--;
        List<Transaction> transactions = getTransactions(player.getUniqueId());
        if (position >= transactions.size()) {
            return null;
        }
        return transactions.get(settings.getTransactionLimit() - (position + 1));
    }

    public void addTransaction(@NotNull OfflinePlayer offlinePlayer, @NotNull Transaction.TransactionType type, double amount, @NotNull String transactor) {
        addTransaction(new Transaction(offlinePlayer.getUniqueId(), messages, type, amount, transactor));
    }

    public boolean removeTransaction(@NotNull OfflinePlayer offlinePlayer, int position) {
        if (position < 1 || position > settings.getTransactionLimit()) {
            return false;
        }
        position--;
        List<Transaction> transactions = getTransactions(offlinePlayer.getUniqueId());
        if (position >= transactions.size()) {
            return false;
        }
        Transaction transaction = transactions.remove(position);
        if (transaction == null) {
            return false;
        }
        removeTransaction(transaction, false);
        transactionsByPlayer.put(transaction.getUniqueId(), transactions);
        return true;
    }

    public boolean clearTransactions(@NotNull UUID uuid) {
        if (transactionsByPlayer.remove(uuid) == null) {
            return false;
        }
        Bukkit.getScheduler().runTask(plugin, () -> database.update("DELETE FROM BankTransactions WHERE uniqueId = ?", uuid));
        return true;
    }
}