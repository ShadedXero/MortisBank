package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.account.DowngradeCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.account.GetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.account.SetCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.admin.account.UpgradeCommand;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class AccountCommand extends BaseCommand {

    public AccountCommand(Messages messages, JavaPlugin plugin, AccountManager accountManager) {
        super("account");
        addSubCommand(new SetCommand(messages, plugin, accountManager));
        addSubCommand(new UpgradeCommand(messages, plugin, accountManager));
        addSubCommand(new DowngradeCommand(messages, plugin, accountManager));
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
