package com.mortisdevelopment.mortisbank.placeholders;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.accounts.Account;
import com.mortisdevelopment.mortisbank.transactions.Transaction;
import com.mortisdevelopment.mortiscore.messages.MessageManager;
import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlaceholderManager extends PlaceholderExpansion {

    private final MortisBank plugin;
    private final Messages personalMessages;
    private final Messages placeholderMessages;

    public PlaceholderManager(MortisBank plugin, MessageManager messageManager) {
        this.plugin = plugin;
        this.personalMessages = messageManager.getMessages("personal_messages");
        this.placeholderMessages = messageManager.getMessages("placeholder_messages");
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
            return checkDouble(params.replace("deposit_whole", ""), plugin.getBankManager().getDepositWhole(player));
        }
        if (params.contains("deposit_half")) {
            return checkDouble(params.replace("deposit_half", ""), plugin.getBankManager().getDepositHalf(player));
        }
        if (params.contains("withdraw_everything")) {
            return checkDouble(params.replace("withdraw_everything", ""), plugin.getBankManager().getWithdrawEverything(player));
        }
        if (params.contains("withdraw_half")) {
            return checkDouble(params.replace("withdraw_half", ""), plugin.getBankManager().getWithdrawHalf(player));
        }
        if (params.contains("withdraw_twenty")) {
            return checkDouble(params.replace("withdraw_twenty", ""), plugin.getBankManager().getWithdrawTwentyPercent(player));
        }
        if (params.contains("has_transactions")) {
            if (plugin.getTransactionManager().getTransaction(player, 0) != null) {
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
                Transaction transaction = plugin.getTransactionManager().getTransaction(player, index);
                if (transaction == null) {
                    continue;
                }
                builder.append(transaction.getTransaction(personalMessages));
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
                return "";
            }
            Transaction transaction = plugin.getTransactionManager().getTransaction(player, index);
            if (transaction == null) {
                return "";
            }
            return transaction.getTransaction(personalMessages);
        }
        if (params.contains("leaderboard_")) {
            int index;
            try {
                index = Integer.parseInt(params.replace("leaderboard_", ""));
            }catch (NumberFormatException exp) {
                return null;
            }
            String leaderboardPlaceholder = placeholderMessages.getSimpleMessage("leaderboard_placeholder");
            UUID uuid = plugin.getDataManager().getUUID(index);
            if (uuid == null) {
                return leaderboardPlaceholder;
            }
            String leaderboard = placeholderMessages.getSimplePlaceholderMessage("leaderboard", new Placeholder(Bukkit.getOfflinePlayer(uuid)));
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
