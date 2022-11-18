package me.none030.mortisbank.methods;

import me.none030.mortisbank.utils.TransactionType;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.none030.mortisbank.MortisBank.plugin;
import static me.none030.mortisbank.methods.DataMethods.*;
import static me.none030.mortisbank.methods.Depositing.onDeposit;
import static me.none030.mortisbank.methods.MoneyFormat.ConvertMoney;
import static me.none030.mortisbank.methods.Withdrawing.onWithdraw;

public class SignGUI {

    public static void SendSignGUI(Player player, TransactionType type){
        SignGUIAPI.builder()
                .action(e -> {
                    Player p = e.getPlayer();
                    if (e.getLines().get(0) != null) {
                        double money = ConvertMoney(e.getLines().get(0));
                        if (money != 0) {
                            double purse = getBankPurse(p.getUniqueId());
                            double bankBalance = getBankBalance(p.getUniqueId());

                            if (type.equals(TransactionType.DEPOSIT)) {
                                onDeposit(p, Math.min(money, purse));
                            } else {
                                onWithdraw(p, Math.min(money, bankBalance));
                            }
                        } else {
                            p.sendMessage("§cPlease input a valid number!");
                        }
                    } else {
                        p.sendMessage("§cPlease input a valid number!");
                    }
                })
                .withLines(Arrays.asList("", "^^^^^^^^^^^^^^^", "Enter the amount", "to " + type.toString().toLowerCase()))
                .uuid(player.getUniqueId())
                .instance(plugin)
                .build()
                .open();
    }
}
