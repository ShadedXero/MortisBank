package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.AddCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.GetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.SetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.SubtractCommand;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BalanceCommand extends BaseCommand {

    public BalanceCommand(Messages messages, BankManager bankManager) {
        super("balance");
        addSubCommand(new SetCommand(messages, bankManager));
        addSubCommand(new AddCommand(messages, bankManager));
        addSubCommand(new SubtractCommand(messages, bankManager));
        addSubCommand(new GetCommand(messages, bankManager));
    }

    @Override
    public boolean isSender(CommandSender commandSender, boolean b) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
