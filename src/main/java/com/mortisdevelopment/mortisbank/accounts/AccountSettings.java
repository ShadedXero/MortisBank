package com.mortisdevelopment.mortisbank.accounts;

import lombok.Getter;

@Getter
public class AccountSettings {

    private final short defaultAccount;

    public AccountSettings(short defaultAccount) {
        this.defaultAccount = defaultAccount;
    }
}
