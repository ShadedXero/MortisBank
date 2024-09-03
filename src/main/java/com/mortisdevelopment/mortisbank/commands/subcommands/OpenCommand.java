package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortisbank.personal.PersonalManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenCommand extends PermissionCommand {

    private final PersonalManager personalManager;

    public OpenCommand(Messages messages, PersonalManager personalManager) {
        super("open", "mortisbank.open", messages);
        this.personalManager = personalManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(getMessages().getMessage("open_usage"));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(getMessages().getMessage("invalid_target"));
            return false;
        }
        personalManager.getMenu().open(target, new Placeholder(target));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
