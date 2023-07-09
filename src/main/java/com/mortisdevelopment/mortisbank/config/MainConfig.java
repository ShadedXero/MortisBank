package com.mortisdevelopment.mortisbank.config;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.admin.AdminManager;
import com.mortisdevelopment.mortisbank.bank.deposit.DepositManager;
import com.mortisdevelopment.mortisbank.bank.interest.InterestDisplayMode;
import com.mortisdevelopment.mortisbank.bank.interest.InterestManager;
import com.mortisdevelopment.mortisbank.bank.interest.InterestMode;
import com.mortisdevelopment.mortisbank.bank.interest.InterestSettings;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalManager;
import com.mortisdevelopment.mortisbank.bank.withdrawal.WithdrawalManager;
import com.mortisdevelopment.mortisbank.data.DataManager;
import com.mortisdevelopment.mortisbank.data.DatabaseType;
import com.mortisdevelopment.mortisbank.placeholderapi.PlaceholderManager;
import com.mortisdevelopment.mortisbank.utils.GuiSettings;
import com.mortisdevelopment.mortisbank.utils.InputMode;
import com.mortisdevelopment.mortiscorespigot.configs.Config;
import com.mortisdevelopment.mortiscorespigot.databases.*;
import com.mortisdevelopment.mortiscorespigot.menus.Menu;
import com.mortisdevelopment.mortiscorespigot.menus.MenuSound;
import com.mortisdevelopment.mortiscorespigot.utils.TimeUtils;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MainConfig extends Config {

    private final MortisBank plugin = MortisBank.getInstance();
    private final ConfigManager configManager;

    public MainConfig(@NotNull ConfigManager configManager) {
        super("config.yml");
        this.configManager = configManager;
        loadConfig();
    }

    @Override
    public void loadConfig() {
        saveConfig();
        File file = getFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Database database = loadDatabase(config.getConfigurationSection("database"));
        if (database == null) {
            return;
        }
        boolean leaderboard = config.getBoolean("leaderboard");
        int transactionLimit = config.getInt("transaction-limit");
        configManager.getManager().setDataManager(new DataManager(database, leaderboard, transactionLimit));
        InterestSettings settings = loadInterestSettings(config.getConfigurationSection("interests"));
        if (settings == null) {
            return;
        }
        configManager.getManager().setInterestManager(new InterestManager(configManager.getManager().getAccountManager(), settings));
        Menu personalMenu = loadPersonalMenu(config.getConfigurationSection("personal"));
        if (personalMenu == null) {
            return;
        }
        configManager.getManager().setPersonalManager(new PersonalManager(configManager.getManager(), personalMenu));
        GuiSettings guiSettings = loadGuiSettings(config);
        if (guiSettings == null) {
            return;
        }
        Menu depositMenu = loadDepositMenu(config.getConfigurationSection("deposit"));
        if (depositMenu == null) {
            return;
        }
        configManager.getManager().setDepositManager(new DepositManager(configManager.getManager(), depositMenu, guiSettings));
        Menu withdrawalMenu = loadWithdrawalMenu(config.getConfigurationSection("withdrawal"));
        if (withdrawalMenu == null) {
            return;
        }
        configManager.getManager().setWithdrawalManager(new WithdrawalManager(configManager.getManager(), withdrawalMenu, guiSettings));
        configManager.getManager().setPlaceholderManager(new PlaceholderManager(configManager.getManager()));
        configManager.getManager().setAdminManager(new AdminManager(configManager.getManager()));
    }

    private Database loadDatabase(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        DatabaseType type;
        try {
            type = DatabaseType.valueOf(section.getString("type"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        String database = section.getString("database");
        if (database == null) {
            return null;
        }
        String username = section.getString("username");
        if (username == null) {
            return null;
        }
        String password = section.getString("password");
        if (password == null) {
            return null;
        }
        if (type.equals(DatabaseType.H2)) {
            return new H2Database(new File(plugin.getDataFolder() + "/data/", database), username, password);
        }
        String host = section.getString("host");
        int port = section.getInt("port");
        if (type.equals(DatabaseType.SQL)) {
            return new SqlDatabase(host, port, database, username, password);
        }
        if (type.equals(DatabaseType.MYSQL)) {
            return new MySqlDatabase(host, port, database, username, password);
        }
        if (type.equals(DatabaseType.MARIADB)) {
            return new MariaDatabase(host, port, database, username, password);
        }
        if (type.equals(DatabaseType.POSTGRESQL)) {
            return new PostgreSqlDatabase(host, port, database, username, password);
        }
        return null;
    }

    private InterestSettings loadInterestSettings(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        boolean enabled = section.getBoolean("enabled");
        InterestMode mode;
        try {
            mode = InterestMode.valueOf(section.getString("mode"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        InterestDisplayMode displayMode;
        try {
            displayMode = InterestDisplayMode.valueOf(section.getString("display-mode"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        String interval = section.getString("interval");
        if (interval == null) {
            return null;
        }
        long seconds = TimeUtils.getSeconds(interval);
        return new InterestSettings(enabled, mode, displayMode, seconds);
    }

    private GuiSettings loadGuiSettings(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        InputMode mode;
        try {
            mode = InputMode.valueOf(section.getString("input-mode"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        int inputSlot = section.getInt("sign-input-slot");
        String itemId = section.getString("anvil-item");
        if (itemId == null) {
            return null;
        }
        ItemStack item = configManager.getManager().getItemManager().getItem(itemId);
        if (item == null) {
            return null;
        }
        return new GuiSettings(mode, inputSlot, item);
    }

    private Menu loadPersonalMenu(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        Menu menu = loadMenu(section.getConfigurationSection("menu"), configManager.getManager().getItemManager());
        if (menu == null) {
            return null;
        }
        MenuSound sound = loadSound(section.getConfigurationSection("sound"));
        if (sound != null) {
            menu.setSound(sound);
        }
        return menu;
    }

    private Menu loadDepositMenu(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        Menu menu = loadMenu(section.getConfigurationSection("menu"), configManager.getManager().getItemManager());
        if (menu == null) {
            return null;
        }
        MenuSound sound = loadSound(section.getConfigurationSection("sound"));
        if (sound != null) {
            menu.setSound(sound);
        }
        return menu;
    }

    private Menu loadWithdrawalMenu(ConfigurationSection section) {
        if (section == null) {
            return null;
        }
        Menu menu = loadMenu(section.getConfigurationSection("menu"), configManager.getManager().getItemManager());
        if (menu == null) {
            return null;
        }
        MenuSound sound = loadSound(section.getConfigurationSection("sound"));
        if (sound != null) {
            menu.setSound(sound);
        }
        return menu;
    }

    private MenuSound loadSound(ConfigurationSection sound) {
        if (sound == null) {
            return null;
        }
        boolean enabled = sound.getBoolean("enabled");
        if (!enabled) {
            return null;
        }
        Sound type;
        try {
            type = Sound.valueOf(sound.getString("type"));
        }catch (IllegalArgumentException exp) {
            return null;
        }
        int volume = sound.getInt("volume");
        int pitch = sound.getInt("pitch");
        return new MenuSound(type, volume, pitch);
    }
}
