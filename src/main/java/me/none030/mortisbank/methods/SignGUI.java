package me.none030.mortisbank.methods;

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import me.none030.mortisbank.utils.SignUtils;
import me.none030.mortisbank.utils.TransactionType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.none030.mortisbank.MortisBank.plugin;

public class SignGUI {

    public static List<SignUtils> Signs = new ArrayList<>();

    public static void SendSignGUI(Player player, TransactionType type) {

        Location location = player.getLocation();
        location.setY(255);

        Block block = location.getWorld().getBlockAt(location);
        XBlock.setType(block, XMaterial.OAK_SIGN);
        Sign sign = (Sign) location.getWorld().getBlockAt(location).getState();
        sign.setEditable(true);
        sign.setLine(1, "^^^^^^^^^^^^^^^");
        sign.setLine(2, "Enter the amount");
        if (type.equals(TransactionType.DEPOSIT)) {
            sign.setLine(3, "to deposit");
        } else {
            sign.setLine(3, "to withdraw");
        }
        sign.update(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.openSign((Sign) location.getWorld().getBlockAt(location).getState());
            }
        }.runTaskLater(plugin, 20L);
        Signs.add(new SignUtils(player.getUniqueId(), sign.getLocation(), type));
    }
}
