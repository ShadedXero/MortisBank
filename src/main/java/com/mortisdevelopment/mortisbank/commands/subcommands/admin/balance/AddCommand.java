package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class AddCommand extends BalanceActionCommand {

    public AddCommand(Messages messages, JavaPlugin plugin, BankManager bankManager) {
        super("add", "mortisbank.admin.balance.add", messages, plugin, bankManager, Action.ADD);
    }
}