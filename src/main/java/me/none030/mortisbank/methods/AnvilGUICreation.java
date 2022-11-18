package me.none030.mortisbank.methods;

import me.none030.mortisbank.utils.TransactionType;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.none030.mortisbank.MortisBank.plugin;
import static me.none030.mortisbank.methods.DataMethods.*;
import static me.none030.mortisbank.methods.Depositing.onDeposit;
import static me.none030.mortisbank.methods.MoneyFormat.ConvertMoney;
import static me.none030.mortisbank.methods.Withdrawing.onWithdraw;

public class AnvilGUICreation {

    public static void SendAnvilGUI(Player player, TransactionType type) {

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Enter the amount");
        item.setItemMeta(meta);

        new AnvilGUI.Builder()
                .onComplete((p, text) -> {                                    //called when the inventory output slot is clicked
                    if (text != null) {
                        double money = ConvertMoney(text);
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
                    return AnvilGUI.Response.close();
                })
                .text("Enter the amount")                              //sets the text the GUI should start with
                .itemLeft(item)                      //use a custom item for the first slot
                .title("Enter the amount to " + type.toString().toLowerCase())                                       //set the title of the GUI (only works in 1.14+)
                .plugin(plugin)                                          //set the plugin instance
                .open(player);

    }
}
