package com.mortisdevelopment.mortisbank.commands;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.AdminCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.HelpCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.OpenCommand;
import com.mortisdevelopment.mortisbank.transactions.TransactionManager;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BankCommand extends BaseCommand {

    private final BankManager bankManager;

    public BankCommand(Messages messages, MortisBank plugin, BankManager bankManager, AccountManager accountManager, TransactionManager transactionManager) {
        super("mortisbank");
        setAliases(List.of("bank"));
        this.bankManager = bankManager;
        addSubCommand(new HelpCommand(messages));
        addSubCommand(new OpenCommand(messages, bankManager));
        addSubCommand(new AdminCommand(messages, plugin, bankManager, accountManager, transactionManager));
    }

    @Override
    public boolean isSender(CommandSender sender, boolean silent) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length > 0) {
            return false;
        }
        bankManager.getPersonalMenu().open(player, new Placeholder(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
