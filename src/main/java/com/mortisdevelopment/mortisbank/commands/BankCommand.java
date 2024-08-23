package com.mortisdevelopment.mortisbank.commands;

import com.mortisdevelopment.mortisbank.commands.subcommands.HelpCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.OpenCommand;
import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BankCommand extends BaseCommand {

    public BankCommand(CommandManager commandManager, PersonalManager personalManager) {
        super("mortisbank");
        setAliases(List.of("bank"));
        addSubCommand(new OpenCommand(commandManager, personalManager));
        addSubCommand(new HelpCommand(commandManager));
    }

    @Override
    public boolean isSender(CommandSender sender, boolean silent) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("help", "open", "admin");
        }
        return null;
    }
}
