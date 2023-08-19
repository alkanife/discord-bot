package fr.alkanife.alkabot.util;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StringUtils {

    /**
     * Reduce a string's length, and add ... at the end
     *
     * Example:
     * It's a beautiful day outside -> It's a beautiful day ou`...`
     *
     * @param value  the original string
     * @param length the length
     * @return the limited string
     */
    public static @NotNull String limitString(@NotNull String value, int length) {
        StringBuilder buf = new StringBuilder(value);
        if (buf.length() > length) {
            buf.setLength(length - 5);
            buf.append("`...`");
        }

        return buf.toString();
    }

    public static String dateToString(Date date) {
        if (date == null)
            return null;

        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(date);
    }

    public static String offsetToString(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null)
            return null;

        return dateToString(new Date(offsetDateTime.toInstant().toEpochMilli()));
    }

    public static boolean isURL(@NotNull String s) {
        return s.toLowerCase(Locale.ROOT).startsWith("http");
    }

    public static boolean endsWithZero(int i) { //what an ugly way
        return Integer.toString(i).endsWith("0");
    }
}
