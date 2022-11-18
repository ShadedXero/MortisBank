package me.none030.mortisbank.methods;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.none030.mortisbank.methods.DataMethods.*;
import static me.none030.mortisbank.methods.Interests.getInterestTime;
import static me.none030.mortisbank.methods.MoneyFormat.formatMoney;
import static me.none030.mortisbank.methods.TransactionsRecords.Transactions;
import static me.none030.mortisbank.methods.TransactionsRecords.getDuration;
import static me.none030.mortisbank.methods.Upgrades.getLimit;

public class PlaceholderAPI extends PlaceholderExpansion implements Listener {

    @Override
    public @NotNull String getAuthor() {
        return "None_123";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mortisbank";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        DecimalFormat value = new DecimalFormat("#,###.#");

        if (params.equalsIgnoreCase("bank_balance")) {
            return value.format(getBankBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("bank_balance_formatted")) {
            return formatMoney(getBankBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("bank_balance_raw")) {
            return String.valueOf(getBankBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("interest_time")) {
            return String.valueOf(getInterestTime(player));
        }
        if (params.equalsIgnoreCase("bank_limit")) {
            return value.format(getLimit(getBankAccount(player.getUniqueId())));
        }
        if (params.equalsIgnoreCase("bank_limit_formatted")) {
            return formatMoney(getLimit(getBankAccount(player.getUniqueId())));
        }
        if (params.equalsIgnoreCase("bank_limit_raw")) {
            return String.valueOf(getLimit(getBankAccount(player.getUniqueId())));
        }
        if (params.equalsIgnoreCase("deposit_whole_purse")) {
            return value.format(getBankPurse(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("deposit_whole_purse_formatted")) {
            return formatMoney(getBankPurse(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("deposit_whole_purse_raw")) {
            return String.valueOf(getBankPurse(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("deposit_half_purse")) {
            return value.format(getBankPurse(player.getUniqueId()) / 2);
        }
        if (params.equalsIgnoreCase("deposit_half_purse_formatted")) {
            return formatMoney(getBankPurse(player.getUniqueId()) / 2);
        }
        if (params.equalsIgnoreCase("deposit_half_purse_raw")) {
            return String.valueOf(getBankPurse(player.getUniqueId()) / 2);
        }
        if (params.equalsIgnoreCase("withdraw_everything_in_account")) {
            return value.format(getBankBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("withdraw_everything_in_account_formatted")) {
            return formatMoney(getBankBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("withdraw_everything_in_account_raw")) {
            return String.valueOf(getBankBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("withdraw_half_the_account")) {
            return value.format(getBankBalance(player.getUniqueId()) / 2);
        }
        if (params.equalsIgnoreCase("withdraw_half_the_account_formatted")) {
            return formatMoney(getBankBalance(player.getUniqueId()) / 2);
        }
        if (params.equalsIgnoreCase("withdraw_half_the_account_raw")) {
            return String.valueOf(getBankBalance(player.getUniqueId()) / 2);
        }
        if (params.equalsIgnoreCase("withdraw_20_the_account")) {
            return value.format(getBankBalance(player.getUniqueId()) * 0.20);
        }
        if (params.equalsIgnoreCase("withdraw_20_the_account_formatted")) {
            return formatMoney(getBankBalance(player.getUniqueId()) * 0.20);
        }
        if (params.equalsIgnoreCase("withdraw_20_the_account_raw")) {
            return String.valueOf(getBankBalance(player.getUniqueId()) * 0.20);
        }
        if (Transactions.containsKey(player.getUniqueId())) {
            List<String> transactions = new ArrayList<>();
            for (String line : Transactions.get(player.getUniqueId())) {
                String timeInString = StringUtils.substringBetween(line, "%", "%");
                LocalDateTime time = LocalDateTime.parse(timeInString);
                String duration = getDuration(time, LocalDateTime.now());
                assert duration != null;
                transactions.add(line.replace(timeInString, "").replace("%%", duration));
            }
            Collections.reverse(transactions);
            try {
                if (params.equalsIgnoreCase("recent_transaction_1")) {
                    return transactions.get(0);
                }
                if (params.equalsIgnoreCase("recent_transaction_2")) {
                    return transactions.get(1);
                }
                if (params.equalsIgnoreCase("recent_transaction_3")) {
                    return transactions.get(2);
                }
                if (params.equalsIgnoreCase("recent_transaction_4")) {
                    return transactions.get(3);
                }
                if (params.equalsIgnoreCase("recent_transaction_5")) {
                    return transactions.get(4);
                }
                if (params.equalsIgnoreCase("recent_transaction_6")) {
                    return transactions.get(5);
                }
                if (params.equalsIgnoreCase("recent_transaction_7")) {
                    return transactions.get(6);
                }
                if (params.equalsIgnoreCase("recent_transaction_8")) {
                    return transactions.get(7);
                }
                if (params.equalsIgnoreCase("recent_transaction_9")) {
                    return transactions.get(8);
                }
                if (params.equalsIgnoreCase("recent_transaction_10")) {
                    return transactions.get(9);
                }
            }catch (IndexOutOfBoundsException exp) {
                return null;
            }
        }

        return null;
    }
}
