package com.mortisdevelopment.mortisbank.bank.personal;

public enum TransactionType {

    DEPOSIT("&a+"),
    WITHDRAW("&c-");

    private final String symbol;

    TransactionType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
