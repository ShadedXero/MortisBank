package com.mortisdevelopment.mortisbank.personal;

import lombok.Getter;

@Getter
public enum TransactionType {

    DEPOSIT("&a+"),
    WITHDRAW("&c-");

    private final String symbol;

    TransactionType(String symbol) {
        this.symbol = symbol;
    }
}
