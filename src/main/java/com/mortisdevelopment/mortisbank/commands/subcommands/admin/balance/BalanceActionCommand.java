package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BalanceActionCommand extends PermissionCommand {

    public enum Action{SET,ADD,SUBTRACT}
    private final Action action;
    private final BankManager bankManager;

    public BalanceActionCommand(String name, String permission, Messages messages, Action action, BankManager bankManager) {
        super(name, permission, messages);
        this.action = action;
        this.bankManager = bankManager;
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
        switch (action) {
            case SET -> bankManager.setBalance(target, amount);
            case ADD -> bankManager.addBalance(target, amount);
            case SUBTRACT -> bankManager.subtractBalance(target, amount);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}