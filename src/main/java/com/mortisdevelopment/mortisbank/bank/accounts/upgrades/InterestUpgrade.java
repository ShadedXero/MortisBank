package com.mortisdevelopment.mortisbank.bank.accounts.upgrades;

public class InterestUpgrade {

    private final double from;
    private final double to;
    private final float percent;

    public InterestUpgrade(double from, double to, float percent) {
        this.from = from;
        this.to = to;
        this.percent = percent;
    }

    public double getRequirement() {
        return to - from;
    }

    public boolean hasRequirement(double balance) {
        return balance >= getRequirement();
    }

    public double getInterest() {
        return (getRequirement() * percent) / 100;
    }

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }

    public float getPercent() {
        return percent;
    }
}
