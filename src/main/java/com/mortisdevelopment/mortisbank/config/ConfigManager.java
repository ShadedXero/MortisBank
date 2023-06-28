package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.bank.BankManager;
import com.mortisdevelopment.mortiscorespigot.configs.ItemConfig;

public class ConfigManager {

    private final BankManager manager;
    private final ItemConfig itemConfig;
    private final AccountConfig accountConfig;
    private final MainConfig mainConfig;
    private final MessageConfig messageConfig;

    public ConfigManager(BankManager manager) {
        this.manager = manager;
        this.itemConfig = new ItemConfig("items.yml", manager.getItemManager());
        this.accountConfig = new AccountConfig(this);
        this.mainConfig = new MainConfig(this);
        this.messageConfig = new MessageConfig(this);
    }

    public BankManager getManager() {
        return manager;
    }

    public ItemConfig getItemConfig() {
        return itemConfig;
    }

    public AccountConfig getAccountConfig() {
        return accountConfig;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
