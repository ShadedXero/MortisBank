package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends PermissionCommand {

    private final MortisBank plugin;

    public ReloadCommand(Messages messages, MortisBank plugin) {
        super("reload", "mortisbank.admin.reload", messages);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] strings) {
        plugin.reload();
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
