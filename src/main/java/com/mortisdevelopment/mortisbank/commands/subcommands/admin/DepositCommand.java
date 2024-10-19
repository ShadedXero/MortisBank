package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.actions.types.DepositActionType;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholders.Placeholder;
import com.mortisdevelopment.mortiscore.utils.PlayerExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DepositCommand extends PermissionCommand {

    private final MortisBank plugin;

    public DepositCommand(Messages messages, MortisBank plugin) {
        super("deposit", "mortisbank.admin.deposit", messages);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
        if (args.length < 2) {
            getMessages().sendMessage(sender, "wrong_usage");
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            getMessages().sendMessage(sender, "invalid_target");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        }catch (NumberFormatException exp) {
            getMessages().sendMessage(sender, "invalid_number");
            return false;
        }
        new DepositActionType(plugin, amount).execute(new PlayerExecutor(target), new Placeholder());
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
