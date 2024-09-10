package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TransactionCommand extends BaseCommand {

    public TransactionCommand(Messages messages, TransactionManager transactionManager) {
        super("transaction");
        addSubCommand();
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
