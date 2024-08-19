package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortisbank.commands.CommandManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AdminCommand extends PermissionCommand {

    public AdminCommand(CommandManager manager) {
        super("admin", "mortisbank.admin", manager);
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
