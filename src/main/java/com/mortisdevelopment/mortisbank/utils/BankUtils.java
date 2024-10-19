package com.mortisdevelopment.mortisbank.utils;

import com.mortisdevelopment.mortiscore.placeholders.Placeholder;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import org.bukkit.OfflinePlayer;

public class BankUtils {

    public static Placeholder getPlaceholder(double amount) {
        Placeholder placeholder = new Placeholder();
        placeholder.addReplacement("%amount%", NumberUtils.format(amount));
        placeholder.addReplacement("%amount_raw%", String.valueOf(amount));
        placeholder.addReplacement("%amount_formatted%", NumberUtils.getMoney(amount));
        return placeholder;
    }

    public static Placeholder getPlaceholder(OfflinePlayer offlinePlayer, double amount) {
        Placeholder placeholder = new Placeholder(offlinePlayer);
        placeholder.addPlaceholder(getPlaceholder(amount));
        return placeholder;
    }
}