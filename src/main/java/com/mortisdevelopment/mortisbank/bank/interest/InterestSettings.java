package com.mortisdevelopment.mortisbank.bank.interest;

import org.jetbrains.annotations.NotNull;

public class InterestSettings {

    private final boolean enabled;
    private final InterestMode mode;
    private final InterestDisplayMode displayMode;
    private final long interval;

    public InterestSettings(boolean enabled, @NotNull InterestMode mode, @NotNull InterestDisplayMode displayMode, long interval) {
        this.enabled = enabled;
        this.mode = mode;
        this.displayMode = displayMode;
        this.interval = interval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public InterestMode getMode() {
        return mode;
    }

    public InterestDisplayMode getDisplayMode() {
        return displayMode;
    }

    public long getInterval() {
        return interval;
    }
}
