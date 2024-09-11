package com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction;

import com.mortisdevelopment.mortisbank.transactions.Transaction;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AddCommand extends PermissionCommand {

    private final TransactionManager transactionManager;

    public AddCommand(Messages messages, TransactionManager transactionManager) {
        super("add", "mortisbank.admin.transaction.add", messages);
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length < 4) {
            getMessages().sendMessage(sender, "wrong_usage");
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            getMessages().sendMessage(sender, "invalid_target");
            return false;
        }
        Transaction.TransactionType type;
        try {
            type = Transaction.TransactionType.valueOf(args[1]);
        } catch (IllegalArgumentException e) {
            getMessages().sendMessage(sender, "invalid_type");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            getMessages().sendMessage(sender, "invalid_amount");
            return false;
        }
        String transactor = args[3];
        transactionManager.addTransaction(target, type, amount, transactor);
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}