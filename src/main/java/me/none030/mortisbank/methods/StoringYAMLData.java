package me.none030.mortisbank.methods;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class StoringYAMLData {

    public static void StoreYamlData(UUID player, double bankBalance, int bankAccount) {

        try {
            File file = new File("plugins/MortisBank/", "data.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection dataSection = data.getConfigurationSection("data");
            ConfigurationSection interestsSection = data.getConfigurationSection("interests");
            assert dataSection != null && interestsSection != null;

            dataSection.set(player.toString(), bankBalance + "," + bankAccount);
            interestsSection.set(player.toString(), LocalDateTime.now().toString());
            data.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public static void ChangeYamlBalance(UUID player, double bankBalance) {

        try {
            File file = new File("plugins/MortisBank/", "data.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = data.getConfigurationSection("data");
            assert section != null;

            String[] raw = Objects.requireNonNull(section.getString(player.toString())).split(",");
            section.set(player.toString(), bankBalance + "," + raw[1]);
            data.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public static void ChangeYamlAccount(UUID player, int bankAccount) {

        try {
            File file = new File("plugins/MortisBank/", "data.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = data.getConfigurationSection("data");
            assert section != null;

            String[] raw = Objects.requireNonNull(section.getString(player.toString())).split(",");
            section.set(player.toString(), raw[0] + "," + bankAccount);
            data.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    public static double getYamlBalance(UUID player) {

        File file = new File("plugins/MortisBank/", "data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = data.getConfigurationSection("data");
        assert section != null;

        if (section.contains(player.toString())) {
            String[] raw = Objects.requireNonNull(section.getString(player.toString())).split(",");
            return Double.parseDouble(raw[0]);
        }

        return -1;
    }

    public static int getYamlAccount(UUID player) {

        File file = new File("plugins/MortisBank/", "data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = data.getConfigurationSection("data");
        assert section != null;

        if (section.contains(player.toString())) {
            String[] raw = Objects.requireNonNull(section.getString(player.toString())).split(",");
            return Integer.parseInt(raw[1]);
        }

        return 0;

    }

    public static LocalDateTime getYamlInterest(UUID player) {

        File file = new File("plugins/MortisBank/", "data.yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = data.getConfigurationSection("interests");
        assert section != null;

        if (section.contains(player.toString())) {
            return LocalDateTime.parse(Objects.requireNonNull(section.getString(player.toString())));
        }

        return null;
    }

    public static void ChangeYamlInterest(UUID player, LocalDateTime time) {

        try {
            File file = new File("plugins/MortisBank/", "data.yml");
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            data.set(player.toString(), time);
            data.save(file);
        }catch (IOException exp) {
            exp.printStackTrace();
        }
    }
}
