package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class SubtractCommand extends BalanceActionCommand {

    public SubtractCommand(Messages messages, JavaPlugin plugin, BankManager bankManager) {
        super("subtract", "mortisbank.admin.balance.subtract", messages, plugin, bankManager, Action.SUBTRACT);
    }
}
