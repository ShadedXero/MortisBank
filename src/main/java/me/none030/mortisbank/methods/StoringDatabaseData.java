package me.none030.mortisbank.methods;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class StoringDatabaseData {

    public static Connection connection;

    public static Connection getConnection() throws SQLException {

        try {
            if (connection != null) {
                return connection;
            }
            File file = new File("plugins/MortisBank/", "config.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = config.getConfigurationSection("config.database");

            assert section != null;
            String url = "jdbc:mysql://" + section.getString("host") + ":" + section.getString("port") + "/" + section.getString("database");
            String user = section.getString("user");
            String password = section.getString("password");

            Connection conn = DriverManager.getConnection(url, user, password);

            connection = conn;

            System.out.println("Connected to database.");

            return connection;
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        return null;
    }

    public static void initializeDatabase() {

        try {
            Statement statement = Objects.requireNonNull(getConnection()).createStatement();

            //Create the player_stats table
            String sql = "CREATE TABLE IF NOT EXISTS BankBalance(uuid varchar(36) primary key, bankBalance double, bankAccount int)";
            String sql2 = "CREATE TABLE IF NOT EXISTS BankInterests(uuid varchar(36) primary key, time varchar(36))";

            statement.execute(sql);
            statement.execute(sql2);

            statement.close();

        } catch (SQLException exp) {
            exp.printStackTrace();
            System.out.println("Could not initialize database.");
        }
    }

    public static void StoreDatabaseData(UUID player, double bankBalance, int bankAccount) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("INSERT INTO BankBalance(uuid, bankBalance, bankAccount) VALUES(?, ?, ?)");
            stmt.setString(1, String.valueOf(player));
            stmt.setDouble(2, bankBalance);
            stmt.setInt(3, bankAccount);
            stmt.execute();

            PreparedStatement stmt2 = Objects.requireNonNull(getConnection()).prepareStatement("INSERT INTO BankInterests(uuid, time) VALUES(?, ?)");
            stmt2.setString(1, String.valueOf(player));
            stmt2.setString(2, LocalDateTime.now().toString());
            stmt2.execute();

        }catch (SQLException exp){
            exp.printStackTrace();
        }
    }

    public static void ChangeDatabaseBalance(UUID player, double bankBalance) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("UPDATE BankBalance SET bankBalance = ? WHERE uuid = ?");
            stmt.setDouble(1, bankBalance);
            stmt.setString(2, String.valueOf(player));
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public static void ChangeDatabaseAccount(UUID player, int bankAccount) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("UPDATE BankBalance SET bankAccount = ? WHERE uuid = ?");
            stmt.setInt(1, bankAccount);
            stmt.setString(2, String.valueOf(player));
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
    }

    public static double getDatabaseBalance(UUID player) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("SELECT bankBalance FROM BankBalance WHERE uuid = ?;");
            stmt.setString(1, String.valueOf(player));
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("bankBalance");
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        return -1;
    }

    public static int getDatabaseAccount(UUID player) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("SELECT bankAccount FROM BankBalance WHERE uuid = ?;");
            stmt.setString(1, String.valueOf(player));
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("bankAccount");
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }
        return 0;
    }

    public static LocalDateTime getDatabaseInterest(UUID player) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("SELECT time FROM BankInterests WHERE uuid = ?;");
            stmt.setString(1, String.valueOf(player));
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return LocalDateTime.parse(resultSet.getString("time"));
            }
        } catch (SQLException exp) {
            exp.printStackTrace();
        }

        return null;
    }

    public static void ChangeDatabaseInterest(UUID player, LocalDateTime time) {

        try {
            PreparedStatement stmt = Objects.requireNonNull(getConnection()).prepareStatement("UPDATE BankInterests SET time = ? WHERE uuid = ?");
            stmt.setString(1, time.toString());
            stmt.setString(2, String.valueOf(player));
            stmt.executeUpdate();
        } catch (SQLException exp) {
            exp.printStackTrace();
        }

    }

}
