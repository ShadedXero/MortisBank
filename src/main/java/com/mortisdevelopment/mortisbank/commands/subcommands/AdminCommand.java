package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.*;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AdminCommand extends PermissionCommand {

    public AdminCommand(Messages messages, MortisBank plugin, BankManager bankManager, AccountManager accountManager, TransactionManager transactionManager) {
        super("admin", "mortisbank.admin", messages);
        addSubCommand(new DepositCommand(messages, plugin));
        addSubCommand(new WithdrawCommand(messages, plugin));
        addSubCommand(new BalanceCommand(messages, bankManager));
        addSubCommand(new AccountCommand(messages, accountManager));
        addSubCommand(new TransactionCommand(messages, transactionManager));
        addSubCommand(new ReloadCommand(messages, plugin));
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
