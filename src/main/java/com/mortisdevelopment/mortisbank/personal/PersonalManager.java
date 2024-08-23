package com.mortisdevelopment.mortisbank.personal;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.managers.Manager;
import com.mortisdevelopment.mortiscore.menus.CustomMenu;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class PersonalManager extends Manager {

    private final MortisBank plugin;
    private final CustomMenu menu;

    public PersonalManager(MortisBank plugin, @NotNull CustomMenu menu) {
        this.plugin = plugin;
        this.menu = menu;
    }

    public PersonalTransaction getTransaction(@NotNull OfflinePlayer player, int position) {
        List<PersonalTransaction> transactions = plugin.getDataManager().getTransactions(player.getUniqueId());
        if (position >= transactions.size()) {
            return null;
        }
        return transactions.get(position);
    }
}
