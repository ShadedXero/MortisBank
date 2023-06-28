package com.mortisdevelopment.mortisbank.bank.accounts;

public class AccountSettings {

    private final short defaultAccount;
    private final int backSlot;

    public AccountSettings(short defaultAccount, int backSlot) {
        this.defaultAccount = defaultAccount;
        this.backSlot = backSlot;
    }

    public short getDefaultAccount() {
        return defaultAccount;
    }

    public int getBackSlot() {
        return backSlot;
    }
}
