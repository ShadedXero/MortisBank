package com.mortisdevelopment.mortisbank.commands.subcommands.admin.account;

import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class GetCommand extends PermissionCommand {

    public GetCommand(Messages messages) {
        super("get", "mortisbank.admin.account.get", messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
        if (args.length < 1) {
            getMessages().sendMessage(sender, "wrong_usage");
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            getMessages().sendMessage(sender, "invalid_target");
            return false;
        }
        getMessages().sendPlaceholderMessage(sender, "account_get", new Placeholder(target));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
