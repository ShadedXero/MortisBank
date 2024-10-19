package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BalanceActionCommand extends PermissionCommand {

    public enum Action{SET,ADD,SUBTRACT}

    private final JavaPlugin plugin;
    private final BankManager bankManager;
    private final Action action;

    public BalanceActionCommand(String name, String permission, Messages messages, JavaPlugin plugin, BankManager bankManager, Action action) {
        super(name, permission, messages);
        this.plugin = plugin;
        this.bankManager = bankManager;
        this.action = action;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
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
        } catch (NumberFormatException e) {
            getMessages().sendMessage(sender, "invalid_amount");
            return false;
        }
        boolean success = switch (action) {
            case SET -> bankManager.setBalance(plugin, target, amount);
            case ADD -> bankManager.addBalance(plugin, target, amount);
            case SUBTRACT -> bankManager.subtractBalance(plugin, target, amount);
        };
        if (!success) {
            getMessages().sendMessage(sender, "could_not_process");
            return false;
        }
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}