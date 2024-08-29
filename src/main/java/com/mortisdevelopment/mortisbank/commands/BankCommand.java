package com.mortisdevelopment.mortisbank.commands;

import com.mortisdevelopment.mortisbank.commands.subcommands.HelpCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.OpenCommand;
import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BankCommand extends BaseCommand {

    private final PersonalManager personalManager;

    public BankCommand(CommandManager commandManager, PersonalManager personalManager) {
        super("mortisbank");
        setAliases(List.of("bank"));
        this.personalManager = personalManager;
        addSubCommand(new OpenCommand(commandManager, personalManager));
        addSubCommand(new HelpCommand(commandManager));
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
        personalManager.getMenu().open(player, new Placeholder(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return List.of("help", "open", "admin");
        }
        return null;
    }
}
