package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction.AddCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction.ClearCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction.GetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.transaction.RemoveCommand;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class TransactionCommand extends BaseCommand {

    public TransactionCommand(Messages messages, JavaPlugin plugin, TransactionManager transactionManager) {
        super("transaction");
        addSubCommand(new AddCommand(messages, plugin, transactionManager));
        addSubCommand(new RemoveCommand(messages, plugin, transactionManager));
        addSubCommand(new ClearCommand(messages, plugin, transactionManager));
        addSubCommand(new GetCommand(messages));
    }

    @Override
    public boolean isSender(CommandSender commandSender, boolean b) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
