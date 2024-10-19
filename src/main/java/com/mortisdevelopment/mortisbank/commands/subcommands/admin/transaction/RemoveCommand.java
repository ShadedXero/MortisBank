package com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction;

import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RemoveCommand extends PermissionCommand {

    private final JavaPlugin plugin;
    private final TransactionManager transactionManager;

    public RemoveCommand(Messages messages, JavaPlugin plugin, TransactionManager transactionManager) {
        super("remove", "mortisbank.admin.transaction.remove", messages);
        this.plugin = plugin;
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
        if (args.length < 2) {
            getMessages().sendMessage(sender, "wrong_usage");
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            getMessages().sendMessage(sender, "invalid_target");
            return false;
        }
        int position;
        try {
            position = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            getMessages().sendMessage(sender, "invalid_number");
            return false;
        }
        if (!transactionManager.removeTransaction(plugin, target, position)) {
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
