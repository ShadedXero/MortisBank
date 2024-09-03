package com.mortisdevelopment.mortisbank.commands.subcommands.admin;

import com.mortisdevelopment.mortisbank.actions.types.DepositActionType;
import com.mortisdevelopment.mortisbank.actions.types.SetAccountActionType;
import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortisbank.utils.BankUtils;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.utils.PlayerExecutor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DepositCommand extends PermissionCommand {

    private final BankManager bankManager;

    public DepositCommand(Messages messages, BankManager bankManager) {
        super("deposit", "mortisbank.admin.deposit", messages);
        this.bankManager = bankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String s, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(getMessages().getMessage("admin_deposit_usage"));
            return false;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            sender.sendMessage(getMessages().getMessage("invalid_target"));
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        }catch (NumberFormatException exp) {
            sender.sendMessage(getMessages().getMessage("invalid_number"));
            return false;
        }
        if (amount <= 0) {
            sender.sendMessage(getMessages().getMessage("invalid_number"));
            return false;
        }
        if (!bankManager.deposit(target, amount)) {
            sender.sendMessage(getMessages().getMessage("could_not_process"));
            return false;
        }
        getMessages().sendPlaceholderMessage(sender, "admin_deposit", BankUtils.getPlaceholder(amount));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
