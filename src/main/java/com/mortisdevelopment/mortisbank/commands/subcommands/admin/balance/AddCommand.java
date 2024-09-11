package com.mortisdevelopment.mortisbank.commands.subcommands.admin.balance;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscore.messages.Messages;

public class AddCommand extends BalanceActionCommand {

    public AddCommand(Messages messages, BankManager bankManager) {
        super("add", "mortisbank.admin.balance.add", messages, Action.ADD, bankManager);
    }
}