package com.mortisdevelopment.mortisbank.utils;

import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.placeholder.methods.PlayerPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import org.bukkit.OfflinePlayer;

public class BankUtils {

    public static Placeholder getPlaceholder(double amount) {
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%amount%", NumberUtils.format(amount));
        method.addReplacement("%amount_raw%", String.valueOf(amount));
        method.addReplacement("%amount_formatted%", NumberUtils.getMoney(amount));
        return new Placeholder(method);
    }

    public static Placeholder getPlaceholder(OfflinePlayer offlinePlayer, double amount) {
        Placeholder placeholder = new Placeholder(new PlayerPlaceholderMethod(offlinePlayer));
        placeholder.merge(getPlaceholder(amount));
        return placeholder;
    }
}