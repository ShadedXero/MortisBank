package com.mortisdevelopment.mortisbank.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalTransaction;
import com.mortisdevelopment.mortiscorespigot.databases.Database;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DataManager {

    private final MortisBank plugin = MortisBank.getInstance();
    private final Gson gson = new Gson();
    private final Database database;
    private final boolean leaderboard;
    private final int transactionLimit;
    private final HashMap<UUID, Double> balanceByUUID;
    private final HashMap<UUID, Short> accountByUUID;
    private final HashMap<UUID, LocalDateTime> interestByUUID;
    private final HashMap<UUID, List<PersonalTransaction>> transactionsByUUID;

    public DataManager(@NotNull Database database, boolean leaderboard, int transactionLimit) {
        this.database = database;
        this.leaderboard = leaderboard;
        this.transactionLimit = transactionLimit;
        this.balanceByUUID = new HashMap<>();
        this.accountByUUID = new HashMap<>();
        this.interestByUUID = new HashMap<>();
        this.transactionsByUUID = new HashMap<>();
        initializeDatabases();
        loadDatabase();
    }

    private void initializeDatabases() {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.execute("CREATE TABLE IF NOT EXISTS MortisBank(uuid varchar(36) primary key, balance double, account smallint, interest tinytext, transactions mediumtext)");
            }
        }.runTask(plugin);
    }

    private void loadDatabase() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet result = database.query("SELECT * FROM MortisBank");
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        double balance = result.getDouble("balance");
                        short account = result.getShort("account");
                        String rawInterest = result.getString("interest");
                        LocalDateTime interest;
                        try {
                            interest = LocalDateTime.parse(rawInterest);
                        }catch (DateTimeParseException exp) {
                            interest = null;
                        }
                        List<PersonalTransaction> transactions = getTransactions(result.getString("transactions"));
                        balanceByUUID.put(uuid, balance);
                        accountByUUID.put(uuid, account);
                        interestByUUID.put(uuid, interest);
                        transactionsByUUID.put(uuid, transactions);
                    }
                }catch (SQLException exp) {
                    exp.printStackTrace();
                }
            }
        }.runTask(plugin);
    }

    private List<PersonalTransaction> trimTransactions(@NotNull List<PersonalTransaction> transactions) {
        if (transactions.size() <= transactionLimit) {
            return transactions;
        }
        int amount = transactions.size() - transactionLimit;
        if (amount > 0) {
            transactions.subList(0, amount).clear();
        }
        return transactions;
    }

    private List<String> getRawTransactions(@NotNull List<PersonalTransaction> transactions) {
        List<String> rawTransactions = new ArrayList<>();
        for (PersonalTransaction transaction : transactions) {
            rawTransactions.add(transaction.getRawTransaction());
        }
        return rawTransactions;
    }

    private String getTransactions(@NotNull List<PersonalTransaction> transactions) {
        List<String> rawTransactions = getRawTransactions(transactions);
        return gson.toJson(rawTransactions);
    }

    private List<PersonalTransaction> getTransactions(@NotNull String raw) {
        List<PersonalTransaction> transactionList = new ArrayList<>();
        List<String> rawTransactions = gson.fromJson(raw, new TypeToken<List<String>>(){}.getType());
        for (String rawTransaction : rawTransactions) {
            PersonalTransaction transaction = new PersonalTransaction(rawTransaction);
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

    public void addBank(@NotNull UUID uuid, double balance, short account, @NotNull LocalDateTime interest, @NotNull List<PersonalTransaction> transactions) {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("INSERT INTO MortisBank (uuid, balance, account, interest, transactions) VALUES (?, ?, ?, ?, ?)", uuid.toString(), balance, account, interest.toString(), getTransactions(transactions));
                balanceByUUID.put(uuid, balance);
                accountByUUID.put(uuid, account);
                interestByUUID.put(uuid, interest);
                transactionsByUUID.put(uuid, transactions);
            }
        }.runTask(plugin);
    }

    public void setBalance(@NotNull UUID uuid, double balance) {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("UPDATE MortisBank SET balance = ? WHERE uuid = ?", balance, uuid.toString());
            }
        }.runTask(plugin);
        balanceByUUID.put(uuid, balance);
    }

    public void setAccount(@NotNull UUID uuid, short account) {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("UPDATE MortisBank SET account = ? WHERE uuid = ?", account, uuid.toString());
            }
        }.runTask(plugin);
        accountByUUID.put(uuid, account);
    }

    public void setInterest(@NotNull UUID uuid, LocalDateTime interest) {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("UPDATE MortisBank SET interest = ? WHERE uuid = ?", interest.toString(), uuid.toString());
            }
        }.runTask(plugin);
        interestByUUID.put(uuid, interest);
    }

    public void setTransactions(@NotNull UUID uuid, @NotNull List<PersonalTransaction> transactions) {
        List<PersonalTransaction> transactionList = trimTransactions(transactions);
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("UPDATE MortisBank SET transactions = ? WHERE uuid = ?", getTransactions(transactionList), uuid.toString());
            }
        }.runTask(plugin);
        transactionsByUUID.put(uuid, transactionList);
    }

    public void addTransaction(@NotNull UUID uuid, @NotNull PersonalTransaction transaction) {
        List<PersonalTransaction> transactions = getTransactions(uuid);
        transactions.add(transaction);
        List<PersonalTransaction> transactionList = trimTransactions(transactions);
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("UPDATE MortisBank SET transactions = ? WHERE uuid = ?", getTransactions(transactionList), uuid.toString());
            }
        }.runTask(plugin);
        transactionsByUUID.put(uuid, transactionList);
    }

    public void removeBank(@NotNull UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                database.update("DELETE FROM MortisBank WHERE uuid = ?", uuid.toString());
            }
        }.runTask(plugin);
    }

    public double getBalance(@NotNull UUID uuid) {
        return balanceByUUID.get(uuid);
    }

    public short getAccount(@NotNull UUID uuid) {
        return accountByUUID.get(uuid);
    }

    public LocalDateTime getInterest(@NotNull UUID uuid) {
        return interestByUUID.get(uuid);
    }

    public List<PersonalTransaction> getTransactions(@NotNull UUID uuid) {
        return transactionsByUUID.get(uuid);
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isLeaderboard() {
        return leaderboard;
    }

    public int getTransactionLimit() {
        return transactionLimit;
    }

    public HashMap<UUID, Double> getBalanceByUUID() {
        return balanceByUUID;
    }

    public HashMap<UUID, Short> getAccountByUUID() {
        return accountByUUID;
    }

    public HashMap<UUID, LocalDateTime> getInterestByUUID() {
        return interestByUUID;
    }

    public HashMap<UUID, List<PersonalTransaction>> getTransactionsByUUID() {
        return transactionsByUUID;
    }
}
