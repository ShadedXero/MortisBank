package com.mortisdevelopment.mortisbank.transactions;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.mortisdevelopment.mortiscore.messages.Messages;
import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
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
    private final String rawTransaction;
    private final TransactionType type;
    private final String amount;
    private final LocalDateTime time;
    private final String transactor;

    public Transaction(@NotNull String rawTransaction) {
        this.rawTransaction = rawTransaction;
        this.type = getType(rawTransaction);
        this.amount = getAmount(rawTransaction);
        this.time = getTime(rawTransaction);
        this.transactor = getTransactor(rawTransaction);
    }

    public Transaction(@NotNull TransactionType type, @NotNull String amount, @NotNull LocalDateTime time, @NotNull String transactor) {
        this.rawTransaction = getRawTransaction(type, amount, time, transactor);
        this.type = type;
        this.amount = amount;
        this.time = time;
        this.transactor = transactor;
    }

    public Transaction(@NotNull TransactionType type, @NotNull String amount, @NotNull String transactor) {
        this.rawTransaction = getRawTransaction(type, amount, LocalDateTime.now(), transactor);
        this.type = type;
        this.amount = amount;
        this.time = LocalDateTime.now();
        this.transactor = transactor;
    }

    private String getRawTransaction(@NotNull TransactionType type, @NotNull String amount, @NotNull LocalDateTime time, @NotNull String transactor) {
        return "%" + type.name() + "%" +
                "(" + amount + ")" +
                "{" + time + "}" +
                "<" + transactor + ">";
    }

    private TransactionType getType(@NotNull String rawTransaction) {
        String type = StringUtils.substringBetween(rawTransaction, "%", "%");
        if (type == null) {
            return null;
        }
        try {
            return TransactionType.valueOf(type);
        }catch (IllegalArgumentException exp) {
            return null;
        }
    }

    private String getAmount(@NotNull String rawTransaction) {
        return StringUtils.substringBetween(rawTransaction, "(", ")");
    }

    private LocalDateTime getTime(@NotNull String rawTransaction) {
        String time = StringUtils.substringBetween(rawTransaction, "{", "}");
        if (time == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(time);
        }catch (DateTimeParseException exp) {
            return null;
        }
    }

    private String getTransactor(@NotNull String rawTransaction) {
        return StringUtils.substringBetween(rawTransaction, "<", ">");
    }

    private String getDuration(@NotNull Messages messages) {
        return TimeUtils.getTime(time, LocalDateTime.now(),
                " " + messages.getSimpleMessage("years"),
                " " + messages.getSimpleMessage("months"),
                " " + messages.getSimpleMessage("days"),
                " " + messages.getSimpleMessage("hours"),
                " " + messages.getSimpleMessage("minutes"),
                " " + messages.getSimpleMessage("seconds"));
    }

    public boolean isValid() {
        return type != null && time != null && transactor != null && amount != null;
    }

    public String getTransaction(@NotNull Messages messages) {
        if (!isValid()) {
            return null;
        }
        Placeholder placeholder = new Placeholder();
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%transaction-type%", type.getSymbol());
        method.addReplacement("%amount%", amount);
        method.addReplacement("%time%", getDuration(messages));
        method.addReplacement("%transactor%",transactor);
        placeholder.addMethod(method);
        return placeholder.setPlaceholders(messages.getSimpleMessage("transaction"));
    }
}
