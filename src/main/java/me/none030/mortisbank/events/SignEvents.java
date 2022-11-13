package me.none030.mortisbank.events;

import me.none030.mortisbank.utils.SignUtils;
import me.none030.mortisbank.utils.TransactionType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

import static me.none030.mortisbank.MortisBank.database;
import static me.none030.mortisbank.MortisBank.getEconomy;
import static me.none030.mortisbank.methods.Depositing.onDeposit;
import static me.none030.mortisbank.methods.MoneyFormat.ConvertMoney;
import static me.none030.mortisbank.methods.SignGUI.Signs;
import static me.none030.mortisbank.methods.StoringDatabaseData.getDatabaseBalance;
import static me.none030.mortisbank.methods.StoringYAMLData.getYamlBalance;
import static me.none030.mortisbank.methods.Withdrawing.onWithdraw;

public class SignEvents implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {

        Player player = e.getPlayer();

        for (SignUtils signUtils : Signs) {
            if (signUtils.getLocation().equals(e.getBlock().getLocation())) {
                if (e.getLine(0) != null && !Objects.equals(e.getLine(0), "")) {
                    double money = ConvertMoney(Objects.requireNonNull(e.getLine(0)));
                    if (money != 0) {
                        Economy economy = getEconomy();
                        double purse = economy.getBalance(player.getName());
                        double bankBalance;
                        if (database) {
                            bankBalance = getDatabaseBalance(player.getUniqueId());
                        } else {
                            bankBalance = getYamlBalance(player.getUniqueId());
                        }

                        if (signUtils.getType().equals(TransactionType.DEPOSIT)) {
                            if (money <= purse) {
                                onDeposit(player, money);
                            } else {
                                onDeposit(player, purse);
                            }
                        } else {
                            if (money <= bankBalance) {
                                onWithdraw(player, money);
                            } else {
                                onWithdraw(player, bankBalance);
                            }
                        }
                    } else {
                        player.sendMessage("§cPlease input a valid number!");
                    }
                } else {
                    player.sendMessage("§cPlease input a valid number!");
                }
                e.getBlock().setType(Material.AIR);
                Signs.remove(signUtils);
                break;
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        for (SignUtils signUtils : Signs) {
            if (player.getUniqueId().equals(signUtils.getPlayer())) {
                signUtils.getLocation().getBlock().setType(Material.AIR);
                Signs.remove(signUtils);
                break;
            }
        }

    }
}
