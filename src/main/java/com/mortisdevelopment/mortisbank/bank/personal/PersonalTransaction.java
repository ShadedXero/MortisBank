package com.mortisdevelopment.mortisbank.bank.personal;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.mortisdevelopment.mortiscorespigot.utils.MessageUtils;
import com.mortisdevelopment.mortiscorespigot.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

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
        try {
            return TransactionType.valueOf(type);
        }catch (IllegalArgumentException exp) {
            return null;
        }
    }

    private String getAmount(@NotNull String rawTransaction) {
        DecimalFormat formatter = new DecimalFormat("#,###.#");
        String amount = StringUtils.substringBetween(rawTransaction, "(", ")");
        Double value;
        try {
            value = Double.parseDouble(amount);
        }catch (NullPointerException | NumberFormatException ignored) {
            value = null;
        }
        if (value == null) {
            return null;
        }
        return formatter.format(value);
    }

    private LocalDateTime getTime(@NotNull String rawTransaction) {
        String time = StringUtils.substringBetween(rawTransaction, "{", "}");
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
                personalManager.getMessage("TRANSACTION_TIME_YEAR"),
                personalManager.getMessage("TRANSACTION_TIME_MONTH"),
                personalManager.getMessage("TRANSACTION_TIME_DAY"),
                personalManager.getMessage("TRANSACTION_TIME_HOUR"),
                personalManager.getMessage("TRANSACTION_TIME_MINUTE"),
                personalManager.getMessage("TRANSACTION_TIME_SECOND"));
    }

    public boolean isValid() {
        return type != null && time != null && transactor != null && amount != null;
    }

    public String getTransaction(@NotNull PersonalManager personalManager) {
        if (!isValid()) {
            return null;
        }
        MessageUtils utils = new MessageUtils(personalManager.getMessage("TRANSACTION"));
        utils.replace("%transaction-type%", String.valueOf(type.getCharacter()));
        utils.replace("%amount%", amount);
        utils.replace("%time%", getDuration(personalManager));
        utils.replace("%transactor%", transactor);
        return utils.getMessage();
    }

    public String getRawTransaction() {
        return rawTransaction;
    }

    public TransactionType getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getTransactor() {
        return transactor;
    }
}
