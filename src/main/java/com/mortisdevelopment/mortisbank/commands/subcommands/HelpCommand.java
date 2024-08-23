package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortisbank.commands.CommandManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends PermissionCommand {

    public HelpCommand(CommandManager manager) {
        super("help", "mortisbank.help", manager);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        sender.sendMessage(getManager().getMessage("help"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
