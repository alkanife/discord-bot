package dev.alkanife.alkabot.util;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

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

    public static boolean isURL(@NotNull String s) {
        try {
            new URL(s).toURI();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean endsWithZero(int i) { //what an ugly way
        return Integer.toString(i).endsWith("0");
    }
}
