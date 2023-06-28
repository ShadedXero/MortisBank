package com.mortisdevelopment.mortisbank.bank.admin;

import com.mortisdevelopment.mortisbank.bank.personal.PersonalMenu;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalTransaction;
import com.mortisdevelopment.mortisbank.bank.personal.TransactionType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankCommand implements TabExecutor {

    private final AdminManager manager;
    private enum BalanceType{GET, SET, ADD, REMOVE}
    private enum AccountType{GET, SET, UPGRADE, DOWNGRADE}
    private enum TransactionMode{GET, ADD, CLEAR, CLEARALL}
    private enum InterestType{GET, GIVE}
    private enum InterestTimeType{GET, RESET}

    public BankCommand(@NotNull AdminManager manager) {
        this.manager = manager;
    }

    private OfflinePlayer getTarget(@NotNull String arg) {
        OfflinePlayer target = Bukkit.getPlayer(arg);
        if (target == null) {
            try {
                UUID uuid = UUID.fromString(arg);
                target = Bukkit.getOfflinePlayer(uuid);
            }catch (IllegalArgumentException exp) {
                return null;
            }
        }
        return target;
    }

    private double getAmount(String arg) {
        double amount;
        try {
            amount = Double.parseDouble(arg);
        }catch (NumberFormatException exp) {
            return -1;
        }
        return amount;
    }

    private boolean checkTransaction(@NotNull CommandSender sender, @NotNull String[] args, @NotNull TransactionType type) {
        OfflinePlayer target = getTarget(args[2]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        double amount = getAmount(args[3]);
        if (amount <= 0) {
            sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
            return false;
        }
        if (type.equals(TransactionType.DEPOSIT)) {
            if (!manager.getBankManager().getDepositManager().deposit(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_DEPOSIT", target));
            return true;
        }
        if (type.equals(TransactionType.WITHDRAW)) {
            if (!manager.getBankManager().getWithdrawalManager().withdraw(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_WITHDRAW", target));
            return true;
        }
        return false;
    }

    private boolean checkBalance(@NotNull CommandSender sender, @NotNull String[] args, @NotNull BalanceType type) {
        OfflinePlayer target = getTarget(args[2]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        if (type.equals(BalanceType.GET)) {
            sender.sendMessage(manager.getMessage("ADMIN_BALANCE_GET", target));
            return true;
        }
        double amount = getAmount(args[3]);
        if (amount <= 0) {
            sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
            return false;
        }
        if (type.equals(BalanceType.SET)) {
            if (!manager.setBalance(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_BALANCE_SET", target));
            return true;
        }
        if (type.equals(BalanceType.ADD)) {
            if (!manager.addBalance(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_BALANCE_ADD", target));
            return true;
        }
        if (type.equals(BalanceType.REMOVE)) {
            if (!manager.removeBalance(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_BALANCE_REMOVE", target));
            return true;
        }
        return false;
    }

    private boolean checkAccount(@NotNull CommandSender sender, @NotNull String[] args, @NotNull AccountType type) {
        OfflinePlayer target = getTarget(args[2]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        if (type.equals(AccountType.GET)) {
            sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_GET", target));
            return true;
        }
        if (type.equals(AccountType.UPGRADE)) {
            if (!manager.upgradeAccount(target)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_UPGRADE", target));
            return true;
        }
        if (type.equals(AccountType.DOWNGRADE)) {
            if (!manager.downgradeAccount(target)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_DOWNGRADE", target));
            return true;
        }
        if (type.equals(AccountType.SET)) {
            short priority;
            try {
                priority = Short.parseShort(args[3]);
            }catch (NumberFormatException exp) {
                sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
                return false;
            }
            if (!manager.setAccount(target, priority)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_SET", target));
            return true;
        }
        return false;
    }

    private boolean checkTransaction(@NotNull CommandSender sender, @NotNull String[] args, @NotNull TransactionMode mode) {
        OfflinePlayer target = getTarget(args[2]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        if (mode.equals(TransactionMode.CLEARALL)) {
            if (!manager.clearTransactions(target)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            return true;
        }
        if (mode.equals(TransactionMode.GET)) {
            List<PersonalTransaction> transactions = manager.getBankManager().getDataManager().getTransactions(target.getUniqueId());
            if (transactions.size() == 0) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            for (PersonalTransaction transaction : transactions) {
                sender.sendMessage(transaction.getTransaction(manager.getBankManager().getPersonalManager()));
            }
            return true;
        }
        if (mode.equals(TransactionMode.CLEAR)) {
            int position;
            try {
                position = Integer.parseInt(args[3]);
            }catch (NumberFormatException exp) {
                sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
                return false;
            }
            if (!manager.clearTransaction(target, position)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            return true;
        }
        if (mode.equals(TransactionMode.ADD)) {
            TransactionType type;
            try {
                type = TransactionType.valueOf(args[3]);
            }catch (IllegalArgumentException exp) {
                sender.sendMessage(manager.getMessage("INVALID_TRANSACTION_TYPE"));
                return false;
            }
            String amount = args[4];
            String transactor = args[5];
            if (!manager.addTransaction(target, type, amount, transactor)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean checkInterest(@NotNull CommandSender sender, @NotNull String[] args, @NotNull InterestType type) {
        OfflinePlayer target = getTarget(args[2]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        if (type.equals(InterestType.GET)) {
            sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_GET", target));
            return true;
        }
        if (type.equals(InterestType.GIVE)) {
            if (!manager.getBankManager().getInterestManager().giveInterest(target)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
           return true;
        }
        return false;
    }

    private boolean checkInterestTime(@NotNull CommandSender sender, @NotNull String[] args, @NotNull InterestTimeType type) {
        OfflinePlayer target = getTarget(args[2]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        if (type.equals(InterestTimeType.GET)) {
            sender.sendMessage(manager.getMessage("ADMIN_INTEREST_TIME_GET", target));
            return true;
        }
        if (type.equals(InterestTimeType.RESET)) {
            manager.getBankManager().getInterestManager().resetInterestTime(target);
            sender.sendMessage(manager.getMessage("ADMIN_INTEREST_TIME_RESET", target));
            return true;
        }
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(manager.getMessage("NO_CONSOLE"));
                return false;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("mortisbank.main")) {
                player.sendMessage(manager.getMessage("NO_PERMISSION"));
                return false;
            }
            PersonalMenu menu = new PersonalMenu(manager.getBankManager().getPersonalManager(), player);
            menu.open();
            player.sendMessage(manager.getMessage("PERSONAL_MENU_OPEN"));
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("mortisbank.help")) {
                return false;
            }
            sender.sendMessage(manager.getMessage("HELP"));
            return true;
        }
        if (args[0].equalsIgnoreCase("open")) {
            if (!sender.hasPermission("mortisbank.open")) {
                sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(manager.getMessage("OPEN_USAGE"));
                return false;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(manager.getMessage("INVALID_TARGET"));
                return false;
            }
            PersonalMenu menu = new PersonalMenu(manager.getBankManager().getPersonalManager(), target);
            menu.open();
            return true;
        }
        if (args[0].equalsIgnoreCase("admin")) {
            if (!sender.hasPermission("mortisbank.admin")) {
                sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(manager.getMessage("ADMIN_USAGE"));
                return false;
            }
            if (args[1].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("mortisbank.admin.reload")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                manager.getBankManager().reload();
                sender.sendMessage(manager.getMessage("RELOADED"));
                return true;
            }
            if (args[1].equalsIgnoreCase("deposit")) {
                if (!sender.hasPermission("mortisbank.admin.deposit")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                if (args.length < 4) {
                    sender.sendMessage(manager.getMessage("ADMIN_DEPOSIT_USAGE"));
                    return false;
                }
                return checkTransaction(sender, args, TransactionType.DEPOSIT);
            }
            if (args[1].equalsIgnoreCase("withdraw")) {
                if (!sender.hasPermission("mortisbank.admin.withdraw")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                if (args.length < 4) {
                    sender.sendMessage(manager.getMessage("ADMIN_WITHDRAW_USAGE"));
                    return false;
                }
                return checkTransaction(sender, args, TransactionType.WITHDRAW);
            }
            if (args[1].equalsIgnoreCase("balance")) {
                if (!sender.hasPermission("mortisbank.admin.balance")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage(manager.getMessage("ADMIN_BALANCE_USAGE"));
                    return false;
                }
                if (args[2].equalsIgnoreCase("get")) {
                    if (!sender.hasPermission("mortisbank.admin.get")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_GET_USAGE"));
                        return false;
                    }
                    return checkBalance(sender, args, BalanceType.GET);
                }
                if (args[2].equalsIgnoreCase("set")) {
                    if (!sender.hasPermission("mortisbank.admin.set")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 5) {
                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_SET_USAGE"));
                        return false;
                    }
                    return checkBalance(sender, args, BalanceType.SET);
                }
                if (args[2].equalsIgnoreCase("add")) {
                    if (!sender.hasPermission("mortisbank.admin.add")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 5) {
                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_ADD_USAGE"));
                        return false;
                    }
                    return checkBalance(sender, args, BalanceType.ADD);
                }
                if (args[2].equalsIgnoreCase("remove")) {
                    if (!sender.hasPermission("mortisbank.admin.remove")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 5) {
                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_REMOVE_USAGE"));
                        return false;
                    }
                    return checkBalance(sender, args, BalanceType.REMOVE);
                }
            }
            if (args[1].equalsIgnoreCase("account")) {
                if (!sender.hasPermission("mortisbank.admin.account")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_USAGE"));
                    return false;
                }
                if (args[2].equalsIgnoreCase("get")) {
                    if (!sender.hasPermission("mortisbank.admin.account.get")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_GET_USAGE"));
                        return false;
                    }
                    return checkAccount(sender, args, AccountType.GET);
                }
                if (args[2].equalsIgnoreCase("set")) {
                    if (!sender.hasPermission("mortisbank.admin.account.set")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 5) {
                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_SET_USAGE"));
                        return false;
                    }
                    return checkAccount(sender, args, AccountType.SET);
                }
                if (args[2].equalsIgnoreCase("upgrade")) {
                    if (!sender.hasPermission("mortisbank.admin.account.upgrade")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_UPGRADE_USAGE"));
                        return false;
                    }
                    return checkAccount(sender, args, AccountType.UPGRADE);
                }
                if (args[2].equalsIgnoreCase("downgrade")) {
                    if (!sender.hasPermission("mortisbank.admin.account.downgrade")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_DOWNGRADE_USAGE"));
                        return false;
                    }
                    return checkAccount(sender, args, AccountType.DOWNGRADE);
                }
            }
            if (args[1].equalsIgnoreCase("transaction")) {
                if (!sender.hasPermission("mortisbank.admin.transaction")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_USAGE"));
                    return false;
                }
                if (args[2].equalsIgnoreCase("get")) {
                    if (!sender.hasPermission("mortisbank.admin.transaction.get")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_GET_USAGE"));
                        return false;
                    }
                    return checkTransaction(sender, args, TransactionMode.GET);
                }
                if (args[2].equalsIgnoreCase("add")) {
                    if (!sender.hasPermission("mortisbank.admin.transaction.add")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 7) {
                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_ADD_USAGE"));
                        return false;
                    }
                    return checkTransaction(sender, args, TransactionMode.ADD);
                }
                if (args[2].equalsIgnoreCase("clear")) {
                    if (!sender.hasPermission("mortisbank.admin.transaction.clear")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 5) {
                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_CLEAR_USAGE"));
                        return false;
                    }
                    return checkTransaction(sender, args, TransactionMode.CLEAR);
                }
                if (args[2].equalsIgnoreCase("clearAll")) {
                    if (!sender.hasPermission("mortisbank.admin.transaction.clearall")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    return checkTransaction(sender, args, TransactionMode.CLEARALL);
                }
            }
            if (args[1].equalsIgnoreCase("interest")) {
                if (!sender.hasPermission("mortisbank.admin.interest")) {
                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage(manager.getMessage("ADMIN_INTEREST_USAGE"));
                    return false;
                }
                if (args[2].equalsIgnoreCase("get")) {
                    if (!sender.hasPermission("mortisbank.admin.interest.get")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_INTEREST_GET_USAGE"));
                        return false;
                    }
                    return checkInterest(sender, args, InterestType.GET);
                }
                if (args[2].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission("mortisbank.admin.interest.give")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_INTEREST_GIVE_USAGE"));
                        return false;
                    }
                    return checkInterest(sender, args, InterestType.GIVE);
                }
                if (args[2].equalsIgnoreCase("time")) {
                    if (!sender.hasPermission("mortisbank.admin.interest.time")) {
                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                        return false;
                    }
                    if (args.length < 4) {
                        sender.sendMessage(manager.getMessage("ADMIN_INTEREST_TIME_USAGE"));
                        return false;
                    }
                    if (args[3].equalsIgnoreCase("get")) {
                        if (!sender.hasPermission("mortisbank.admin.interest.time.get")) {
                            sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                            return false;
                        }
                        if (args.length < 5) {
                            sender.sendMessage(manager.getMessage("ADMIN_INTEREST_TIME_GET_USAGE"));
                            return false;
                        }
                        return checkInterestTime(sender, args, InterestTimeType.GET);
                    }
                    if (args[3].equalsIgnoreCase("reset")) {
                        if (!sender.hasPermission("mortisbank.admin.interest.time.reset")) {
                            sender.sendMessage(manager.getMessage("NO_PERMISSION"));
                            return false;
                        }
                        if (args.length < 5) {
                            sender.sendMessage(manager.getMessage("ADMIN_INTEREST_TIME_RESET_USAGE"));
                            return false;
                        }
                        return checkInterestTime(sender, args, InterestTimeType.RESET);
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("help");
            arguments.add("open");
            arguments.add("admin");
            return arguments;
        }
        if (args[0].equalsIgnoreCase("admin")) {
            if (args.length == 2) {
                List<String> arguments = new ArrayList<>();
                arguments.add("reload");
                arguments.add("balance");
                arguments.add("account");
                arguments.add("transaction");
                arguments.add("interest");
                return arguments;
            }
            if (args[1].equalsIgnoreCase("balance")) {
                if (args.length == 3) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("get");
                    arguments.add("set");
                    arguments.add("add");
                    arguments.add("remove");
                    return arguments;
                }
            }
            if (args[1].equalsIgnoreCase("account")) {
                if (args.length == 3) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("get");
                    arguments.add("set");
                    arguments.add("upgrade");
                    arguments.add("downgrade");
                    return arguments;
                }
            }
            if (args[1].equalsIgnoreCase("transaction")) {
                if (args.length == 3) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("get");
                    arguments.add("add");
                    arguments.add("clear");
                    arguments.add("clearAll");
                    return arguments;
                }
            }
            if (args[1].equalsIgnoreCase("interest")) {
                if (args.length == 3) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("get");
                    arguments.add("give");
                    arguments.add("time");
                    return arguments;
                }
                if (args[2].equalsIgnoreCase("time")) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("get");
                    arguments.add("reset");
                    return arguments;
                }
            }
        }
        return null;
    }
}
