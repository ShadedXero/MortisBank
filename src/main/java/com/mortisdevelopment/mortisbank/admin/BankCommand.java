package com.mortisdevelopment.mortisbank.admin;

public class BankCommand {

//    private final MortisBank plugin = MortisBank.getInstance();
//    private final AdminManager manager = plugin.getAdminManager();
//    private enum BalanceType{GET, SET, ADD, REMOVE}
//    private enum AccountType{GET, SET, UPGRADE, DOWNGRADE}
//    private enum TransactionMode{GET, ADD, CLEAR, CLEARALL}
//
//    private OfflinePlayer getTarget(@NotNull String arg) {
//        OfflinePlayer target = Bukkit.getPlayer(arg);
//        if (target == null) {
//            try {
//                UUID uuid = UUID.fromString(arg);
//                target = Bukkit.getOfflinePlayer(uuid);
//            }catch (IllegalArgumentException exp) {
//                return null;
//            }
//        }
//        return target;
//    }
//
//    private double getAmount(String arg) {
//        double amount;
//        try {
//            amount = Double.parseDouble(arg);
//        }catch (NumberFormatException exp) {
//            return -1;
//        }
//        return amount;
//    }
//
//    private boolean checkTransaction(@NotNull CommandSender sender, @NotNull String[] args, @NotNull TransactionType type) {
//        DecimalFormat formatter = new DecimalFormat("#,###.#");
//        OfflinePlayer target = getTarget(args[2]);
//        if (target == null) {
//            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
//            return false;
//        }
//        double amount = getAmount(args[3]);
//        if (amount <= 0) {
//            sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
//            return false;
//        }
//        if (type.equals(TransactionType.DEPOSIT)) {
//            if (!plugin.getDepositManager().deposit(target, amount)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_DEPOSIT"));
//            utils.replace("%amount%", formatter.format(amount));
//            utils.replace("%amount_raw%", String.valueOf(amount));
//            utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
//            sender.sendMessage(utils.getMessage());
//            return true;
//        }
//        if (type.equals(TransactionType.WITHDRAW)) {
//            if (!plugin.getWithdrawalManager().withdraw(target, amount)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_WITHDRAW"));
//            utils.replace("%amount%", formatter.format(amount));
//            utils.replace("%amount_raw%", String.valueOf(amount));
//            utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
//            sender.sendMessage(utils.getMessage());
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkBalance(@NotNull CommandSender sender, @NotNull String[] args, @NotNull BalanceType type) {
//        DecimalFormat formatter = new DecimalFormat("#,###.#");
//        OfflinePlayer target = getTarget(args[3]);
//        if (target == null) {
//            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
//            return false;
//        }
//        if (type.equals(BalanceType.GET)) {
//            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_GET"));
//            return true;
//        }
//        double amount = getAmount(args[4]);
//        if (amount <= 0) {
//            sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
//            return false;
//        }
//        if (type.equals(BalanceType.SET)) {
//            if (!manager.setBalance(target, amount)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_SET"));
//            return true;
//        }
//        if (type.equals(BalanceType.ADD)) {
//            if (!manager.addBalance(target, amount)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_ADD"));
//            utils.replace("%amount%", formatter.format(amount));
//            utils.replace("%amount_raw%", String.valueOf(amount));
//            utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
//            sender.sendMessage(utils.getMessage());
//            return true;
//        }
//        if (type.equals(BalanceType.REMOVE)) {
//            if (!manager.removeBalance(target, amount)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_REMOVE"));
//            utils.replace("%amount%", formatter.format(amount));
//            utils.replace("%amount_raw%", String.valueOf(amount));
//            utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
//            sender.sendMessage(utils.getMessage());
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkAccount(@NotNull CommandSender sender, @NotNull String[] args, @NotNull AccountType type) {
//        OfflinePlayer target = getTarget(args[3]);
//        if (target == null) {
//            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
//            return false;
//        }
//        if (type.equals(AccountType.GET)) {
//            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_ACCOUNT_GET"));
//            return true;
//        }
//        if (type.equals(AccountType.UPGRADE)) {
//            if (!manager.upgradeAccount(target)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_ACCOUNT_UPGRADE"));
//            return true;
//        }
//        if (type.equals(AccountType.DOWNGRADE)) {
//            if (!manager.downgradeAccount(target)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_ACCOUNT_DOWNGRADE"));
//            return true;
//        }
//        if (type.equals(AccountType.SET)) {
//            short priority;
//            try {
//                priority = Short.parseShort(args[4]);
//            }catch (NumberFormatException exp) {
//                sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
//                return false;
//            }
//            if (!manager.setAccount(target, priority)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_ACCOUNT_SET"));
//            return true;
//        }
//        return false;
//    }
//
//    private boolean checkTransaction(@NotNull CommandSender sender, @NotNull String[] args, @NotNull TransactionMode mode) {
//        OfflinePlayer target = getTarget(args[3]);
//        if (target == null) {
//            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
//            return false;
//        }
//        if (mode.equals(TransactionMode.CLEARALL)) {
//            if (!manager.clearTransactions(target)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_CLEARALL"));
//            return true;
//        }
//        if (mode.equals(TransactionMode.GET)) {
//            List<PersonalTransaction> transactions = plugin.getDataManager().getTransactions(target.getUniqueId());
//            if (transactions.isEmpty()) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            for (PersonalTransaction transaction : transactions) {
//                sender.sendMessage(transaction.getTransaction(plugin.getPersonalManager()));
//            }
//            return true;
//        }
//        if (mode.equals(TransactionMode.CLEAR)) {
//            int position;
//            try {
//                position = Integer.parseInt(args[4]);
//            }catch (NumberFormatException exp) {
//                sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
//                return false;
//            }
//            if (!manager.clearTransaction(target, position)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_CLEAR"));
//            return true;
//        }
//        if (mode.equals(TransactionMode.ADD)) {
//            TransactionType type;
//            try {
//                type = TransactionType.valueOf(args[4]);
//            }catch (IllegalArgumentException exp) {
//                sender.sendMessage(manager.getMessage("INVALID_TRANSACTION_TYPE"));
//                return false;
//            }
//            String amount = args[5];
//            String transactor = args[6];
//            if (!manager.addTransaction(target, type, amount, transactor)) {
//                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
//                return false;
//            }
//            sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_ADD"));
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
//        if (args.length == 0) {
//            if (!(sender instanceof Player)) {
//                sender.sendMessage(manager.getMessage("NO_CONSOLE"));
//                return false;
//            }
//            Player player = (Player) sender;
//            if (!player.hasPermission("mortisbank.main")) {
//                player.sendMessage(manager.getMessage("NO_PERMISSION"));
//                return false;
//            }
//            plugin.getPersonalManager().getMenu().open(player);
//            player.sendMessage(manager.getMessage("PERSONAL_MENU_OPEN"));
//            return true;
//        }
//        if (args[0].equalsIgnoreCase("help")) {
//            if (!sender.hasPermission("mortisbank.help")) {
//                return false;
//            }
//            sender.sendMessage(manager.getMessage("HELP"));
//            return true;
//        }
//        if (args[0].equalsIgnoreCase("open")) {
//            if (!sender.hasPermission("mortisbank.open")) {
//                sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                return false;
//            }
//            if (args.length < 2) {
//                sender.sendMessage(manager.getMessage("OPEN_USAGE"));
//                return false;
//            }
//            Player target = Bukkit.getPlayer(args[1]);
//            if (target == null) {
//                sender.sendMessage(manager.getMessage("INVALID_TARGET"));
//                return false;
//            }
//            plugin.getPersonalManager().getMenu().open(target);
//            return true;
//        }
//        if (args[0].equalsIgnoreCase("admin")) {
//            if (!sender.hasPermission("mortisbank.admin")) {
//                sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                return false;
//            }
//            if (args.length < 2) {
//                sender.sendMessage(manager.getMessage("ADMIN_USAGE"));
//                return false;
//            }
//            if (args[1].equalsIgnoreCase("reload")) {
//                if (!sender.hasPermission("mortisbank.admin.reload")) {
//                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                    return false;
//                }
//                plugin.reload();
//                sender.sendMessage(manager.getMessage("RELOADED"));
//                return true;
//            }
//            if (args[1].equalsIgnoreCase("deposit")) {
//                if (!sender.hasPermission("mortisbank.admin.deposit")) {
//                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                    return false;
//                }
//                if (args.length < 4) {
//                    sender.sendMessage(manager.getMessage("ADMIN_DEPOSIT_USAGE"));
//                    return false;
//                }
//                return checkTransaction(sender, args, TransactionType.DEPOSIT);
//            }
//            if (args[1].equalsIgnoreCase("withdraw")) {
//                if (!sender.hasPermission("mortisbank.admin.withdraw")) {
//                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                    return false;
//                }
//                if (args.length < 4) {
//                    sender.sendMessage(manager.getMessage("ADMIN_WITHDRAW_USAGE"));
//                    return false;
//                }
//                return checkTransaction(sender, args, TransactionType.WITHDRAW);
//            }
//            if (args[1].equalsIgnoreCase("balance")) {
//                if (!sender.hasPermission("mortisbank.admin.balance")) {
//                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                    return false;
//                }
//                if (args.length < 3) {
//                    sender.sendMessage(manager.getMessage("ADMIN_BALANCE_USAGE"));
//                    return false;
//                }
//                if (args[2].equalsIgnoreCase("get")) {
//                    if (!sender.hasPermission("mortisbank.admin.get")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 4) {
//                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_GET_USAGE"));
//                        return false;
//                    }
//                    return checkBalance(sender, args, BalanceType.GET);
//                }
//                if (args[2].equalsIgnoreCase("set")) {
//                    if (!sender.hasPermission("mortisbank.admin.set")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 5) {
//                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_SET_USAGE"));
//                        return false;
//                    }
//                    return checkBalance(sender, args, BalanceType.SET);
//                }
//                if (args[2].equalsIgnoreCase("add")) {
//                    if (!sender.hasPermission("mortisbank.admin.add")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 5) {
//                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_ADD_USAGE"));
//                        return false;
//                    }
//                    return checkBalance(sender, args, BalanceType.ADD);
//                }
//                if (args[2].equalsIgnoreCase("remove")) {
//                    if (!sender.hasPermission("mortisbank.admin.remove")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 5) {
//                        sender.sendMessage(manager.getMessage("ADMIN_BALANCE_REMOVE_USAGE"));
//                        return false;
//                    }
//                    return checkBalance(sender, args, BalanceType.REMOVE);
//                }
//            }
//            if (args[1].equalsIgnoreCase("account")) {
//                if (!sender.hasPermission("mortisbank.admin.account")) {
//                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                    return false;
//                }
//                if (args.length < 3) {
//                    sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_USAGE"));
//                    return false;
//                }
//                if (args[2].equalsIgnoreCase("get")) {
//                    if (!sender.hasPermission("mortisbank.admin.account.get")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 4) {
//                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_GET_USAGE"));
//                        return false;
//                    }
//                    return checkAccount(sender, args, AccountType.GET);
//                }
//                if (args[2].equalsIgnoreCase("set")) {
//                    if (!sender.hasPermission("mortisbank.admin.account.set")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 5) {
//                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_SET_USAGE"));
//                        return false;
//                    }
//                    return checkAccount(sender, args, AccountType.SET);
//                }
//                if (args[2].equalsIgnoreCase("upgrade")) {
//                    if (!sender.hasPermission("mortisbank.admin.account.upgrade")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 4) {
//                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_UPGRADE_USAGE"));
//                        return false;
//                    }
//                    return checkAccount(sender, args, AccountType.UPGRADE);
//                }
//                if (args[2].equalsIgnoreCase("downgrade")) {
//                    if (!sender.hasPermission("mortisbank.admin.account.downgrade")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 4) {
//                        sender.sendMessage(manager.getMessage("ADMIN_ACCOUNT_DOWNGRADE_USAGE"));
//                        return false;
//                    }
//                    return checkAccount(sender, args, AccountType.DOWNGRADE);
//                }
//            }
//            if (args[1].equalsIgnoreCase("transaction")) {
//                if (!sender.hasPermission("mortisbank.admin.transaction")) {
//                    sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                    return false;
//                }
//                if (args.length < 3) {
//                    sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_USAGE"));
//                    return false;
//                }
//                if (args[2].equalsIgnoreCase("get")) {
//                    if (!sender.hasPermission("mortisbank.admin.transaction.get")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 4) {
//                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_GET_USAGE"));
//                        return false;
//                    }
//                    return checkTransaction(sender, args, TransactionMode.GET);
//                }
//                if (args[2].equalsIgnoreCase("add")) {
//                    if (!sender.hasPermission("mortisbank.admin.transaction.add")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 7) {
//                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_ADD_USAGE"));
//                        return false;
//                    }
//                    return checkTransaction(sender, args, TransactionMode.ADD);
//                }
//                if (args[2].equalsIgnoreCase("clear")) {
//                    if (!sender.hasPermission("mortisbank.admin.transaction.clear")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 5) {
//                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_CLEAR_USAGE"));
//                        return false;
//                    }
//                    return checkTransaction(sender, args, TransactionMode.CLEAR);
//                }
//                if (args[2].equalsIgnoreCase("clearAll")) {
//                    if (!sender.hasPermission("mortisbank.admin.transaction.clearall")) {
//                        sender.sendMessage(manager.getMessage("NO_PERMISSION"));
//                        return false;
//                    }
//                    if (args.length < 4) {
//                        sender.sendMessage(manager.getMessage("ADMIN_TRANSACTION_CLEARALL_USAGE"));
//                        return false;
//                    }
//                    return checkTransaction(sender, args, TransactionMode.CLEARALL);
//                }
//            }
//        }
//        return false;
//    }
//
//    @Nullable
//    @Override
//    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
//        if (args.length == 1) {
//            List<String> arguments = new ArrayList<>();
//            arguments.add("help");
//            arguments.add("open");
//            arguments.add("admin");
//            return arguments;
//        }
//        if (args[0].equalsIgnoreCase("admin")) {
//            if (args.length == 2) {
//                List<String> arguments = new ArrayList<>();
//                arguments.add("reload");
//                arguments.add("deposit");
//                arguments.add("withdraw");
//                arguments.add("balance");
//                arguments.add("account");
//                arguments.add("transaction");
//                return arguments;
//            }
//            if (args[1].equalsIgnoreCase("balance")) {
//                if (args.length == 3) {
//                    List<String> arguments = new ArrayList<>();
//                    arguments.add("get");
//                    arguments.add("set");
//                    arguments.add("add");
//                    arguments.add("remove");
//                    return arguments;
//                }
//            }
//            if (args[1].equalsIgnoreCase("account")) {
//                if (args.length == 3) {
//                    List<String> arguments = new ArrayList<>();
//                    arguments.add("get");
//                    arguments.add("set");
//                    arguments.add("upgrade");
//                    arguments.add("downgrade");
//                    return arguments;
//                }
//            }
//            if (args[1].equalsIgnoreCase("transaction")) {
//                if (args.length == 3) {
//                    List<String> arguments = new ArrayList<>();
//                    arguments.add("get");
//                    arguments.add("add");
//                    arguments.add("clear");
//                    arguments.add("clearAll");
//                    return arguments;
//                }
//            }
//        }
//        return null;
//    }
}
