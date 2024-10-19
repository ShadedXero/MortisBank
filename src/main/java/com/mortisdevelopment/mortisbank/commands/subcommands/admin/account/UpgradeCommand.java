package com.mortisdevelopment.mortisbank.commands.subcommands.admin.account;

import com.mortisdevelopment.mortisbank.accounts.AccountManager;
import com.mortisdevelopment.mortiscore.commands.PermissionCommand;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class UpgradeCommand extends PermissionCommand {

    private final JavaPlugin plugin;
    private final AccountManager accountManager;

    public UpgradeCommand(Messages messages, JavaPlugin plugin, AccountManager accountManager) {
        super("upgrade", "mortisbank.admin.account.upgrade", messages);
        this.plugin = plugin;
        this.accountManager = accountManager;
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
        if (!accountManager.upgradeAccount(plugin, target)) {
            getMessages().sendMessage(sender, "could_not_process");
            return false;
        }
        getMessages().sendMessage(sender, "command_success");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String s, String[] strings) {
        return null;
    }
}
