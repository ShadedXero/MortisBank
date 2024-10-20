package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.transactions.Transaction;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.currencies.Currency;
import com.mortisdevelopment.mortiscore.databases.Database;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.items.CustomItem;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholders.Placeholder;
import com.mortisdevelopment.mortiscore.utils.ConfigUtils;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Getter @Setter
public class BankManager extends Manager<MortisBank> {

    public enum DepositType {
        ALL,
        HALF,
        SPECIFIC
    }
    public enum WithdrawalType {
        ALL,
        HALF,
        TWENTY,
        SPECIFIC
    }

    private final AccountManager accountManager;
    private final TransactionManager transactionManager;
    private final Database database;
    private final Messages depositMessages;
    private final Messages withdrawalMessages;
    private BankSettings settings;
    private CustomMenu personalMenu;
    private final HashMap<UUID, Double> balanceByPlayer = new HashMap<>();
    private boolean initialized;

    public BankManager(AccountManager accountManager, TransactionManager transactionManager, Database database, MessageManager messageManager) {
        this.accountManager = accountManager;
        this.transactionManager = transactionManager;
        this.database = database;
        this.depositMessages = messageManager.getMessages("deposit-messages");
        this.withdrawalMessages = messageManager.getMessages("withdrawal-messages");
    }

    public void onStart(MortisBank plugin) {
        File file = ConfigUtils.getFile(plugin, "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            this.personalMenu = plugin.getCore().getMenuManager().getObject(plugin, config.getConfigurationSection("personal-menu"));
        } catch (ConfigException e) {
            e.setFile(file);
            e.setPath(config);
            throw new RuntimeException(e);
        }
    }

    public void reload(MortisBank plugin, boolean personalMenu) {
        File file = ConfigUtils.getFile(plugin, "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            this.settings = getBankSettings(plugin, config);
            if (personalMenu) {
                onStart(plugin);
            }
        } catch (ConfigException e) {
            e.setFile(file);
            e.setPath(config);
            throw new RuntimeException(e);
        }
        if (!initialized) {
            initialize(plugin);
            initialized = true;
        }
    }

    @Override
    public void reload(MortisBank plugin) {
       reload(plugin, true);
    }

    private BankSettings getBankSettings(MortisBank plugin, ConfigurationSection section) throws ConfigException {
        Currency currency = plugin.getCore().getCurrencyManager().getCurrency(plugin, section.getConfigurationSection("currency"));
        boolean leaderboard = section.getBoolean("leaderboard");
        BankSettings.InputMode mode;
        try {
            mode = BankSettings.InputMode.valueOf(section.getString("input-mode"));
        }catch (IllegalArgumentException exp) {
            throw new ConfigException(section);
        }
        int inputSlot = section.getInt("sign-input-slot");
        CustomItem customItem = plugin.getCore().getItemManager().getObject(plugin, section.getConfigurationSection("anvil-item"));
        return new BankSettings(currency, leaderboard, mode, inputSlot, customItem);
    }

