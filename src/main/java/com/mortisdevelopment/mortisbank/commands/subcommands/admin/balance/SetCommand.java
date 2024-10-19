package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class SetCommand extends BalanceActionCommand {

    public SetCommand(Messages messages, JavaPlugin plugin, BankManager bankManager) {
        super("set", "mortisbank.admin.balance.set", messages, plugin, bankManager, Action.SET);
    }
}
