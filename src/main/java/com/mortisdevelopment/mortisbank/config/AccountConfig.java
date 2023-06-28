package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.bank.accounts.Account;
import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.accounts.AccountSettings;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.AccountRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.ItemRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.MoneyRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.requirements.PermissionRequirement;
import com.mortisdevelopment.mortisbank.bank.accounts.upgrades.InterestUpgrade;
import com.mortisdevelopment.mortiscorespigot.configs.Config;
import com.mortisdevelopment.mortiscorespigot.menus.Menu;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AccountConfig extends Config {

    private final ConfigManager configManager;

    public AccountConfig(@NotNull ConfigManager configManager) {
        super("accounts.yml");
        this.configManager = configManager;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        saveConfig();
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Menu menu = loadMenu(config.getConfigurationSection("menu"), configManager.getManager().getItemManager());
        if (menu == null) {
            return;
        }
        AccountSettings settings = loadSettings(config);
        if (settings == null) {
            return;
        }
        configManager.getManager().setAccountManager(new AccountManager(configManager.getManager(), menu, settings));
        loadAccounts(config.getConfigurationSection("accounts"));
    }

    private AccountSettings loadSettings(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        short defaultAccount = (short) section.getInt("default-account");
        int backSlot = section.getInt("back-slot");
        return new AccountSettings(defaultAccount, backSlot);
    }

    private void loadAccounts(ConfigurationSection accounts) {
        if (accounts == null) {
            return;
        }
        for (String id : accounts.getKeys(false)) {
            ConfigurationSection section = accounts.getConfigurationSection(id);
            if (section == null) {
                continue;
            }
            short priority = (short) section.getInt("priority");
            String name = MessageUtils.color(section.getString("name"));
            String iconId = section.getString("icon");
            if (iconId == null) {
                continue;
            }
            ItemStack icon = configManager.getManager().getItemManager().getItem(iconId);
            if (icon == null) {
                continue;
            }
            int iconSlot = section.getInt("icon-slot");
            double maxBalance = section.getDouble("max-balance");
            List<InterestUpgrade> upgrades = new ArrayList<>();
            ConfigurationSection upgradeSection = section.getConfigurationSection("upgrades");
            if (upgradeSection != null) {
                ConfigurationSection interests = upgradeSection.getConfigurationSection("interests");
                if (interests != null) {
                    for (String ignored : interests.getKeys(false)) {
                        ConfigurationSection interestSection = interests.getConfigurationSection(id);
                        if (interestSection == null) {
                            continue;
                        }
                        double from = interestSection.getDouble("from");
                        double to = interestSection.getDouble("to");
                        float percent = interestSection.getInt("percent");
                        InterestUpgrade interestUpgrade = new InterestUpgrade(from, to, percent);
                        upgrades.add(interestUpgrade);
                    }
                }
            }
            List<AccountRequirement> requirements = new ArrayList<>();
            ConfigurationSection requirementSection = section.getConfigurationSection("requirement");
            if (requirementSection != null) {
                if (requirementSection.contains("money")) {
                    double money = requirementSection.getDouble("money");
                    MoneyRequirement requirement = new MoneyRequirement(money);
                    requirements.add(requirement);
                }
                if (requirementSection.contains("permissions")) {
                    List<String> permissions = requirementSection.getStringList("permissions");
                    for (String permission : permissions) {
                        PermissionRequirement requirement = new PermissionRequirement(permission);
                        requirements.add(requirement);
                    }
                }
                if (requirementSection.contains("items")) {
                    List<String> rawItemIds = requirementSection.getStringList("items");
                    for (String rawItemId : rawItemIds) {
                        String[] raw = rawItemId.split(":");
                        int amount;
                        try {
                            amount = Integer.parseInt(raw[1]);
                        }catch (NumberFormatException exp) {
                            continue;
                        }
                        ItemStack item = configManager.getManager().getItemManager().getItem(raw[0]);
                        if (item == null) {
                            continue;
                        }
                        ItemRequirement requirement = new ItemRequirement(item, amount);
                        requirements.add(requirement);
                    }
                }
            }
            Account account = new Account(id, priority, name, icon, iconSlot, maxBalance, upgrades, requirements);
            configManager.getManager().getAccountManager().getAccounts().add(account);
        }
    }
}
