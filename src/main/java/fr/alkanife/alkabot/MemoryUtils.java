package fr.alkanife.alkabot;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class MemoryUtils {

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemory() {
        return getMaxMemory() - getFreeMemory();
    }

    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

}
