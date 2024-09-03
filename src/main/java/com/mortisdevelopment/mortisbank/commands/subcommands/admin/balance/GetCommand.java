package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GetCommand extends PermissionCommand {

    public GetCommand(String name, String permission, Messages messages) {
        super(name, permission, messages);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return List.of();
    }
}
