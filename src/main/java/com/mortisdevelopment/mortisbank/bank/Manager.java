package com.mortisdevelopment.mortisbank.bank;

import com.mortisdevelopment.mortiscorespigot.managers.CoreManager;
import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Manager extends CoreManager {

    public String getMessage(String id) {
        return new MessageUtils(getMessage(id)).setPlaceholders();
    }

    public String getMessage(String id, Player player) {
        return new MessageUtils(getMessage(id)).setPlaceholders(player);
    }

    public String getMessage(String id, OfflinePlayer player) {
        return new MessageUtils(getMessage(id)).setPlaceholders(player);
    }

    public void sendMessage(String id, Player player, OfflinePlayer target) {
        player.sendMessage(new MessageUtils(getMessage(id)).setPlaceholders(target));
    }

    public void sendMessage(String id, Player player) {
        player.sendMessage(new MessageUtils(getMessage(id)).setPlaceholders(player));
    }
}
