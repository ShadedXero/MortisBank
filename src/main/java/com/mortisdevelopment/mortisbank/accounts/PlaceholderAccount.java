package com.mortisdevelopment.mortisbank.accounts;

import com.mortisdevelopment.mortiscore.exceptions.ConfigException;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.objects.abstracts.PlaceholderStringAbstract;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlaceholderAccount extends PlaceholderStringAbstract<Account> {

    private AccountManager accountManager;

    public PlaceholderAccount(Account originalObject) {
        super(originalObject, null);
    }

    public PlaceholderAccount(String rawObject, AccountManager accountManager) throws ConfigException {
        super(rawObject);
        this.accountManager = accountManager;
    }

    @Override
    protected Account getObjectAbstract(Placeholder placeholder) {
        return accountManager.getAccount(placeholder.setPlaceholders(getRawObject()));
    }

    @Override
    public PlaceholderAccount clone() {
        PlaceholderAccount clone = (PlaceholderAccount) super.clone();
        clone.accountManager = accountManager;
        return clone;
    }
}