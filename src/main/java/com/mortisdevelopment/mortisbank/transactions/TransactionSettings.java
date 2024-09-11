package com.mortisdevelopment.mortisbank.transactions;

import lombok.Getter;

@Getter
public class TransactionSettings {

    private final int transactionLimit;

    public TransactionSettings(int transactionLimit) {
        this.transactionLimit = transactionLimit;
    }
}