package dev.alkanife.alkabot.util.timetracker;

import dev.alkanife.alkabot.Alkabot;

import java.util.HashMap;
import java.util.UUID;

public class TimeTracker {

    public static boolean isTracking = false;
    private static final HashMap<String, Long> trackLog = new HashMap<>();

    public static void start(String key) {
        if (isTracking && key != null)
            trackLog.put(key, System.currentTimeMillis());
    }

    public static String startUnique(String key) {
        if (!isTracking || key == null)
            return null;

        String k = "[" + UUID.randomUUID() + "] " + key;
        start(k);
        return k;
    }

    public static void end(String key) {
        if (isTracking && key != null) {
            long startTime = trackLog.get(key);

            if (startTime != 0) {
                String displayKey = key;

                if (key.startsWith("["))
                    displayKey = key.substring(39);

                // The Yellow color used here may not completely work with some terminals. #TODO
                Alkabot.getInstance().getLogger().info("\u001B[0;33m{} = {}ms\u001B[0m", displayKey, System.currentTimeMillis() - startTime);
                trackLog.remove(key);
            }
        }
    }

    public static void kill(String key) {
        if (isTracking && key != null)
            trackLog.remove(key);
    }
}
