package me.none030.mortisbank.utils;

import org.bukkit.Location;

import java.util.UUID;

public class SignUtils {

    private UUID player;
    private Location location;
    private TransactionType type;

    public SignUtils(UUID player, Location location, TransactionType type) {
        this.player = player;
        this.location = location;
        this.type = type;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