    private void initialize(JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            database.execute("CREATE TABLE IF NOT EXISTS MortisBank(uniqueId varchar(36) primary key, balance double)");
            ResultSet result = database.query("SELECT * FROM MortisBank");
            try {
                while (result.next()) {
                    UUID uniqueId = UUID.fromString(result.getString("uniqueId"));
                    double balance = result.getDouble("balance");
                    balanceByPlayer.put(uniqueId, balance);
                }
            }catch (SQLException exp) {
                throw new RuntimeException(exp);
            }
        });
    }

    private List<UUID> getSortedPlayers() {
        List<UUID> sortedUUIDs = new ArrayList<>(balanceByPlayer.keySet());
        sortedUUIDs.sort(Comparator.<UUID>comparingDouble(balanceByPlayer::get).reversed());
        return sortedUUIDs;
    }

    public UUID getPlayer(int position) {
        List<UUID> uuids = getSortedPlayers();
        if (position >= uuids.size()) {
            return null;
        }
        return uuids.get(position);
    }

    public void setBalance(JavaPlugin plugin, @NotNull UUID uuid, double balance) {
        boolean contains = balanceByPlayer.containsKey(uuid);
        balanceByPlayer.put(uuid, Math.max(balance, 0));
        if (contains) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("UPDATE MortisBank SET balance = ? WHERE uniqueId = ?", balance, uuid.toString()));
        }else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> database.update("INSERT INTO MortisBank(uniqueId, balance) VALUES (?, ?)", uuid.toString(), balance));
        }
    }

    public double getBalance(@NotNull UUID uuid) {
        return balanceByPlayer.get(uuid);
    }

    private void addTransaction(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, double amount, Transaction.TransactionType type) {
        transactionManager.addTransaction(plugin, offlinePlayer, type, amount, Objects.requireNonNullElse(offlinePlayer.getName(), "Unknown"));
    }

    private Placeholder getTransactionPlaceholder(double purse) {
        Placeholder placeholder = new Placeholder();
        placeholder.addReplacement("%amount%", NumberUtils.format(purse));
        placeholder.addReplacement("%amount_raw%", String.valueOf(purse));
        placeholder.addReplacement("%amount_formatted%", NumberUtils.getMoney(purse));
        return placeholder;
    }

    public Messages getMessages(Transaction.TransactionType type) {
        return switch (type) {
            case DEPOSIT -> depositMessages;
            case WITHDRAW -> withdrawalMessages;
        };
    }

    private void sendTransactionMessage(Player player, double purse, Transaction.TransactionType type) {
        if (player == null) {
            return;
        }
        Placeholder placeholder = getTransactionPlaceholder(purse);
        placeholder.addPlaceholder(new Placeholder(player));
        getMessages(type).sendPlaceholderMessage(player, type.name().toLowerCase(Locale.ROOT), placeholder);
    }

    public boolean deposit(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, @NotNull DepositType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(DepositType.SPECIFIC)) {
            settings.open(plugin, this, player, Transaction.TransactionType.DEPOSIT, new Placeholder(player));
            return true;
        }
        double purse = settings.getCurrency().getBalance(offlinePlayer);
        if (type.equals(DepositType.HALF)) {
            purse = purse / 2;
        }
        return deposit(plugin, purse, offlinePlayer, purse);
    }

    public boolean deposit(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, double amount) {
        return deposit(plugin, settings.getCurrency().getBalance(offlinePlayer), offlinePlayer, amount);
    }

    private boolean deposit(JavaPlugin plugin, double purse, OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        if (amount <= 0) {
            depositMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        amount = Math.min(amount, purse);
        if (amount <= 0) {
            depositMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        Account account = accountManager.getAccount(plugin, offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            depositMessages.sendPlaceholderMessage(player, "full", new Placeholder(player));
            return false;
        }
        double newBalance = balance + amount;
        if (!account.isStorable(newBalance)) {
            amount = account.getSpace(balance);
            newBalance = balance + amount;
        }
        settings.getCurrency().withdraw(offlinePlayer, amount);
        setBalance(plugin, offlinePlayer.getUniqueId(), newBalance);
        addTransaction(plugin, offlinePlayer, amount, Transaction.TransactionType.DEPOSIT);
        sendTransactionMessage(player, amount, Transaction.TransactionType.DEPOSIT);
        return true;
    }

    public double getDepositWhole(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer) {
        return getDeposit(plugin, settings.getCurrency().getBalance(offlinePlayer), offlinePlayer);
    }

    public double getDepositHalf(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer) {
        return getDeposit(plugin, settings.getCurrency().getBalance(offlinePlayer) / 2, offlinePlayer);
    }

    private double getDeposit(JavaPlugin plugin, double purse, OfflinePlayer offlinePlayer) {
        Account account = accountManager.getAccount(plugin, offlinePlayer);
        if (account == null) {
            return 0;
        }
        if (purse <= 0) {
            return 0;
        }
        double balance = getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            return 0;
        }
        return Math.min(purse, account.getSpace(balance));
    }

    public boolean withdraw(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, @NotNull WithdrawalType type) {
        Player player = offlinePlayer.getPlayer();
        if (type.equals(WithdrawalType.SPECIFIC)) {
            return settings.open(plugin, this, player, Transaction.TransactionType.WITHDRAW, new Placeholder(player));
        }
        double balance = getBalance(offlinePlayer.getUniqueId());
        if (balance <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "little", new Placeholder(player));
            return false;
        }
        double amount = 0;
        switch (type) {
            case ALL -> amount = balance;
            case HALF -> amount = balance / 2;
            case TWENTY -> amount = balance * 0.20;
        }
        return withdraw(plugin, balance, offlinePlayer, amount);
    }

    public boolean withdraw(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        double balance = getBalance(offlinePlayer.getUniqueId());
        if (balance <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        return withdraw(plugin, balance, offlinePlayer, amount);
    }

    private boolean withdraw(JavaPlugin plugin, double balance, OfflinePlayer offlinePlayer, double amount) {
        Player player = offlinePlayer.getPlayer();
        if (amount <= 0) {
            withdrawalMessages.sendPlaceholderMessage(player, "greater_than_zero", new Placeholder(player));
            return false;
        }
        if (amount > balance) {
            withdrawalMessages.sendPlaceholderMessage(player, "no_money", new Placeholder(player));
            return false;
        }
        settings.getCurrency().deposit(offlinePlayer, amount);
        setBalance(plugin, offlinePlayer.getUniqueId(), balance - amount);
        addTransaction(plugin, offlinePlayer, amount, Transaction.TransactionType.WITHDRAW);
        sendTransactionMessage(player, amount, Transaction.TransactionType.WITHDRAW);
        return true;
    }

    public double getWithdrawEverything(@NotNull OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getUniqueId());
    }

    public double getWithdrawHalf(@NotNull OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getUniqueId()) / 2;
    }

    public double getWithdrawTwentyPercent(@NotNull OfflinePlayer offlinePlayer) {
        return getBalance(offlinePlayer.getUniqueId()) * 0.20;
    }

    private void setBalance(JavaPlugin plugin, Account account, OfflinePlayer offlinePlayer, double amount) {
        setBalance(plugin, offlinePlayer.getUniqueId(), Math.min(amount, account.getMaxBalance()));
    }

    public boolean setBalance(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, double amount) {
        Account account = accountManager.getAccount(plugin, offlinePlayer);
        if (account == null) {
            return false;
        }
        setBalance(plugin, account, offlinePlayer, amount);
        return true;
    }

    public boolean addBalance(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, double amount) {
        Account account = accountManager.getAccount(plugin, offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = getBalance(offlinePlayer.getUniqueId());
        if (account.isFull(balance)) {
            return false;
        }
        setBalance(plugin, account, offlinePlayer, balance + amount);
        return true;
    }

    public boolean subtractBalance(JavaPlugin plugin, @NotNull OfflinePlayer offlinePlayer, double amount) {
        Account account = accountManager.getAccount(plugin, offlinePlayer);
        if (account == null) {
            return false;
        }
        double balance = getBalance(offlinePlayer.getUniqueId());
        if (balance <= 0) {
            return false;
        }
        setBalance(plugin, account, offlinePlayer, balance - amount);
        return true;
    }
}