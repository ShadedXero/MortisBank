package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.admin.AdminManager;
import com.mortisdevelopment.mortisbank.bank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.bank.interest.InterestManager;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalManager;
import com.mortisdevelopment.mortisbank.bank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortisbank.config.ConfigManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.placeholderapi.PlaceholderManager;
import com.mortisdevelopment.mortiscorespigot.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class BankManager {

    private final MortisBank plugin = MortisBank.getInstance();
    private ItemManager itemManager;
    private DataManager dataManager;
    private AccountManager accountManager;
    private PersonalManager personalManager;
    private DepositManager depositManager;
    private WithdrawalManager withdrawalManager;
    private InterestManager interestManager;
    private AdminManager adminManager;
    private PlaceholderManager placeholderManager;
    private ConfigManager configManager;

    public BankManager() {
        this.itemManager = new ItemManager();
        this.configManager = new ConfigManager(this);
        plugin.getServer().getPluginManager().registerEvents(new BankListener(this), plugin);
    }

    public void reload() {
        HandlerList.unregisterAll(plugin);
        Bukkit.getScheduler().cancelTasks(plugin);
        setItemManager(new ItemManager());
        setConfigManager(new ConfigManager(this));
        plugin.getServer().getPluginManager().registerEvents(new BankListener(this), plugin);
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public void setItemManager(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public PersonalManager getPersonalManager() {
        return personalManager;
    }

    public void setPersonalManager(PersonalManager personalManager) {
        this.personalManager = personalManager;
    }

    public DepositManager getDepositManager() {
        return depositManager;
    }

    public void setDepositManager(DepositManager depositManager) {
        this.depositManager = depositManager;
    }

    public WithdrawalManager getWithdrawalManager() {
        return withdrawalManager;
    }

    public void setWithdrawalManager(WithdrawalManager withdrawalManager) {
        this.withdrawalManager = withdrawalManager;
    }

    public InterestManager getInterestManager() {
        return interestManager;
    }

    public void setInterestManager(InterestManager interestManager) {
        this.interestManager = interestManager;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }

    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public void setPlaceholderManager(PlaceholderManager placeholderManager) {
        this.placeholderManager = placeholderManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }
}
