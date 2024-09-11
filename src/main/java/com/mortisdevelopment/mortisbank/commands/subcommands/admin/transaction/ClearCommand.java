package com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction;

import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ClearCommand extends PermissionCommand {

    private final TransactionManager transactionManager;

    public ClearCommand(Messages messages, TransactionManager transactionManager) {
        super("clear", "mortisbank.admin.transaction.clear", messages);
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
        if (args.length < 1) {
            getMessages().sendMessage(sender, "wrong_usage");
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            getMessages().sendMessage(sender, "invalid_target");
            return false;
        }
        if (!transactionManager.clearTransactions(target.getUniqueId())) {
            getMessages().sendMessage(sender, "could_not_process");
            return false;
        }
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
