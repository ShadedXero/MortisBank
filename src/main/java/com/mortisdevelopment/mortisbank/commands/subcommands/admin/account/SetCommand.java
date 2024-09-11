package com.mortisdevelopment.mortisbank.commands.subcommands.admin.account;

import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetCommand extends PermissionCommand {

    private final AccountManager accountManager;

    public SetCommand(Messages messages, AccountManager accountManager) {
        super("set", "mortisbank.admin.account.set", messages);
        this.accountManager = accountManager;
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
        boolean success = true;
        try {
            short priority = Short.parseShort(args[1]);
            if (!accountManager.setAccount(target, priority)) {
                success = false;
            }
        } catch (NumberFormatException e) {
            Account account = accountManager.getAccount(args[1]);
            if (account == null) {
                getMessages().sendMessage(sender, "invalid_account");
                return false;
            }
            if (!accountManager.setAccount(target, account.getPriority())) {
                success = false;
            }
        }
        if (!success) {
            getMessages().sendMessage(sender, "could_not_process");
            return false;
        }
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String s, String[] args) {
        if (args.length == 2) {
            return new ArrayList<>(accountManager.getAccountById().keySet());
        }
        return null;
    }
}
