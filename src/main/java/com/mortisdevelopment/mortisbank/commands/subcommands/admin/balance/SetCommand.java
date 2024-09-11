package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.messages.Messages;

public class SetCommand extends BalanceActionCommand {

    public SetCommand(Messages messages, BankManager bankManager) {
        super("set", "mortisbank.admin.balance.set", messages, Action.SET, bankManager);
    }
}
