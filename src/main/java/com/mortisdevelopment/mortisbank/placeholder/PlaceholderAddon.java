package com.mortisdevelopment.mortisbank.placeholder;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.personal.PersonalTransaction;
import com.mortisdevelopment.mortiscore.utils.ColorUtils;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlaceholderAddon extends PlaceholderExpansion {

    private final MortisBank plugin;
    private final PlaceholderManager placeholderManager;

    public PlaceholderAddon(MortisBank plugin, @NotNull PlaceholderManager placeholderManager) {
        this.plugin = plugin;
        this.placeholderManager = placeholderManager;
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

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null) {
            return null;
        }
        return getReplacement(offlinePlayer, params);
    }

    public String getReplacement(OfflinePlayer player, String params) {
        if (params.contains("max_balance")) {
            Account account = plugin.getAccountManager().getAccount(player);
            if (account == null) {
                return null;
            }
            return checkDouble(params.replace("max_balance", ""), account.getMaxBalance());
        }
        if (params.contains("balance")) {
            return checkDouble(params.replace("balance", ""), plugin.getDataManager().getBalance(player.getUniqueId()));
        }
        if (params.contains("account_priority")) {
            Account account = plugin.getAccountManager().getAccount(player);
            if (account == null) {
                return null;
            }
            return String.valueOf(account.getPriority());
        }
        if (params.contains("account")) {
            Account account = plugin.getAccountManager().getAccount(player);
            if (account == null) {
                return null;
            }
            return account.getName();
        }
        if (params.contains("deposit_whole")) {
            return checkDouble(params.replace("deposit_whole", ""), plugin.getDepositManager().getWhole(player));
        }
        if (params.contains("deposit_half")) {
            return checkDouble(params.replace("deposit_half", ""), plugin.getDepositManager().getHalf(player));
        }
        if (params.contains("withdraw_everything")) {
            return checkDouble(params.replace("withdraw_everything", ""), plugin.getWithdrawalManager().getEverything(player));
        }
        if (params.contains("withdraw_half")) {
            return checkDouble(params.replace("withdraw_half", ""), plugin.getWithdrawalManager().getHalf(player));
        }
        if (params.contains("withdraw_twenty")) {
            return checkDouble(params.replace("withdraw_twenty", ""), plugin.getWithdrawalManager().getTwentyPercent(player));
        }
        if (params.contains("has_transactions")) {
            if (plugin.getPersonalManager().getTransaction(player, 0) != null) {
                return "true";
            }else {
                return "false";
            }
        }
        if (params.contains("transaction_list_")) {
            int maxIndex;
            try {
                maxIndex = Integer.parseInt(params.replace("transaction_list_", ""));
            }catch (NumberFormatException exp) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < maxIndex; index++) {
                PersonalTransaction transaction = plugin.getPersonalManager().getTransaction(player, index);
                if (transaction == null) {
                    continue;
                }
                builder.append(transaction.getTransaction(plugin.getPersonalManager()));
                if (index < (maxIndex - 1)) {
                    builder.append("\n");
                }
            }
            return builder.toString();
        }
        if (params.contains("transaction_")) {
            int index;
            try {
                index = Integer.parseInt(params.replace("transaction_", ""));
            }catch (NumberFormatException exp) {
                return null;
            }
            PersonalTransaction transaction = plugin.getPersonalManager().getTransaction(player, index);
            if (transaction == null) {
                return null;
            }
            return transaction.getTransaction(plugin.getPersonalManager());
        }
        if (params.contains("leaderboard_")) {
            int index;
            try {
                index = Integer.parseInt(params.replace("leaderboard_", ""));
            }catch (NumberFormatException exp) {
                return null;
            }
            String leaderboardPlaceholder = ColorUtils.color(placeholderManager.getMessage("leaderboard_placeholder"));
            UUID uuid = plugin.getDataManager().getUUID(index);
            if (uuid == null) {
                return leaderboardPlaceholder;
            }
            String leaderboard = ColorUtils.color(placeholderManager.getPlaceholderMessage(Bukkit.getOfflinePlayer(uuid), "leaderboard"));
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName == null) {
                return leaderboardPlaceholder;
            }
            return leaderboard;
        }
        return null;
    }

    public String checkDouble(String param, double value) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        if (param.contains("raw")) {
            return String.valueOf(value);
        }
        if (param.contains("formatted")) {
            return NumberUtils.getMoney(value);
        }
        return formatter.format(value);
    }
}
