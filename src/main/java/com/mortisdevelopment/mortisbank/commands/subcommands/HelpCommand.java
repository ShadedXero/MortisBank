package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends PermissionCommand {

    public HelpCommand(Messages messages) {
        super("help", "mortisbank.help", messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        getMessages().sendMessage(sender, "help");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
