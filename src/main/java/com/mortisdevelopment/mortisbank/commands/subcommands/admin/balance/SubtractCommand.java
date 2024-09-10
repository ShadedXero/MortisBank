package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.messages.Messages;

public class SubtractCommand extends BalanceActionCommand {

    public SubtractCommand(Messages messages, BankManager bankManager) {
        super("subtract", "mortisbank.balance.subtract", messages, Action.SUBTRACT, bankManager);
    }
}
