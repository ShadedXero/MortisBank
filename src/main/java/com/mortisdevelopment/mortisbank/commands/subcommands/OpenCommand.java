package com.mortisdevelopment.mortisbank.commands.subcommands;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenCommand extends PermissionCommand {

    private final BankManager bankManager;

    public OpenCommand(Messages messages, BankManager bankManager) {
        super("open", "mortisbank.open", messages);
        this.bankManager = bankManager;
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
        bankManager.getPersonalMenu().open(target, new Placeholder(target));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
