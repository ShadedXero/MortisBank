package com.mortisdevelopment.mortisbank.transactions;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.NumberUtils;
import com.mortisdevelopment.mortiscore.utils.TimeUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public class Transaction {

    public enum TransactionType {
        DEPOSIT,
        WITHDRAW
    }
    private final Messages messages;
    private final String string;

    public Transaction(@NotNull String string) {
        this.string = string;
    }

    public Transaction(Messages messages, @NotNull TransactionType type, double amount, @NotNull LocalDateTime time, @NotNull String transactor) {
        this.string = toString(messages, type, amount, time, transactor);
    }

    public Transaction(Messages messages, @NotNull TransactionType type, double amount, @NotNull String transactor) {
        this(messages, type, amount, LocalDateTime.now(), transactor);
    }

    private String toString(Messages messages, @NotNull TransactionType type, double amount, @NotNull LocalDateTime time, @NotNull String transactor) {
        String message = switch (type) {
            case DEPOSIT -> messages.getSimpleMessage("deposit_transaction");
            case WITHDRAW -> messages.getSimpleMessage("withdraw_transaction");
        };
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%amount%", NumberUtils.format(amount));
        method.addReplacement("%transactor%", transactor);
        method.addReplacement("%time%", "%time%" + time + "%time%");
        return new Placeholder(method).setPlaceholders(message);
    }

    private LocalDateTime getTime(@NotNull String rawTransaction) {
        String time = StringUtils.substringBetween(rawTransaction, "%time%", "%time%");
        if (time == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(time);
        }catch (DateTimeParseException exp) {
            return null;
        }
    }

    private String getDuration(LocalDateTime time) {
        return TimeUtils.getTime(time, LocalDateTime.now(),
                " " + messages.getSimpleMessage("years"),
                " " + messages.getSimpleMessage("months"),
                " " + messages.getSimpleMessage("days"),
                " " + messages.getSimpleMessage("hours"),
                " " + messages.getSimpleMessage("minutes"),
                " " + messages.getSimpleMessage("seconds"));
    }

    public String toString() {
        LocalDateTime time = getTime(string);
        String duration = getDuration(time);
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%time%", getDuration(messages));
        return new Placeholder(method).setPlaceholders(messages.getSimpleMessage("transaction"));
    }
}