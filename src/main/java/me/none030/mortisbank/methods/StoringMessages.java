package me.none030.mortisbank.methods;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class StoringMessages {

    public static String DepositMessage = null;
    public static String WithdrawMessage = null;
    public static String MaxCoinsMessage = null;
    public static String DepositLittleAmountMessage = null;
    public static String WithdrawLittleAmountMessage = null;
    public static String InterestMessage = null;

    public static void StoreMessages() {

        File file = new File("plugins/MortisBank/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("config.messages");
        assert section != null;

        DepositMessage = section.getString("deposit").replace("&", "§");
        WithdrawMessage = section.getString("withdraw").replace("&", "§");
        MaxCoinsMessage = section.getString("max-coins").replace("&", "§");
        DepositLittleAmountMessage = section.getString("deposit-little-amount").replace("&", "§");
        WithdrawLittleAmountMessage = section.getString("withdraw-little-amount").replace("&", "§");
        InterestMessage = section.getString("interest").replace("&", "§");

    }
}
