package com.mortisdevelopment.mortisbank.placeholderapi;

import com.mortisdevelopment.mortisbank.bank.accounts.Account;
import com.mortisdevelopment.mortisbank.bank.personal.PersonalTransaction;
import com.mortisdevelopment.mortiscorespigot.utils.MoneyUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlaceholderAddon extends PlaceholderExpansion {

    private final PlaceholderManager placeholderManager;

    public PlaceholderAddon(@NotNull PlaceholderManager placeholderManager) {
        this.placeholderManager = placeholderManager;
        this.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mortisbank";
    }

    @Override
    public @NotNull String getAuthor() {
        return "com.mortisdevelopment";
    }

    @Override
    public @NotNull String getVersion() {
        return "3.0";
    }

    private String checkDouble(String param, double value) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (param.contains("raw")) {
            return String.valueOf(value);
        }
        if (param.contains("formatted")) {
            return MoneyUtils.getMoney(value);
        }
        return formatter.format(value);
    }

    private String checkInterestTime(String param, OfflinePlayer player) {
        if (param.contains("single")) {
            return placeholderManager.getBankManager().getInterestManager().getInterestTimeSingle(player);
        }
        if (param.contains("multiple")) {
            return placeholderManager.getBankManager().getInterestManager().getInterestTimeMultiple(player);
        }
        return placeholderManager.getBankManager().getInterestManager().getInterestTime(player);
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.contains("max_balance")) {
            return checkDouble(params, placeholderManager.getBankManager().getDataManager().getBalance(player.getUniqueId()));
        }
        if (params.contains("balance")) {
            return checkDouble(params, placeholderManager.getBankManager().getDataManager().getBalance(player.getUniqueId()));
        }
        if (params.contains("account")) {
            Account account = placeholderManager.getBankManager().getAccountManager().getAccount(player);
            if (account == null) {
                return null;
            }
            return account.getId();
        }
        if (params.contains("account_priority")) {
            Account account = placeholderManager.getBankManager().getAccountManager().getAccount(player);
            if (account == null) {
                return null;
            }
            return String.valueOf(account.getPriority());
        }
        if (params.contains("interest_time")) {
            return checkInterestTime(params, player);
        }
        if (params.contains("interest")) {
            Account account = placeholderManager.getBankManager().getAccountManager().getAccount(player);
            if (account == null) {
                return null;
            }
            return checkDouble(params, account.getInterest(placeholderManager.getBankManager().getDataManager().getBalance(player.getUniqueId())));
        }
        if (params.contains("deposit_whole")) {
            return checkDouble(params, placeholderManager.getBankManager().getDepositManager().getWhole(player));
        }
        if (params.contains("deposit_half")) {
            return checkDouble(params, placeholderManager.getBankManager().getDepositManager().getHalf(player));
        }
        if (params.contains("withdraw_everything")) {
            return checkDouble(params, placeholderManager.getBankManager().getWithdrawalManager().getEverything(player));
        }
        if (params.contains("withdraw_half")) {
            return checkDouble(params, placeholderManager.getBankManager().getWithdrawalManager().getHalf(player));
        }
        if (params.contains("withdraw_twenty")) {
            return checkDouble(params, placeholderManager.getBankManager().getWithdrawalManager().getTwentyPercent(player));
        }
        if (params.contains("transaction_")) {
            int index;
            try {
                index = Integer.parseInt(params.replace("transaction_", ""));
            }catch (NumberFormatException exp) {
                return null;
            }
            PersonalTransaction transaction = placeholderManager.getBankManager().getPersonalManager().getTransaction(player, index);
            if (transaction == null) {
                return null;
            }
            return transaction.getTransaction(placeholderManager.getBankManager().getPersonalManager());
        }
        if (params.contains("leaderboard_")) {
//            DecimalFormat formatter = new DecimalFormat("#,###.#");
            int index;
            try {
                index = Integer.parseInt(params.replace("leaderboard_", ""));
            }catch (NumberFormatException exp) {
                return null;
            }
            String leaderboardPlaceholder = placeholderManager.getMessage("LEADERBOARD_PLACEHOLDER");
            UUID uuid = placeholderManager.getBankManager().getDataManager().getUUID(index);
            if (uuid == null) {
                return leaderboardPlaceholder;
            }
            String leaderboard = placeholderManager.getMessage("LEADERBOARD", Bukkit.getOfflinePlayer(uuid));
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName == null) {
                return leaderboardPlaceholder;
            }
//            leaderboard.replace("%player_name%", playerName);
//            leaderboard.replace("%amount%", formatter.format(balance));
//            leaderboard.replace("%amount_raw%", String.valueOf(balance));
//            leaderboard.replace("%amount_formatted%", MoneyUtils.getMoney(balance));
//            leaderboard.getMessage();
            return leaderboard;
        }
        return null;
    }
}
