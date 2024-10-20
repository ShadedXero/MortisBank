package com.mortisdevelopment.mortisbank.transactions;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.databases.Database;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.utils.ConfigUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter @Setter
public class TransactionManager extends Manager<MortisBank> {

    private final Database database;
    private final Messages messages;
    private TransactionSettings settings;
    private final HashMap<UUID, List<Transaction>> transactionsByPlayer = new HashMap<>();
    private boolean initialized;

    public TransactionManager(Database database, Messages messages) {
        this.database = database;
        this.messages = messages;
    }

    @Override
    public void reload(MortisBank plugin) {
        File file = ConfigUtils.getFile(plugin, "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.settings = getTransactionSettings(config);
        if (!initialized) {
            initialize(plugin);
            initialized = true;
        }
    }

    private TransactionSettings getTransactionSettings(ConfigurationSection section) {
        return new TransactionSettings(section.getInt("transaction-limit"));
    }

    private void initialize(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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
            }finally {
                for (List<Transaction> transactions : transactionsByPlayer.values()) {
                    if (transactions.size() > 1) {
                        sort(transactions);
                    }
                }
            }
        });
    }

    private void sort(List<Transaction> transactions) {
        transactions.sort(Comparator.comparing(Transaction::getTime).reversed());
    }

    public void addTransaction(JavaPlugin plugin, Transaction transaction) {
        List<Transaction> transactions = transactionsByPlayer.computeIfAbsent(transaction.getUniqueId(), k -> new ArrayList<>());
        transactions.add(transaction);
        if (transactions.size() > 1) {
            sort(transactions);
            List<Transaction> transactionsToRemove = new ArrayList<>();
            while (transactions.size() > settings.getTransactionLimit()) {
                transactionsToRemove.add(transactions.remove(transactions.size() - 1));
            }
            for (Transaction toRemove : transactionsToRemove) {
                removeTransaction(plugin, toRemove, false);
            }
        }
        transactionsByPlayer.put(transaction.getUniqueId(), transactions);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("INSERT INTO BankTransactions(id, uniqueId, transaction) VALUES (?, ?, ?)", transaction.getId(), transaction.getUniqueId(), transaction.getRawTransaction()));
    }

    public void removeTransaction(JavaPlugin plugin, Transaction transaction, boolean modifyCache) {
        if (modifyCache) {
            transactionsByPlayer.computeIfAbsent(transaction.getUniqueId(), k -> new ArrayList<>()).remove(transaction);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("DELETE FROM BankTransactions WHERE id = ?", transaction.getId()));
    }

    public void removeTransaction(JavaPlugin plugin, Transaction transaction) {
        removeTransaction(plugin, transaction, true);
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
        return transactions.get(position);
    }

    public void addTransaction(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, @NotNull Transaction.TransactionType type, double amount, @NotNull String transactor) {
        addTransaction(plugin, new Transaction(offlinePlayer.getUniqueId(), messages, type, amount, transactor));
    }

    public boolean removeTransaction(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, int position) {
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
        removeTransaction(plugin, transaction, false);
        transactionsByPlayer.put(transaction.getUniqueId(), transactions);
        return true;
    }

    public boolean clearTransactions(JavaPlugin plugin, @NotNull UUID uuid) {
        if (transactionsByPlayer.remove(uuid) == null) {
            return false;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("DELETE FROM BankTransactions WHERE uniqueId = ?", uuid));
        return true;
    }
}