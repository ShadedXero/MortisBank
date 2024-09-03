package com.mortisdevelopment.mortisbank.commands;

import com.mortisdevelopment.mortisbank.commands.subcommands.HelpCommand;
import com.mortisdevelopment.mortisbank.commands.subcommands.OpenCommand;
import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortiscore.commands.BaseCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BankCommand extends BaseCommand {

    private final PersonalManager personalManager;

    public BankCommand(Messages messages, PersonalManager personalManager) {
        super("mortisbank");
        setAliases(List.of("bank"));
        this.personalManager = personalManager;
        addSubCommand(new OpenCommand(messages, personalManager));
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
        personalManager.getMenu().open(player, new Placeholder(player));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
