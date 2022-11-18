package me.none030.mortisbank.methods;

import me.none030.mortisbank.utils.TransactionType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

public class TransactionsRecords {

    public static HashMap<UUID, List<String>> Transactions = new HashMap<>();

    public static void StoreTransaction(TransactionType type, double amount, Player player, String name) {
        String symbol = null;
        if (type.equals(TransactionType.DEPOSIT)) {
            symbol = "§a+";
        }
        if (type.equals(TransactionType.WITHDRAW)) {
            symbol = "§c-";
        }
        DecimalFormat value = new DecimalFormat("#,###.#");
        LocalDateTime localtime = LocalDateTime.now();
        String time = "%" + localtime + "%";

        String transaction = symbol + " §6" + value.format(amount) + "§7, §e" + time + " §7by " + name;

        if (Transactions.containsKey(player.getUniqueId())) {
            List<String> transactions = new ArrayList<>(Transactions.get(player.getUniqueId()));
            if (transactions.size() >= 10) {
                transactions.remove(0);
            }
            transactions.add(transaction);
            Transactions.put(player.getUniqueId(), transactions);
        } else {
            Transactions.put(player.getUniqueId(), Collections.singletonList(transaction));
        }
    }

    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    public static String getDuration(LocalDateTime from, LocalDateTime to) {

        Period period = getPeriod(from, to);
        long[] time = getTime(from, to);

        if (period.getDays() != 0) {
            if (period.getDays() == 1) {
                return period.getDays() + " day ago";
            } else {
                return period.getDays() + " days ago";
            }
        }
        if (time[0] != 0) {
            if (time[0] == 1) {
                return time[0] + " hour ago";
            } else {
                return time[0] + " hours ago";
            }
        }
        if (time[1] != 0) {
            if (time[1] == 1) {
                return time[1] + " min ago";
            } else {
                return time[1] + " mins ago";
            }
        }
        if (time[2] != 0) {
            if (time[2] == 1) {
                return time[2] + " sec ago";
            } else {
                return time[2] + " secs ago";
            }
        }

        return null;
    }

    public static long getInterestDuration(LocalDateTime from, LocalDateTime to) {

        File file = new File("plugins/MortisBank/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int interestTime = config.getInt("config.interests.interest-time");
        Period period = getPeriod(from, to);
        long[] time = getTime(from, to);
        int hours = 0;

        if (period.getDays() >= 1) {
            hours = hours + period.getDays() * 24;
        }
        if (time[0] != 0) {
            hours = (int) (hours + time[0]);
        }

        if (hours >= interestTime) {
            return 0;
        } else {
            return interestTime - hours;
        }
    }

    public static boolean isAboveTime(LocalDateTime from, LocalDateTime to) {

        File file = new File("plugins/MortisBank/", "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        int interestTime = config.getInt("config.interests.interest-time");
        Period period = getPeriod(from, to);
        long[] time = getTime(from, to);
        int hours = 0;

        if (period.getDays() >= 1) {
            hours = hours + period.getDays() * 24;
        }
        if (time[0] != 0) {
            hours = (int) (hours + time[0]);
        }

        return hours > interestTime;
    }

    private static Period getPeriod(LocalDateTime dob, LocalDateTime now) {
        return Period.between(dob.toLocalDate(), now.toLocalDate());
    }

    private static long[] getTime(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        long seconds = duration.getSeconds();

        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        long secs = (seconds % SECONDS_PER_MINUTE);

        return new long[]{hours, minutes, secs};
    }
}
