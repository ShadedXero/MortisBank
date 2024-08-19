package com.mortisdevelopment.mortisbank.accounts;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class Account {

    private final String id;
    private final short priority;
    private final String name;
    private final double maxBalance;

    public Account(@NotNull String id, short priority, @NotNull String name, double maxBalance) {
        this.id = id;
        this.priority = priority;
        this.name = name;
        this.maxBalance = maxBalance;
    }

    public boolean isPriority(short priority) {
        return this.priority == priority;
    }

    public boolean isStorable(double amount) {
        return amount <= maxBalance;
    }

    public boolean isFull(double amount) {
        return amount >= maxBalance;
    }

    public double getSpace(double amount) {
        return maxBalance - amount;
    }
}
