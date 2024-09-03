package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AddCommand extends PermissionCommand {

    private final BankManager bankManager;

    public AddCommand(Messages messages, BankManager bankManager) {
        super("add", "mortisbank.balance.add", messages);
        this.bankManager = bankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("mortisbank.admin.set")) {
            sender.sendMessage(manager.getMessage("NO_PERMISSION"));
            return false;
        }
        if (args.length < 5) {
            sender.sendMessage(manager.getMessage("ADMIN_BALANCE_SET_USAGE"));
            return false;
        }
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        OfflinePlayer target = getTarget(args[3]);
        if (target == null) {
            sender.sendMessage(manager.getMessage("INVALID_TARGET"));
            return false;
        }
        if (type.equals(BalanceType.GET)) {
            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_GET"));
            return true;
        }
        double amount = getAmount(args[4]);
        if (amount <= 0) {
            sender.sendMessage(manager.getMessage("INVALID_NUMBER"));
            return false;
        }
        if (type.equals(BalanceType.SET)) {
            if (!manager.setBalance(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            sender.sendMessage(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_SET"));
            return true;
        }
        if (type.equals(BalanceType.ADD)) {
            if (!manager.addBalance(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_ADD"));
            utils.replace("%amount%", formatter.format(amount));
            utils.replace("%amount_raw%", String.valueOf(amount));
            utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
            sender.sendMessage(utils.getMessage());
            return true;
        }
        if (type.equals(BalanceType.REMOVE)) {
            if (!manager.removeBalance(target, amount)) {
                sender.sendMessage(manager.getMessage("COULD_NOT_PROCESS"));
                return false;
            }
            MessageUtils utils = new MessageUtils(manager.getPlaceholderMessage(target, "ADMIN_BALANCE_REMOVE"));
            utils.replace("%amount%", formatter.format(amount));
            utils.replace("%amount_raw%", String.valueOf(amount));
            utils.replace("%amount_formatted%", MoneyUtils.getMoney(amount));
            sender.sendMessage(utils.getMessage());
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return List.of();
    }
}
