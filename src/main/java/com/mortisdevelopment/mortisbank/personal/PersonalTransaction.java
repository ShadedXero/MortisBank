package com.mortisdevelopment.mortisbank.personal;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.mortisdevelopment.mortiscore.placeholder.Placeholder;
import com.mortisdevelopment.mortiscore.placeholder.methods.ClassicPlaceholderMethod;
import com.mortisdevelopment.mortiscore.utils.TimeUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public class PersonalTransaction {

    private final String rawTransaction;
    private final TransactionType type;
    private final String amount;
    private final LocalDateTime time;
    private final String transactor;

    public PersonalTransaction(@NotNull String rawTransaction) {
        this.rawTransaction = rawTransaction;
        this.type = getType(rawTransaction);
        this.amount = getAmount(rawTransaction);
        this.time = getTime(rawTransaction);
        this.transactor = getTransactor(rawTransaction);
    }

    public PersonalTransaction(@NotNull TransactionType type, @NotNull String amount, @NotNull LocalDateTime time, @NotNull String transactor) {
        this.rawTransaction = getRawTransaction(type, amount, time, transactor);
        this.type = type;
        this.amount = amount;
        this.time = time;
        this.transactor = transactor;
    }

    public PersonalTransaction(@NotNull TransactionType type, @NotNull String amount, @NotNull String transactor) {
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

    private String getDuration(@NotNull PersonalManager personalManager) {
        return TimeUtils.getTime(time, LocalDateTime.now(),
                " " + personalManager.getSimpleMessage("years"),
                " " + personalManager.getSimpleMessage("months"),
                " " + personalManager.getSimpleMessage("days"),
                " " + personalManager.getSimpleMessage("hours"),
                " " + personalManager.getSimpleMessage("minutes"),
                " " + personalManager.getSimpleMessage("seconds"));
    }

    public boolean isValid() {
        return type != null && time != null && transactor != null && amount != null;
    }

    public String getTransaction(@NotNull PersonalManager personalManager) {
        if (!isValid()) {
            return null;
        }
        Placeholder placeholder = new Placeholder();
        ClassicPlaceholderMethod method = new ClassicPlaceholderMethod();
        method.addReplacement("%transaction-type%", type.getSymbol());
        method.addReplacement("%amount%", amount);
        method.addReplacement("%time%", getDuration(personalManager));
        method.addReplacement("%transactor%",transactor);
        placeholder.addMethod(method);
        return placeholder.setPlaceholders(personalManager.getSimpleMessage("transaction"));
    }
}
