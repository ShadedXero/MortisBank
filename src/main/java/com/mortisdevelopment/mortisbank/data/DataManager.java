package com.mortisdevelopment.mortisbank.data;

import com.google.common.reflect.TypeToken;
import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.transactions.Transaction;
import com.mortisdevelopment.mortiscore.databases.Database;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter
public class DataManager {

    private final MortisBank plugin;
    private final Database database;
    private final boolean leaderboard;
    private final int transactionLimit;
    private final HashMap<UUID, Double> balanceByUUID;
    private final HashMap<UUID, Short> accountByUUID;
    private final HashMap<UUID, List<Transaction>> transactionsByUUID;

    public DataManager(MortisBank plugin, @NotNull Database database, boolean leaderboard, int transactionLimit) {
        this.plugin = plugin;
        this.database = database;
        this.leaderboard = leaderboard;
        this.transactionLimit = transactionLimit;
        this.balanceByUUID = new HashMap<>();
        this.accountByUUID = new HashMap<>();
        this.transactionsByUUID = new HashMap<>();
        initializeDatabases();
        loadDatabase();
    }

    private void initializeDatabases() {
        Bukkit.getScheduler().runTask(plugin,
                () -> database.execute("CREATE TABLE IF NOT EXISTS MortisBank(uuid varchar(36) primary key, balance double, account smallint, transactions mediumtext)"));
    }

    private void loadDatabase() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ResultSet result = database.query("SELECT * FROM MortisBank");
                while (result.next()) {
                    UUID uuid = UUID.fromString(result.getString("uuid"));
                    double balance = result.getDouble("balance");
                    short account = result.getShort("account");
                    List<Transaction> transactions = getTransactions(result.getString("transactions"));
                    balanceByUUID.put(uuid, balance);
                    accountByUUID.put(uuid, account);
                    transactionsByUUID.put(uuid, transactions);
                }
            }catch (SQLException exp) {
                throw new RuntimeException(exp);
            }
        });
    }

    private List<Transaction> trimTransactions(@NotNull List<Transaction> transactions) {
        if (transactions.size() <= transactionLimit) {
            return transactions;
        }
        int amount = transactions.size() - transactionLimit;
        if (amount > 0) {
            transactions.subList(0, amount).clear();
        }
        return transactions;
    }

    private List<String> getRawTransactions(@NotNull List<Transaction> transactions) {
        List<String> rawTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            rawTransactions.add(transaction.getRawTransaction());
        }
        return rawTransactions;
    }

    private String getTransactions(@NotNull List<Transaction> transactions) {
        List<String> rawTransactions = getRawTransactions(transactions);
        return gson.toJson(rawTransactions);
    }

    private List<Transaction> getTransactions(@NotNull String raw) {
        List<Transaction> transactionList = new ArrayList<>();
        List<String> rawTransactions = gson.fromJson(raw, new TypeToken<List<String>>(){}.getType());
        for (String rawTransaction : rawTransactions) {
            Transaction transaction = new Transaction(rawTransaction);
            if (!transaction.isValid()) {
                continue;
            }
            transactionList.add(transaction);
        }
        return transactionList;
    }

    private List<UUID> getSortedUUIDs() {
        List<UUID> sortedUUIDs = new ArrayList<>(balanceByUUID.keySet());
        sortedUUIDs.sort(Comparator.<UUID>comparingDouble(balanceByUUID::get).reversed());
        return sortedUUIDs;
    }

    public UUID getUUID(int position) {
        List<UUID> uuids = getSortedUUIDs();
        if (position >= uuids.size()) {
            return null;
        }
        return uuids.get(position);
    }

    public boolean hasPlayer(@NotNull UUID uuid) {
        return balanceByUUID.containsKey(uuid);
    }

    public void addBank(@NotNull UUID uuid, double balance, short account, @NotNull List<Transaction> transactions) {
        balanceByUUID.put(uuid, balance);
        accountByUUID.put(uuid, account);
        transactionsByUUID.put(uuid, transactions);
        Bukkit.getScheduler().runTask(plugin,
                () -> database.update("INSERT INTO MortisBank (uuid, balance, account, transactions) VALUES (?, ?, ?, ?)", uuid.toString(), balance, account, getTransactions(transactions)));
    }

    public void setBalance(@NotNull UUID uuid, double balance) {
        balanceByUUID.put(uuid, Math.max(balance, 0));
        Bukkit.getScheduler().runTask(plugin, () -> database.update("UPDATE MortisBank SET balance = ? WHERE uuid = ?", balance, uuid.toString()));
    }

    public void setAccount(@NotNull UUID uuid, short account) {
        accountByUUID.put(uuid, account);
        Bukkit.getScheduler().runTask(plugin, () -> database.update("UPDATE MortisBank SET account = ? WHERE uuid = ?", account, uuid.toString()));
    }

    public void setTransactions(@NotNull UUID uuid, @NotNull List<Transaction> transactions) {
        List<Transaction> transactionList = trimTransactions(transactions);
        transactionsByUUID.put(uuid, transactionList);
        Bukkit.getScheduler().runTask(plugin, () -> database.update("UPDATE MortisBank SET transactions = ? WHERE uuid = ?", getTransactions(transactionList), uuid.toString()));
    }

    public void addTransaction(@NotNull UUID uuid, @NotNull Transaction transaction) {
        List<Transaction> transactions = getTransactions(uuid);
        transactions.add(transaction);
        List<Transaction> transactionList = trimTransactions(transactions);
        transactionsByUUID.put(uuid, transactionList);
        Bukkit.getScheduler().runTask(plugin, () -> database.update("UPDATE MortisBank SET transactions = ? WHERE uuid = ?", getTransactions(transactionList), uuid.toString()));
    }

    public void removeBank(@NotNull UUID uuid) {
        Bukkit.getScheduler().runTask(plugin, () -> database.update("DELETE FROM MortisBank WHERE uuid = ?", uuid.toString()));
    }

    public double getBalance(@NotNull UUID uuid) {
        return balanceByUUID.get(uuid);
    }

    public short getAccount(@NotNull UUID uuid) {
        return accountByUUID.get(uuid);
    }

    public List<Transaction> getTransactions(@NotNull UUID uuid) {
        return transactionsByUUID.get(uuid);
    }
}
