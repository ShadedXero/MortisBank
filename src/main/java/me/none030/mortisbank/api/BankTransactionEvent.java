package me.none030.mortisbank.api;

import me.none030.mortisbank.utils.TransactionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BankTransactionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final TransactionType type;
    private final double amount;

    public BankTransactionEvent(Player player, TransactionType type, double amount) {
        this.player = player;
        this.type = type;
        this.amount = amount;
    }


    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
