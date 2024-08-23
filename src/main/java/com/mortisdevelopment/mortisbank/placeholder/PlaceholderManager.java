package com.mortisdevelopment.mortisbank.placeholder;

import com.mortisdevelopment.mortisbank.MortisBank;
import com.mortisdevelopment.mortiscore.managers.Manager;
import lombok.Getter;

@Getter
public class PlaceholderManager extends Manager {

    public PlaceholderManager(MortisBank plugin) {
        new PlaceholderAddon(plugin, this).register();
    }
}
