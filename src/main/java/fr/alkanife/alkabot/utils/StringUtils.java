package fr.alkanife.alkabot.utils;

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

    public static boolean isNull(String s) {
        if (s == null)
            return true;

        return s.equalsIgnoreCase("");
    }

    public static String offsetToString(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null)
            return null;

        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(offsetDateTime.toInstant().toEpochMilli()));
    }

    public static boolean isURL(@NotNull String s) {
        return s.toLowerCase(Locale.ROOT).startsWith("http");
    }

    /**
     * Convert milliseconds music durations in human-readable time (00:00:00)
     *
     * @param duration the duration
     * @param format   adds some []
     * @param noLimit  if the method should return nothing when the duration is above 20 hours
     * @return the string
     */
    public static @NotNull String durationToString(long duration, boolean format, boolean noLimit) {
        if (!noLimit)
            if (duration >= 72000000) // 20 hours
                return "";

        StringBuilder stringBuilder = new StringBuilder();

        if (format)
            stringBuilder.append("[");

        if (duration >= 3600000) // 1 hour
             stringBuilder.append(String.format("%02d:%02d:%02d",  TimeUnit.MILLISECONDS.toHours(duration),
                     TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                     TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)));
        else
            stringBuilder.append(String.format("%02d:%02d",  TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)));

        if (format)
            stringBuilder.append("]");

        return stringBuilder.toString();
    }
}
