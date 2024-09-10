package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.AccountCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.BalanceCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.DepositCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.WithdrawCommand;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AdminCommand extends PermissionCommand {

    public AdminCommand(Messages messages, BankManager bankManager, AccountManager accountManager) {
        super("admin", "mortisbank.admin", messages);
        addSubCommand(new DepositCommand(messages, bankManager));
        addSubCommand(new WithdrawCommand(messages, bankManager));
        addSubCommand(new BalanceCommand(messages, bankManager));
        addSubCommand(new AccountCommand(messages, accountManager));
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
