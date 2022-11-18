package me.none030.mortisbank.methods;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormat {

    public static String formatMoney(final double amount) {
        if (amount < 1000.0)
            return format(amount);
        if (amount < 1000000.0)
            return format(amount / 1000.0) + "k";
        if (amount < 1.0E9)
            return format(amount / 1000000.0) + "M";
        if (amount < 1.0E12)
            return format(amount / 1.0E9) + "B";
        if (amount < 1.0E15)
            return format(amount / 1.0E12) + "T";
        if (amount < 1.0E18)
            return format(amount / 1.0E15) + "Q";
        return String.valueOf((long)amount);
    }

    private static String format(final double number) {
        final NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(number);
    }

    public static double ConvertMoney(String value) {

        if (value.contains("k")) {
            try {
                return Double.parseDouble(value.replace("k", "")) * 1000;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("K")) {
            try {
                return Double.parseDouble(value.replace("K", "")) * 1000;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("m")) {
            try {
                return Double.parseDouble(value.replace("m", "")) * 1000000;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("M")) {
            try {
                return Double.parseDouble(value.replace("M", "")) * 1000000;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("b")) {
            try {
                return Double.parseDouble(value.replace("b", "")) * 1000000000;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("B")) {
            try {
                return Double.parseDouble(value.replace("B", "")) * 1000000000;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("t")) {
            try {
                return Double.parseDouble(value.replace("t", "")) * 1000000000000.0;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }
        if (value.contains("T")) {
            try {
                return Double.parseDouble(value.replace("T", "")) * 1000000000000.0;
            } catch (NumberFormatException exp) {
                return 0;
            }
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exp) {
            return 0;
        }
    }
}
