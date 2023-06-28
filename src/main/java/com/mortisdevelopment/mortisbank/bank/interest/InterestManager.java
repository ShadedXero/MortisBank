package com.mortisdevelopment.mortisbank.bank.interest;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortisbank.bank.accounts.Account;
import com.mortisdevelopment.mortisbank.bank.accounts.AccountManager;
import com.mortisdevelopment.mortisbank.bank.Manager;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import com.mortisdevelopment.mortiscorespigot.utils.MoneyUtils;
import com.mortisdevelopment.mortiscorespigot.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.UUID;

public class InterestManager extends Manager {

    private final MortisBank plugin = MortisBank.getInstance();
    private final AccountManager accountManager;
    private final InterestSettings settings;

    public InterestManager(@NotNull AccountManager accountManager, @NotNull InterestSettings settings) {
        this.accountManager = accountManager;
        this.settings = settings;
        if (!settings.isEnabled()) {
            return;
        }
        InterestMode mode = settings.getMode();
        if (mode.equals(InterestMode.ALL)) {
            checkAll();
        }else {
            checkOnline();
        }
        plugin.getServer().getPluginManager().registerEvents(new InterestListener(this), plugin);
    }

    private void checkAll() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : accountManager.getBankManager().getDataManager().getBalanceByUUID().keySet()) {
                    check(Bukkit.getOfflinePlayer(uuid));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void checkOnline() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (accountManager.getBankManager().getDataManager().getBalanceByUUID().containsKey(player.getUniqueId())) {
                        check(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void check(@NotNull OfflinePlayer player) {
        LocalDateTime time = accountManager.getBankManager().getDataManager().getInterest(player.getUniqueId());
        if (time == null) {
            accountManager.getBankManager().getDataManager().setInterest(player.getUniqueId(), LocalDateTime.now().plusSeconds(settings.getInterval()));
            return;
        }
        if (time.isBefore(LocalDateTime.now())) {
            return;
        }
        if (!giveInterest(player)) {
            return;
        }
        resetInterestTime(player);
    }

    private void sendMessage(@NotNull OfflinePlayer offlinePlayer, String message) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(message);
    }

    public boolean giveInterest(@NotNull OfflinePlayer player) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        Account account = accountManager.getAccount(player);
        if (account == null) {
            return false;
        }
        double balance = accountManager.getBankManager().getDataManager().getBalance(player.getUniqueId());
        if (balance < 0 || account.isFull(balance)) {
            return false;
        }
        double interest = account.getInterest(balance);
        double amount = balance + interest;
        if (!account.isStorable(amount)) {
            return false;
        }
        accountManager.getBankManager().getDataManager().setBalance(player.getUniqueId(), amount);
        MessageUtils utils = new MessageUtils(getMessage("INTEREST", player));
        utils.replace("%money%", formatter.format(amount));
        utils.replace("%money_raw%", String.valueOf(amount));
        utils.replace("%money_formatted%", MoneyUtils.getMoney(amount));
        sendMessage(player, utils.getMessage());
        return true;
    }

    public void resetInterestTime(@NotNull OfflinePlayer player) {
        accountManager.getBankManager().getDataManager().setInterest(player.getUniqueId(), getNewInterestTime());
    }

    public LocalDateTime getNewInterestTime() {
        return LocalDateTime.now().plusSeconds(settings.getInterval());
    }

    public String getInterestTime(@NotNull OfflinePlayer player) {
        InterestDisplayMode displayMode = settings.getDisplayMode();
        if (displayMode.equals(InterestDisplayMode.SINGLE)) {
            return getInterestTimeSingle(player);
        }else {
            return getInterestTimeMultiple(player);
        }
    }

    public String getInterestTimeSingle(@NotNull OfflinePlayer player) {
        LocalDateTime interestTime = accountManager.getBankManager().getDataManager().getInterest(player.getUniqueId());
        if (interestTime == null) {
            interestTime = LocalDateTime.now();
        }
        return TimeUtils.getTime(LocalDateTime.now(), interestTime,
                " " + getMessage("YEARS"),
                "" + getMessage("MONTHS"),
                "" + getMessage("DAYS"),
                "" + getMessage("HOURS"),
                "" + getMessage("MINUTES"),
                "" + getMessage("SECONDS"));
    }

    public String getInterestTimeMultiple(@NotNull OfflinePlayer player) {
        LocalDateTime interestTime = accountManager.getBankManager().getDataManager().getInterest(player.getUniqueId());
        if (interestTime == null) {
            interestTime = LocalDateTime.now();
        }
        return TimeUtils.getTimeMultiple(LocalDateTime.now(), interestTime,
                " " + getMessage("YEARS"),
                "" + getMessage("MONTHS"),
                "" + getMessage("DAYS"),
                "" + getMessage("HOURS"),
                "" + getMessage("MINUTES"),
                "" + getMessage("SECONDS"));
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public InterestSettings getSettings() {
        return settings;
    }
}
