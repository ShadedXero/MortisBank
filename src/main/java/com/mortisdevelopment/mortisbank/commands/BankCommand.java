package com.mortisdevelopment.mortisbank.commands;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.commands.subcommands.HelpCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.OpenCommand;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BankCommand extends BaseCommand {

    private final BankManager bankManager;

    public BankCommand(Messages messages, BankManager bankManager) {
        super("mortisbank");
        setAliases(List.of("bank"));
        this.bankManager = bankManager;
        addSubCommand(new OpenCommand(messages, bankManager));
        addSubCommand(new HelpCommand(messages));
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
