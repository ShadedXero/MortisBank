package com.mortisdevelopment.mortisbank.utils;

import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;

public class BankUtils {

    public static Placeholder getPlaceholder(double amount) {
        Placeholder placeholder = new Placeholder();
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();;
        method.addReplacement("%amount%", NumberUtils.format(amount));
        method.addReplacement("%amount_raw%", String.valueOf(amount));
        method.addReplacement("%amount_formatted%", NumberUtils.getMoney(amount));
        placeholder.addMethod(method);
        return placeholder;
    }
}