package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.AddCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.GetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.SetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance.SubtractCommand;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BalanceCommand extends BaseCommand {

    public BalanceCommand(Messages messages, JavaPlugin plugin, BankManager bankManager) {
        super("balance");
        addSubCommand(new SetCommand(messages, plugin, bankManager));
        addSubCommand(new AddCommand(messages, plugin, bankManager));
        addSubCommand(new SubtractCommand(messages, plugin, bankManager));
        addSubCommand(new GetCommand(messages));
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
