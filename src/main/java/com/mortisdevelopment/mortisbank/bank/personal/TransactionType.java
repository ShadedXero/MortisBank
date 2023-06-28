package com.mortisdevelopment.mortisbank.bank.personal;

public enum TransactionType {

    DEPOSIT('+'),
    WITHDRAW('-');

    private final char character;

    TransactionType(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}
